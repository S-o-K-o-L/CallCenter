const LOCAL_IP_ADDRESS = "127.0.0.1";

const getElement = id => document.getElementById(id);
const [btnConnect, btnToggleVideo, btnToggleAudio, roomDiv, localVideo, remoteVideo] = ["btnConnect",
    "toggleVideo", "toggleAudio", "roomDiv",
    "localVideo", "remoteVideo"].map(getElement);
let remoteDescriptionPromise, sendChannel, receiveChannel, roomName, localStream, remoteStream,
    rtcPeerConnection, isCaller, infoDiv;

const iceServers = {
    iceServers: [
        {urls: `stun:${LOCAL_IP_ADDRESS}:3478`},
        {
            urls: `turn:${LOCAL_IP_ADDRESS}:3478`,
            username: "username",
            credential: "password"
        }
    ]
};

const eventSource = new EventSource('http://localhost:8080/admins/stream-delete');

eventSource.onmessage = function (event) {
    console.log('Получено обновление:', event.data);
    if (JSON.parse(event.data).body === "Deleted") {
        window.location.href = "start.html";
        remoteVideo.srcObject = null;
        isCaller = true;
    }
};

const input = document.createElement('input');

eventSource.onerror = function (error) {
    console.error('Ошибка при подписке на события SSE:', error);
};

const streamConstraints = {audio: true, video: true};

let socket = io.connect(`http://${LOCAL_IP_ADDRESS}:8000`, {secure: true});


btnToggleVideo.addEventListener("click", () => toggleTrack("video"));
btnToggleAudio.addEventListener("click", () => toggleTrack("audio"));

function toggleTrack(trackType) {
    if (!localStream) {
        return;
    }

    const track = trackType === "video" ? localStream.getVideoTracks()[0]
        : localStream.getAudioTracks()[0];
    const enabled = !track.enabled;
    track.enabled = enabled;

    const toggleButton = getElement(
        `toggle${trackType.charAt(0).toUpperCase() + trackType.slice(1)}`);
    const icon = getElement(`${trackType}Icon`);
    toggleButton.classList.toggle("disabled-style", !enabled);
    toggleButton.classList.toggle("enabled-style", enabled);
    icon.classList.toggle("bi-camera-video-fill",
        trackType === "video" && enabled);
    icon.classList.toggle("bi-camera-video-off-fill",
        trackType === "video" && !enabled);
    icon.classList.toggle("bi-mic-fill", trackType === "audio" && enabled);
    icon.classList.toggle("bi-mic-mute-fill", trackType === "audio" && !enabled);
}

const user = {
    username: localStorage.getItem("username"),
    password: localStorage.getItem("password"),
    room: localStorage.getItem("room")
};

window.onload = () => {
    roomName = localStorage.getItem("room");
    socket.emit("joinRoom", roomName, user);
    // divRoomConfig.classList.add("d-none");
    roomDiv.classList.remove("d-none");
};

const handleSocketEvent = (eventName, callback) => socket.on(eventName,
    callback);

handleSocketEvent("created", e => {
    navigator.mediaDevices.getUserMedia(streamConstraints).then(stream => {
        localStream = stream;
        localVideo.srcObject = stream;
        isCaller = true;
    }).catch(console.error);

    infoDiv = document.createElement('div');
    infoDiv.id = "info";
    infoDiv.classList.add('dropdown');
    infoDiv.textContent = "Ожидайте подключения консультанта";
    infoDiv.classList.add('list-item');
    roomDiv.appendChild(infoDiv);
});

handleSocketEvent("joined", e => {
    navigator.mediaDevices.getUserMedia(streamConstraints).then(stream => {
        localStream = stream;
        localVideo.srcObject = stream;
        socket.emit("ready", roomName);
    }).catch(console.error);

});

handleSocketEvent("candidate", e => {
    if (rtcPeerConnection) {
        const candidate = new RTCIceCandidate({
            sdpMLineIndex: e.label, candidate: e.candidate,
        });

        rtcPeerConnection.onicecandidateerror = (error) => {
            console.error("Error adding ICE candidate: ", error);
        };

        if (remoteDescriptionPromise) {
            remoteDescriptionPromise
                .then(() => {
                    if (candidate != null) {
                        return rtcPeerConnection.addIceCandidate(candidate);
                    }
                })
                .catch(error => console.log(
                    "Error adding ICE candidate after remote description: ", error));
        }
    }
});

