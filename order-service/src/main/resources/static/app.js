const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8084/delivery-update'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/deliveryStatus', (statusUpdate) => {
        const deliveryStatus = JSON.parse(statusUpdate.body);
        updateDeliveryProgress(deliveryStatus);
        console.log('Subscribed!')
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function updateDeliveryProgress(deliveryStatus) {
    const progressElement = document.getElementById("progress");
    const statusMessageElement = document.getElementById("status-message");
    const statusImageElement = document.getElementById("status-image");

    progressElement.style.width = deliveryStatus.progress + '%';
    progressElement.innerText = deliveryStatus.progress + '%';

    statusMessageElement.innerText = deliveryStatus.message;
    statusImageElement.src = deliveryStatus.imageUrl;
    statusImageElement.style.display = "block";
}

$(function () {
    connect();

    $(window).on('beforeunload', function() {
        disconnect();
    });
});


