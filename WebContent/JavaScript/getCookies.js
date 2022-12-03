/**
 *
 */

function getCookie(cookieValue) {
	var cookies = document.cookie.split(';');
	var cookiesValue = "";
	for(var k = 0; k < cookies.length; k++) {
		cookiesValue = cookies[k].split('=');
		if(cookiesValue[0].charAt(0) == ' ') {
			cookiesValue[0] = cookiesValue[0].substring(1); 
		}
		if(cookiesValue[0] == cookieValue) {
			return cookiesValue[1];
		}
	}
	return "";
}
