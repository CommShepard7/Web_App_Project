<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href = "css/accountstyle.css">
<title>Authentication service</title>
</head>
<body>
<script src = "JavaScript/jQuery_3.6.0.js"></script>
<script src = "JavaScript/getCookies.js"></script>
<script src = "JavaScript/authPage.js"></script>
</head>
<body>
<h1 class = center> Please enter your authentication credentials</h1>
<form id = "credentials" action = "Controller" method = "post">
	User name : <input id = "username" type="text" name="userName" required/><br/>
	Password : <input id = "password" type="password" name="password" required><br/>
	<%
	try{
		boolean wrongPassword = (boolean) request.getAttribute("wrongPassword");
		if(wrongPassword) {
			%> <p style="color:red;">Wrong password</p>  
		<% }
	} catch(NullPointerException e) {
	}
	%>
	<br/>
	<input type="hidden" name="op" value="authenticateUser"/>
</form>
<button id = "submit" > Submit </button>


<br/>
<a class = link href = "newaccount.html"> Return to account creation page </a>

</body>
</html>