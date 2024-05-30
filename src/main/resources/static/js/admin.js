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


function sentToConsul(selectedSpecializations) {
    fetch('http://localhost:8080/update_spec', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("token"),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(selectedSpecializations)
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

function displayConsultants(consultantList) {
    const consultantListElement = document.getElementById('consultantList');
    if (!consultantListElement) {
        console.error('Consultant list element not found');
        return;
    }
    consultantListElement.innerHTML = '';
    consultantList.forEach(consultant => {
        const listItem = document.createElement('div');
        listItem.style.marginBottom = '10px';
        let specs = consultant.specs.map(spec => spec.spec).join(', ');
        listItem.textContent = `${consultant.username} - Specialization: ${specs}`;

        const saveBtn = document.createElement('button');
        saveBtn.textContent = 'Сохранить';
        saveBtn.style.display = 'none';

        const cancelBtn = document.createElement('button');
        cancelBtn.textContent = 'Отмена';
        cancelBtn.style.display = 'none';

        const specializationList = document.createElement('div')
        specializationList.style.display = 'none';

        const addRoleBtn = document.createElement('button');
        addRoleBtn.textContent = 'Редактировать специализации';

        let isSpecializationListDisplayed = false;

        addRoleBtn.addEventListener('click', () => {
            function displayCheckList() {
                if (!isSpecializationListDisplayed) {
                    specialties.forEach((spec, index) => {
                        const checkbox = document.createElement('input');
                        checkbox.type = 'checkbox';
                        checkbox.id = 'specialization' + index;
                        checkbox.value = spec;

                        if (specs.includes(checkbox.value)) {
                            checkbox.checked = true;
                        }

                        const label = document.createElement('label');
                        label.htmlFor = 'specialization' + index;
                        label.textContent = spec;

                        specializationList.appendChild(checkbox);
                        specializationList.appendChild(label);
                        specializationList.appendChild(document.createElement('br'));
                    });
                }

                addRoleBtn.style.display = 'none';
                specializationList.style.display = 'block';
                saveBtn.style.display = 'inline-block';
                cancelBtn.style.display = 'inline-block';

                addRoleBtn.insertAdjacentElement('afterend', specializationList);
                specializationList.insertAdjacentElement('afterend', saveBtn);
                saveBtn.insertAdjacentElement('afterend', cancelBtn);
            }
                displayCheckList();
                isSpecializationListDisplayed = true;
        });

        saveBtn.addEventListener('click', () => {
            addRoleBtn.style.display = 'inline-block';
            console.log('Кнопка включена');
            consultant.specs = Array.from(specializationList
                .querySelectorAll('input[type="checkbox"]:checked'))
                .map(checkbox => checkbox.value);
            specs = consultant.specs.join(', ');
            console.log(consultant);
            sentToConsul(consultant);
            listItem.textContent = `${consultant.username} - Specialization: ${specs}`;
            const buttonContainer = document.createElement('div');
            buttonContainer.appendChild(addRoleBtn);
            listItem.appendChild(buttonContainer);
            resetForm();
        });

        cancelBtn.addEventListener('click', () => {
            addRoleBtn.style.display = 'inline-block';
            resetForm();
        });

        function resetForm() {

            specializationList.style.display = 'none';
            saveBtn.style.display = 'none';
            cancelBtn.style.display = 'none';
            specializationList.querySelectorAll('input[type="checkbox"]')
                .forEach(checkbox => {
                    if (specs.includes(checkbox.value)) {
                        checkbox.checked = true;
                    }
                });
        }

        const buttonContainer = document.createElement('div');
        buttonContainer.appendChild(addRoleBtn);
        listItem.appendChild(buttonContainer);
        consultantListElement.appendChild(listItem);
    });
}

let specialties = [];

function updateUsers(users) {

    document.getElementById('userList').innerHTML = '';
    users.forEach(user => {

        const listItem = document.createElement('div');
        listItem.classList.add('list-item');

        const userDiv = document.createElement('div');
        userDiv.classList.add('dropdown');
        userDiv.textContent = "Имя - " + user.username;
        listItem.appendChild(userDiv);

        const roomDiv = document.createElement('div');
        roomDiv.classList.add('dropdown');
        roomDiv.textContent = "Комната - " + user.room;
        listItem.appendChild(roomDiv);

        const dropdown = document.createElement('select');

        dropdown.classList.add('dropdown');
        specialties.forEach(s => {
            const option = document.createElement('option');
            option.textContent = s;
            dropdown.appendChild(option);
        });
        listItem.appendChild(dropdown);

        const button = document.createElement('button');
        button.classList.add('button');
        button.textContent = 'Отправить';

        button.addEventListener('click', function() {
            const userData = {
                sessionId: user.sessionId,
                username: user.username,
                room: user.room,
                spec: dropdown.value
            };
            sendDataToServer(userData);
            listItem.remove();
        });

        const buttonDel = document.createElement('button');
        buttonDel.classList.add('button');
        buttonDel.textContent = 'Удалить';

        buttonDel.addEventListener('click', function() {
            const userData = {
                sessionId: user.sessionId
            };
            deleteUserFromServer(userData);
            listItem.remove();
        });

        listItem.appendChild(button);
        listItem.appendChild(buttonDel);

        document.getElementById('userList').appendChild(listItem);
    });
}

function deleteUserFromServer(userData) {
    fetch('http://localhost:8080/admins/delete_users', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("token"),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData.sessionId)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            // Удаление элемента списка после успешной отправки данных

        })
        .catch(error => {
            console.error('Error sending data to server:', error);
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

const eventSource = new EventSource('http://localhost:8080/admins/stream');

eventSource.onmessage = function (event) {
    console.log('Получено обновление:', event.data);
    updateUsers(JSON.parse(event.data).body);
};

eventSource.onerror = function (error) {
    console.error('Ошибка при подписке на события SSE:', error);
};