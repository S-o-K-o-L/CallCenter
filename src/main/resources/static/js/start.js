function handleStart(event) {
    event.preventDefault();
    localStorage.setItem("room", generateRoomName());
    window.location.href = "index.html";
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