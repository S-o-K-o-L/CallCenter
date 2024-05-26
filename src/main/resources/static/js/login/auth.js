
function handleLogin(event) {
    event.preventDefault();

    // Get user input
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    localStorage.setItem("username", username);
    localStorage.setItem("password", password);

    const user = {
        username: username,
        password: password
    };

    fetch('http://localhost:8080/auth', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    }).then(response => {
        if (!response.ok) {
            alert('Login and / or password is incorrect');
        }
        return response.json();
    }).then(data => {
        localStorage.setItem('token', data.token);

        const userRole = data.role.find(role => role.authority === "ROLE_USER");
        if (userRole) {
            window.location.href = 'index.html'
            console.log("User Role: ", userRole.authority);
        } else {
            console.log("User Role not found");
            window.location.href = 'index.html'
        }

        const consultantRole = data.role.find(role => role.authority === "ROLE_CONSULTANT");
        if (consultantRole) {
            window.location.href = 'consultant.html'
            console.log("Consultant Role: ", consultantRole.authority);
        } else {
            console.log("Consultant Role not found");
        }

        const adminRole = data.role.find(role => role.authority === "ROLE_ADMIN");
        if (adminRole) {
            window.location.href = 'admin.html'
            console.log("Admin Role: ", adminRole.authority);
        } else {
            console.log("Admin Role not found");
        }


    }).catch(error => {
        console.error('POST request error', error);
    });
}

const loginForm = document.getElementById("loginForm");
loginForm.addEventListener("submit", handleLogin);
