let specialties = [];



function updateUsers(users) {
    document.getElementById('userList').innerHTML = '';
    users.forEach(user => {
        const listItem = document.createElement('li');
        listItem.textContent = user.username + " " + user.room;
        const dropdown = document.createElement('select');
        specialties.forEach(s => {
            const option = document.createElement('option');
            option.textContent = s;
            dropdown.appendChild(option);
        });
        listItem.appendChild(dropdown);

        const button = document.createElement('button');
        button.textContent = 'Отправить';
        button.addEventListener('click', function() {
            const userData = {
                sessionId: user.sessionId,
                username: user.username,
                room: user.room,
                spec: dropdown.value
            };
            sendDataToServer(userData);
            fetch('http://localhost:8080/delete_users', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem("token"),
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    // Удаление элемента списка после успешной отправки данных
                    listItem.remove();
                })
                .catch(error => {
                    console.error('Error sending data to server:', error);
                });
        });
        listItem.appendChild(button);

        document.getElementById('userList').appendChild(listItem);
    });
}

function sendDataToServer(userData) {
    fetch('http://localhost:8080/consultant', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("token"),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    })
        .then(response => {
            if (response.ok) {
                console.log('Данные успешно отправлены на сервер');
            } else {
                console.error('Произошла ошибка при отправке данных на сервер');
            }
        })
        .catch(error => {
            console.error('Произошла ошибка при отправке данных на сервер:', error);
        });
}

function fetchUsers() {
    fetch('http://localhost:8080/get_users',
        {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("token"),
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            let users = data;
            fetchSpec()
                .then(data => {
                    specialties = data;
                    updateUsers(users);
                })
                .catch(err => console.error("Error fetch"));

        })
        .catch(error => {
            console.error('There was a problem with your fetch operation:', error);
        });
}

function fetchSpec() {
    return fetch('http://localhost:8080/get_spec',
        {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("token"),
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            let elementToRemove = "NO_SPEC";
            let index = data.indexOf(elementToRemove);
            if (index !== -1) {
                data.splice(index, 1);
            }
            return data;
        })
        .catch(error => {
            console.error('There was a problem with your fetch operation:', error);
        });
}


window.onload = fetchUsers;

const eventSource = new EventSource('http://localhost:8080/admins/stream');

eventSource.onmessage = function (event) {
    console.log('Получено обновление:', event.data);
    updateUsers(JSON.parse(event.data).body);
};

eventSource.onerror = function (error) {
    console.error('Ошибка при подписке на события SSE:', error);
};