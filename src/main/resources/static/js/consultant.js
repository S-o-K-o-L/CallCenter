let consultant;
let users;

const user = {
    username: "Alex",
    password: 1
};

function displayConsultantQueue(consultant) {
    const info = document.getElementById('consInfo');
    document.getElementById('consInfo').innerHTML='';
    const nameDiv = document.createElement('div');
    nameDiv.classList.add('list-container');
    nameDiv.classList.add('dropdown');
    nameDiv.textContent = "Имя - " + consultant.username;
    info.appendChild(nameDiv);

    const specsDiv = document.createElement('div');
    specsDiv.classList.add('list-container');
    specsDiv.classList.add('dropdown');
    let specs = consultant.specs.map(spec => spec).join(', ');
    specsDiv.textContent = `Specialization: ${specs}`;
    info.appendChild(specsDiv);

    document.getElementById('cons').innerHTML = '';
    consultant.specs.forEach(spec => {
        const list = document.createElement('div');
        list.classList.add('list-container');
        list.id = spec;
        list.textContent = "Очередь " + spec;

        document.getElementById('cons').appendChild(list);
    });
}

function fetchAndDisplayConsultant() {
    fetch('http://localhost:8080/consultant/get_cons', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem("token"),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            consultant = data;
            displayConsultantQueue(consultant);
            fetchAndDisplayUserInCons();
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}

function sendUserUpdateToServer(user) {
    fetch('http://localhost:8080/consultant/del_cons_user', {
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
            users = data;
            fillQueue(users);
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}

function fillQueue(users) {
    users.forEach(user => {
        let spec = user.specs.at(0);
        const list = document.getElementById(spec);
        if (list) {
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

            const button = document.createElement('button');
            button.classList.add('button');
            button.textContent = 'Подключиться';

            button.addEventListener('click', function () {
                sendUserUpdateToServer(user);
                localStorage.setItem("room", user.room);
                listItem.remove();
                window.location.href = "index.html";
            });

            const buttonDel = document.createElement('button');
            buttonDel.classList.add('button');
            buttonDel.textContent = 'Удалить';

            buttonDel.addEventListener('click', function () {
                const userData = {
                    sessionId: user.sessionId
                };
                deleteUserFromServer(userData);
                listItem.remove();
            });

            listItem.appendChild(button);
            listItem.appendChild(buttonDel);

            list.appendChild(listItem);
        }
    });
}

function fetchAndDisplayUserInCons() {
    fetch('http://localhost:8080/consultant/get_cons_user', {
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
            users = data;
            fillQueue(users);
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}

fetchAndDisplayConsultant();

