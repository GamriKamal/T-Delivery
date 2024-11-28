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

let pathCoordinatesAB = [];
let pathCoordinatesBC = [];
let totalDistanceAB, totalDistanceBC;
const pauseTime = 10 * 1000;
let distanceTraveled = 0;
let waypoint = {}

function initializeMapAndSimulation(deliveryStatus) {
    const origin = { lat: deliveryStatus.courierLat, lng: deliveryStatus.courierLng };
    waypoint = { lat: deliveryStatus.restaurantLat, lng: deliveryStatus.restaurantLng };
    const destination = { lat: deliveryStatus.userLat, lng: deliveryStatus.userLng };

    map = new google.maps.Map(document.getElementById("map"), {
        center: origin,
        zoom: 12,
    });

    const directionsService = new google.maps.DirectionsService();
    const directionsRendererAB = new google.maps.DirectionsRenderer({ suppressMarkers: true });
    const directionsRendererBC = new google.maps.DirectionsRenderer({ suppressMarkers: true });
    directionsRendererAB.setMap(map);
    directionsRendererBC.setMap(map);

    new google.maps.Marker({ position: origin, map: map, title: "Courier" });
    new google.maps.Marker({ position: waypoint, map: map, title: "Restaurant" });
    new google.maps.Marker({ position: destination, map: map, title: "User" });

    directionsService.route({
        origin: origin,
        destination: waypoint,
        travelMode: google.maps.TravelMode.DRIVING,
    }, (result, status) => {
        if (status === google.maps.DirectionsStatus.OK) {
            directionsRendererAB.setDirections(result);
            totalDistanceAB = computeTotalDistance(result);
            pathCoordinatesAB = extractPathCoordinates(result);
            startSimulationAB(deliveryStatus);
        }
    });

    directionsService.route({
        origin: waypoint,
        destination: destination,
        travelMode: google.maps.TravelMode.DRIVING,
    }, (result, status) => {
        if (status === google.maps.DirectionsStatus.OK) {
            directionsRendererBC.setDirections(result);
            totalDistanceBC = computeTotalDistance(result);
            pathCoordinatesBC = extractPathCoordinates(result);
        }
    });
}

function startSimulationAB(deliveryStatus) {
    courierMarker = new google.maps.Marker({
        position: origin,
        map: map,
        title: "Courier",
        icon: "https://maps.google.com/mapfiles/kml/shapes/truck.png",
    });

    runSimulation(deliveryStatus.timeToRestaurant * 1000, pathCoordinatesAB, totalDistanceAB, () => {
        setTimeout(startSimulationBC(deliveryStatus), pauseTime);
    });
}

function startSimulationBC(deliveryStatus) {
    courierMarker.setPosition(waypoint);
    console.log(deliveryStatus.timeToUser + " " + deliveryStatus.timeToRestaurant)
    runSimulation(deliveryStatus.timeToUser * 1000, pathCoordinatesBC, totalDistanceBC, () => {
        console.log("The route is completed.");
        sendUpdate(deliveryStatus.orderId)
    });
}

function runSimulation(simulationTime, pathCoordinates, segmentDistance, onComplete) {
    let progress = 0;
    const startTime = Date.now();
    const interval = 1000;

    const simulationInterval = setInterval(() => {
        const elapsedTime = Date.now() - startTime;
        progress = elapsedTime / simulationTime;

        if (progress >= 1) {
            clearInterval(simulationInterval);
            progress = 1;
            if (onComplete) {
                onComplete();
            }
        }

        const newPosition = interpolatePosition(pathCoordinates, progress);
        updateCourierPosition(newPosition.lat(), newPosition.lng(), segmentDistance);
    }, interval);
}

function interpolatePosition(path, progress) {
    const index = Math.floor(progress * (path.length - 1));
    return path[index];
}

function updateCourierPosition(lat, lng, segmentDistance) {
    const courierPosition = new google.maps.LatLng(lat, lng);
    const previousPosition = courierMarker.getPosition();

    if (previousPosition) {
        const distanceIncrement = google.maps.geometry.spherical.computeDistanceBetween(previousPosition, courierPosition);
        distanceTraveled += distanceIncrement;
    }

    courierMarker.setPosition(courierPosition);

    updateProgressBar(distanceTraveled, totalDistanceAB + totalDistanceBC);
}

function updateProgressBar(currentDistance, totalDistance) {
    const progressPercent = Math.min((currentDistance / totalDistance) * 100, 100);
    const progressBar = document.getElementById("progress");
    progressBar.style.width = progressPercent + "%";
    progressBar.textContent = Math.round(progressPercent) + "%";
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

    if (deliveryStatus.showMap) {
        document.getElementById("map").style.display = "block";
        initializeMapAndSimulation(deliveryStatus);
    } else {
        document.getElementById("map").style.display = "none";
    }
}

function sendUpdate(orderId) {
    const messageBody = {
        id: orderId,
        status: "DELIVERED"
    };

    console.log(JSON.stringify(messageBody))

    stompClient.publish({
        destination: "/app/hello",
        body: JSON.stringify(messageBody)
    });
}

function extractPathCoordinates(result) {
    const path = [];
    const myRoute = result.routes[0].legs;
    myRoute.forEach(leg => {
        leg.steps.forEach(step => {
            step.path.forEach(latlng => {
                path.push(latlng);
            });
        });
    });
    return path;
}

function computeTotalDistance(result) {
    let distance = 0;
    const myRoute = result.routes[0].legs;
    myRoute.forEach(leg => {
        leg.steps.forEach(step => {
            distance += step.distance.value;
        });
    });
    return distance;
}

$(function () {
    connect();

    $(window).on('beforeunload', function() {
        disconnect();
    });
});


