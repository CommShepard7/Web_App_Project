/**
 * 
 */


$(document).ready(function() {
    let href=document.location.href.split("http://")[1].split(":")[0];
    webSocket = new WebSocket( "ws://"+href+":8080/WEBApp/gameserver/1010");
    webSocket.addEventListener("open", function(evt) {
            webSocket.send("CONNECTION_DATA:"+getCookie("userName"));
            webSocket.send("START_GAME_QUASAR");
            console.log("Creating game server");
    });
    webSocket.addEventListener("message", function(evt) {
        console.log(evt.data);
        if(evt.data.includes("GAME_WON :")) {
            gameWon = evt.data.split(" : ")[1];
            if(gameWon == "true") {
                window.alert("Game won!");
                window.location.replace("home/games/games.html");
            } else {
                window.alert("Game lost!");
                window.location.replace("home/games/games.html");
            }
        } else {
            gameData = evt.data.split(";");
            document.getElementById("randomNumber").innerHTML = gameData[0];
            document.getElementById("score").innerHTML = gameData[1];
        }
    });
	document.getElementById("ONEToEIGHT").addEventListener("click", function() {
        webSocket.send("GAME_ACTION:QUASAR:ONEToEIGHT");
    });
    document.getElementById("FOURToSEVEN").addEventListener("click", function() {
        webSocket.send("GAME_ACTION:QUASAR:FOURToSEVEN");
    });
});

