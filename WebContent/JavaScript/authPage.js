/**
 * 
 */

$(document).ready(function() {
    loadMain();
});

function loadMain() {
    $("#submit").click(function ()  {
        userNameCheckSubmit();
    });
}

function userNameCheckSubmit() {
	if($("#username").val() == "" || $("#password").val() == "") {
		alert("Please enter credentials");
		return;
	}
	document.cookie = "userName=" + $("#username").val()
	$.ajax({
		url: "Controller?userName=" + $("#username").val() + "&op=usernameCheck",
		type: "GET",
		success : function(response) {
			if(response == "cleared") {
				$("#credentials").submit();
			} else {
				alert("Username does not exist");
			}
		}
	});
}
