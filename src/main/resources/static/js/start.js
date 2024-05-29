function handleStart(event) {
    event.preventDefault();
    window.location.href = "index.html";
}

const startBtn = document.getElementById("startBtn");
startBtn.addEventListener("click", handleStart);