handleSocketEvent("ready", e => {
    if (isCaller) {
        rtcPeerConnection = new RTCPeerConnection(iceServers);
        rtcPeerConnection.onicecandidate = onIceCandidate;
        rtcPeerConnection.ontrack = onAddStream;
        rtcPeerConnection.addTrack(localStream.getTracks()[0], localStream);
        rtcPeerConnection.addTrack(localStream.getTracks()[1], localStream);

        sendChannel = rtcPeerConnection.createDataChannel("sendChannel");
        sendChannel.onmessage = function(event) {
            console.log("Received message:", event.data);
        };

        sendChannel = rtcPeerConnection.createDataChannel("sendChannel");
        sendChannel.onmessage = event => {
            showMessage(event.data);
        };
        sendChannel.onopen = () => {
            showMessage('Message channel opened');
        };
        sendChannel.onclose = () => {
            showMessage('Message channel closed');
        };

        rtcPeerConnection.ondatachannel = event => {
            sendChannel = event.channel;
            sendChannel.onmessage = event => {
                showMessage(event.data);
            };
        };

        rtcPeerConnection
            .createOffer()
            .then(sessionDescription => {
                rtcPeerConnection.setLocalDescription(sessionDescription);
                socket.emit("offer", {
                    type: "offer", sdp: sessionDescription, room: roomName,
                });
            })
            .catch(error => console.log(error));
    }
});

handleSocketEvent("offer", e => {
    if (!isCaller) {
        rtcPeerConnection = new RTCPeerConnection(iceServers);
        rtcPeerConnection.onicecandidate = onIceCandidate;
        rtcPeerConnection.ontrack = onAddStream;
        rtcPeerConnection.addTrack(localStream.getTracks()[0], localStream);
        rtcPeerConnection.addTrack(localStream.getTracks()[1], localStream);

        sendChannel = rtcPeerConnection.createDataChannel("sendChannel");
        sendChannel.onmessage = event => {
            showMessage(event.data);
        };
        sendChannel.onopen = () => {
            showMessage('Message channel opened');
        };
        sendChannel.onclose = () => {
            showMessage('Message channel closed');
        };
        rtcPeerConnection.ondatachannel = event => {
            sendChannel = event.channel;
            sendChannel.onmessage = event => {
                showMessage(event.data);
            };
        };


        if (rtcPeerConnection.signalingState === "stable") {
            remoteDescriptionPromise = rtcPeerConnection.setRemoteDescription(
                new RTCSessionDescription(e));
            remoteDescriptionPromise
                .then(() => {
                    return rtcPeerConnection.createAnswer();
                })
                .then(sessionDescription => {
                    rtcPeerConnection.setLocalDescription(sessionDescription);
                    socket.emit("answer", {
                        type: "answer", sdp: sessionDescription, room: roomName,
                    });
                })
                .catch(error => console.log(error));
        }
    }
});

handleSocketEvent("answer", e => {
    if (isCaller && rtcPeerConnection.signalingState === "have-local-offer") {
        infoDiv = document.getElementById('info');
        infoDiv.style.display = 'none';


        remoteDescriptionPromise = rtcPeerConnection.setRemoteDescription(
            new RTCSessionDescription(e));
        remoteDescriptionPromise.catch(error => console.log(error));
    }
});

handleSocketEvent("userDisconnected", (e) => {
    remoteVideo.srcObject = null;
    isCaller = true;
});

handleSocketEvent("setCaller", callerId => {
    isCaller = socket.id === callerId;
});

handleSocketEvent("full", e => {
    alert("room is full!");
    window.location.reload();
});

const onIceCandidate = e => {
    if (e.candidate) {
        console.log("sending ice candidate");
        socket.emit("candidate", {
            type: "candidate",
            label: e.candidate.sdpMLineIndex,
            id: e.candidate.sdpMid,
            candidate: e.candidate.candidate,
            room: roomName,
        });
    }
}

const onAddStream = e => {
    remoteVideo.srcObject = e.streams[0];
    remoteStream = e.stream;
}

function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const message = messageInput.value;
    messageInput.value = '';

    showMessage('You: ' + message);

    sendChannel.send(message);
}

function showMessage(message) {
    const messagesDiv = document.getElementById('messages');
    const messageElement = document.createElement('div');
    messageElement.textContent = message;
    messagesDiv.appendChild(messageElement);
}
