let consultantList = []
function fetchAndDisplayConsultants() {
    fetch('http://localhost:8080/admins/get_consultant', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("token"),
            'Content-Type': 'application/json'
        }})
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            consultantList = data;
            displayConsultants(consultantList);
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}

function displayConsultants(consultantList) {
    const consultantListElement = document.getElementById('consultantList');
    if (!consultantListElement) {
        console.error('Consultant list element not found');
        return;
    }
    consultantListElement.innerHTML = '';
    consultantList.forEach(consultant => {
        const listItem = document.createElement('li');
        const specs = consultant.specs.map(spec => spec.spec).join(', ');
        listItem.textContent = `${consultant.username} - Specialization: ${specs}`


        const addRoleBtn = document.createElement('button');
        addRoleBtn.textContent = 'Добавить специализацию';
        addRoleBtn.addEventListener('click', () => {
            displayCheckList(addRoleBtn);
        });

        const removeRoleBtn = document.createElement('button');
        removeRoleBtn.textContent = 'Удалить специализацию';
        removeRoleBtn.addEventListener('click', () => {
            displayCheckList(addRoleBtn);
        });

        const buttonContainer = document.createElement('div');
        buttonContainer.appendChild(addRoleBtn);
        buttonContainer.appendChild(removeRoleBtn);
        listItem.appendChild(buttonContainer);

        consultantListElement.appendChild(listItem);
    });
}




function displayCheckList() {


    specialties.forEach((spec, index) => {
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.id = 'specialization' + index;
        checkbox.value = 'specialization' + index;

        const label = document.createElement('label');
        label.htmlFor = 'specialization' + index;
        label.textContent = spec;

        specializationList.appendChild(checkbox);
        specializationList.appendChild(label);
        specializationList.appendChild(document.createElement('br'));
    });

    addRoleBtn.addEventListener('click', () => {
        specializationList.style.display = 'block';
        saveBtn.style.display = 'inline-block'; // Показываем кнопку сохранить
        cancelBtn.style.display = 'inline-block'; // Показываем кнопку отмена
    });

    saveBtn.addEventListener('click', () => {
        const selectedSpecializations = Array.from(specializationList.querySelectorAll('input[type="checkbox"]:checked')).map(checkbox => checkbox.value);
        console.log(selectedSpecializations); // Здесь можно выполнить логику сохранения выбранных специализаций
        resetForm();
    });

    cancelBtn.addEventListener('click', () => {
        resetForm();
    });

    function resetForm() {
        specializationList.style.display = 'none';
        saveBtn.style.display = 'none';
        cancelBtn.style.display = 'none';
        specializationList.querySelectorAll('input[type="checkbox"]')
            .forEach(checkbox => checkbox.checked = false);
    }

    addRoleBtn.insertAdjacentElement('afterend', specializationList);
    specializationList.insertAdjacentElement('afterend', saveBtn);
    saveBtn.insertAdjacentElement('afterend', cancelBtn);
}


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
            fetch('http://localhost:8080/admins/delete_users', {
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
    fetch('http://localhost:8080/admins/get_users',
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

fetchAndDisplayConsultants();

const button = document.getElementById('addRoleBtn');
const saveBtn = document.createElement('button');
saveBtn.textContent = 'Сохранить';
saveBtn.style.display = 'none'; // Сначала скрываем кнопку сохранить

const cancelBtn = document.createElement('button');
cancelBtn.textContent = 'Отмена';
cancelBtn.style.display = 'none'; // Сначала скрываем кнопку отмена


specializationList.style.display = 'none'; // Сначала скрываем чек-лист специализаций

const eventSource = new EventSource('http://localhost:8080/admins/stream');

eventSource.onmessage = function (event) {
    console.log('Получено обновление:', event.data);
    updateUsers(JSON.parse(event.data).body);
};

eventSource.onerror = function (error) {
    console.error('Ошибка при подписке на события SSE:', error);
};