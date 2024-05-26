document.addEventListener('DOMContentLoaded', function () {
    // Function to fetch and display users
    function fetchUsers() {
        fetch('http://localhost:8080/admin', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem("token"),
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(data => {
                // Clear previous list
                document.getElementById('userList').innerHTML = '';
                // Iterate through users and create list items
                data.forEach(user => {
                    const listItem = document.createElement('li');
                    listItem.textContent = user.name;

                    // Create dropdown for specialties
                    const dropdown = document.createElement('select');
                    user.specialties.forEach(spec => {
                        const option = document.createElement('option');
                        option.value = spec;
                        option.textContent = spec;
                        dropdown.appendChild(option);
                    });

                    // Event listener for dropdown change
                    dropdown.addEventListener('change', function () {
                        // Remove the user from the list
                        listItem.remove();
                        // Send request to backend with user info
                        const roomName = generateRoomName(); // Function to generate random room name
                        const requestBody = {
                            user: user,
                            roomName: roomName
                        };
                        fetch('http://localhost:8080/admin', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify(requestBody)
                        })
                            .then(response => {
                                // Handle response
                                console.log('User removed and room created:', roomName);
                            })
                            .catch(error => {
                                console.error('Error:', error);
                            });
                    });

                    listItem.appendChild(dropdown);
                    document.getElementById('userList').appendChild(listItem);
                });
            })
            .catch(error => {
                console.error('Error fetching users:', error);
            });
    }

    // Initial fetch
    fetchUsers();

    // Function to generate random room name
    function generateRoomName() {
        const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        const length = 10;
        let result = '';
        for (let i = 0; i < length; i++) {
            result += characters.charAt(Math.floor(Math.random() * characters.length));
        }
        return result;
    }
});