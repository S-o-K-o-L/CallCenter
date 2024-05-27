// Function to fetch and display users
function updateUsers(users) {
    document.getElementById('userList').innerHTML = '';
    users.forEach(user => {
        const listItem = document.createElement('li');
        listItem.textContent = user.username + " " + user.room;
        const dropdown = document.createElement('select');
        let spec;
        fetchSpec()
            .then(spec =>
                spec.forEach(s => {
                const option = document.createElement('option');
                option.textContent = s;
                dropdown.appendChild(option);
            }))
            .catch(error =>
                console.error('There was a problem with your fetch operation:', error));
        listItem.appendChild(dropdown);
        document.getElementById('userList').appendChild(listItem);
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
            updateUsers(data);
            setTimeout(fetchUsers, 5000);
        })
        .catch(error => {
            console.error('There was a problem with your fetch operation:', error);
            setTimeout(fetchUsers, 5000);
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

function generateRoomName() {
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const length = 10;
    let result = '';
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * characters.length));
    }
    return result;
}
