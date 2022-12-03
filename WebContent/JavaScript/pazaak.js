/**
 * 
 */


var numberPlayerX = 0;
var numberPlayerY = 0;
var numberOpponentX = 0;
var numberOpponentY = 0;

$(document).ready(function() {
    let href=document.location.href.split("http://")[1].split(":")[0];
    webSocket = new WebSocket( "ws://"+href+":8080/WEBApp/gameserver/1010");
    webSocket.addEventListener("open", function(evt) {
            webSocket.send("CONNECTION_DATA:"+getCookie("userName"));
            webSocket.send("START_GAME_PAZAAK");
            console.log("Creating game server");
            window.onbeforeunload = function() {
                webSocket.close();
            }
            loadMain(webSocket);
    });
});

function loadMain(webSocket) {
    let userName = getCookie("userName");
    document.getElementById("player1").innerHTML = userName;
    let activePlayer;
    WebSocket = webSocket;
    document.getElementById("endTurnbtn").addEventListener("click",function() {
       webSocket.send("GAME_ACTION:PAZAAK:ENDTURN:"+userName);
       disableButtons(); 
    });
    WebSocket.addEventListener("message", function(evt) {
        console.log("Recieved game data : " + evt.data);
        let gameData = evt.data;
        let gameDataType = gameData.split(":")[0];
        let data;
        let playerName;
        switch(gameDataType) {
            case "GAME_START":
                let cardNames = gameData.split(":")[1].split(";");
                gameStart(cardNames);
                if(gameData.split(";")[4] == 'true') {
                    activePlayer = true;
                    enableButtons();
                } else {
                    activePlayer = false;
                    disableButtons();
                }
                document.getElementById("player2").innerHTML = gameData.split(";")[5];
                break;
            case "GAME_ONGOING":
                data = gameData.split(":")[1].split(":")[0].split(";");
                playerName = data[0];
                let userScore = data[1];
                console.log(data[0]+data[1]+data[2]+data[3]);
                let cardType = data[2];
                let card = data[3];
                updateBoard(cardType,card,userScore,playerName);
                break;
            case "GAME_END":
                window.alert("Game ended," + gameData.split(":")[1] + " won!");
                window.location.replace("home/games/games.html");
                break;
        }
    });
}

function updateBoard(cardType,card,userScore,playerName) {
    let cardPosX;
    let cardPosY;
    console.log(playerName);
    console.log(getCookie("userName"));
    if((cardType == "MINUS" || cardType == "PLUS") && playerName == getCookie("userName")) {
        if(cardType == "PLUS") {
            let cardId = card.split("+")[1];
            document.getElementById(cardType+":"+cardId).remove();
        } else {
            document.getElementById(cardType+":"+card).remove();
        }
    }
    if(playerName == getCookie("userName")) {
        document.getElementById("playerScore1").innerHTML = userScore;
        cardPosX = 20 + numberPlayerX*200; 
        cardPosY = 100 + numberPlayerY*210;
        numberPlayerX++;
        if(numberPlayerX == 3) {
            numberPlayerX = 0;
            numberPlayerY++;
        }
    } else {
        document.getElementById("playerScore2").innerHTML = userScore;
        cardPosX = 1350 + numberOpponentX*200;
        cardPosY = 100 + numberOpponentY*210;
        numberOpponentX++;
        if(numberOpponentX == 3) {
            numberOpponentX = 0;
            numberOpponentY++;
        }
    }
    console.log(cardPosX);
    cardURL = cardSelector(card)[0];
    let newCard = document.createElement("div");
    newCard.classList.add("card");
    newCard.style.left = cardPosX.toString() + "px";
    newCard.style.top = cardPosY.toString() + "px";
    newCard.style.backgroundImage = "url('" + cardURL + "')";
    document.body.appendChild(newCard);
}

function disableButtons() {
    document.getElementById("endTurnbtn").disababled = true;
}

function enableButtons() {
    document.getElementById("endTurnbtn").disababled = false;
}

function gameStart(playerDeck) {
    let numberPlayerX = 0;
    for(k = 0; k < 4; k++) {
        let card = document.createElement("div");
        card.classList.add("card");
        let cardPosX = 20 + numberPlayerX*200;
        let cardData = cardSelector(playerDeck[k]);
        let cardURL = cardData[0];
        let cardValue = cardData[1];
        if(playerDeck[k].includes("plus")) {
            card.id = "PLUS:"+cardValue;
        } else {
            card.id = "MINUS:"+cardValue;
        }
        card.style.left = cardPosX.toString() + "px";
        card.style.top = "750px";
        card.style.backgroundImage = "url('" + cardURL + "')";
        card.onclick = function() {
            webSocket.send("GAME_ACTION:PAZAAK:"+card.id);
        }
        document.body.appendChild(card);
        numberPlayerX++;
    }
}

function cardSelector(cardName) {
    cardURL = "";
    cardValue = "";
    switch(cardName) {
        case "-1":
        case "minusOne":
            cardURL = "css/media/cards/B-1.png"
            cardValue = "-1";
            break;
        case "-2":
        case "minusTwo":
            cardURL = "css/media/cards/B-2.png"
            cardValue = "-2";
            break;
        case "-3":
        case "minusThree":
            cardURL = "css/media/cards/B-3.png"
            cardValue = "-3";
            break;
        case "-4":
        case "minusFour":
            cardURL = "css/media/cards/B-4.png"
            cardValue = "-4";
            break;
        case "-5":
        case "minusFive":
            cardURL = "css/media/cards/B-5.png"
            cardValue = "-5";
            break;
        case "-6":
        case "minusSix":
            cardURL = "css/media/cards/B-6.png"
            cardValue = "-6";
            break;
        case "+1":
        case "plusOne":
            cardURL = "css/media/cards/B1.png"
            cardValue = "1";
            break;
        case "+2":
        case "plusTwo":
            cardURL = "css/media/cards/B2.png"
            cardValue = "2";
            break;
        case "+3":
        case "plusThree":
            cardURL = "css/media/cards/B3.png"
            cardValue = "3";
            break;
        case "+4":
        case "plusFour":
            cardURL = "css/media/cards/B4.png"
            cardValue = "4";
            break;
        case "+5":
        case "plusFive":
            cardURL = "css/media/cards/B5.png"
            cardValue = "5";
            break;
        case "+6":
        case "plusSix":
            cardURL = "css/media/cards/B6.png"
            cardValue = "6";
            break;
        case "1":
            cardURL = "css/media/cards/G1.png"
            break;
        case "2":
            cardURL = "css/media/cards/G2.png"
            break;
        case "3":
            cardURL = "css/media/cards/G3.png"
            break;
        case "4":
            cardURL = "css/media/cards/G4.png"
            break;
        case "5":
            cardURL = "css/media/cards/G5.png"
            break;
        case "6":
            cardURL = "css/media/cards/G6.png"
            break;
        case "7":
            cardURL = "css/media/cards/G7.png"
            break;
        case "8":
            cardURL = "css/media/cards/G8.png"
            break;
        case "9":
            cardURL = "css/media/cards/G9.png"
            break;
        case "10":
            cardURL = "css/media/cards/G10.png"
            break;
    }
    return [cardURL,cardValue];
}
