// var stompClient = null;
//
// function connect() {
//     var socket = new SockJS('/ws');
//     stompClient = Stomp.over(socket);
//     stompClient.connect({}, function (frame) {
//         console.log('Connected: ' + frame);
//         stompClient.subscribe('/topic/response', function (response) {
//             showResponse(JSON.parse(response.body));
//         });
//     });
// }
//
// function sendCommand() {
//     var command = document.getElementById('command').value;
//     stompClient.send("/app/command", {}, command);
// }
//
// function showResponse(response) {
//     document.getElementById('output').textContent = response;
// }
