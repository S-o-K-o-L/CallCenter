function handleRegistration(event) {
    event.preventDefault();

    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const passwordConfirm = document.getElementById("passwordConfirm").value;

    const user = {
        username: username,
        password: password,
        passwordConfirm: passwordConfirm,
        email: email,
    };

    fetch('http://localhost:8080/registration', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    }).then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response;
    }).then(() => {
        localStorage.setItem("connectedUser", JSON.stringify(user));
        window.location.href = "index.html";
    }).catch(error => {
        console.error('POST request error:', error);
    });

}

// Attach the handleRegistration function to the form's submit event
const registrationForm = document.getElementById("registrationForm");
registrationForm.addEventListener("submit", handleRegistration);