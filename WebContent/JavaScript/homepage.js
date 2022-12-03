/**
 * 
 */

$(document).ready(function() {
    loadMain();
});

function loadMain() {
    var webSocket;
    $("#submit").click(function() {
        if(getCookie("userName") == "") {
            window.location.href = "index.html";
        } else {
            window.location.href = "Controller?userName="+getCookie("userName")+"&op=disconnectUser";
        }
    });
    
    $("#btnChat").click(function() {
    	if(document.getElementById("Chat").style.display != 'none'){
    		document.getElementById("Chat").style.display = 'none';
    		document.getElementById("btnSend").style.display = 'none';
    	} else {
    		document.getElementById("Chat").style.display = 'block';
    		document.getElementById("btnSend").style.display = 'block';
    	}
    });

    $("#username").html("Welcome " + getCookie("userName"));
    
}