function handleStart(event) {
    event.preventDefault();

    localStorage.setItem("room", generateRoomName());

    const user = {
        username: localStorage.getItem("username"),
        password: localStorage.getItem("password"),
        room: localStorage.getItem("room")
    };

    fetch('http://localhost:8080/connect', {
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

function generateRoomName() {
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const length = 10;
    let result = '';
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * characters.length));
    }
    return result;
}

const startBtn = document.getElementById("startBtn");
startBtn.addEventListener("click", handleStart);