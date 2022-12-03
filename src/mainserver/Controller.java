package mainserver;

import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.userdata.UserException;
import gameserver.GameSocketServer;

/**
 * Servlet implementation class Controller
 */
@WebServlet("/Controller")
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@EJB
	Facade facade;
	private boolean initialized = false;
	/**
     * Default constructor. 
     */
    public Controller() {

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(!initialized) {
			try {
				facade.createUser("Shepard", "Commander", "shepard", "1");
				facade.createUser("Master", "Chief", "masterchief", "1");
				facade.createItem();
			} catch (UserException e) {
				e.printStackTrace();
			}
			launchGameServer();
			initialized = true;
		}
		String op = request.getParameter("op");
		String firstName;
		String lastName;
		String userName;
		String password;
		String redirectAddress;
		String userToken;
		String nextURI;
		String itemId;
		switch(op) {
		case "createAccount" :
			firstName = request.getParameter("firstName");
			lastName = request.getParameter("lastName");
			userName = request.getParameter("userName");
			password = request.getParameter("password");
			try {
				facade.createUser(firstName, lastName, userName, password);
				request.getRequestDispatcher("index.html").forward(request, response);
			} catch(UserException e) {
				response.getWriter().append(e.getMessage());
			}
			break;
		case "authenticateUser" :
			userName = request.getParameter("userName");
			password = request.getParameter("password");
			try {
				String newToken = facade.authenticateUser(userName, password);
				if(newToken.equals("accessDenied")) {
					request.setAttribute("wrongPassword", true);
					request.getRequestDispatcher("WEB-INF/authentication.jsp").forward(request, response);
					return;
				} else {
					response.addCookie(new Cookie("TOKEN",newToken));
					response.sendRedirect("home/homepage.html");
				}
			} catch (UserException e) {
				response.getWriter().append(e.getMessage());
			}
			break;
		case "usernameCheck" :
			userName = request.getParameter("userName");
			if(facade.checkUserName(userName)) {
				response.getWriter().write("cleared");
			}
			break;
		case "authCheck" :
			userToken = request.getParameter("TOKEN");
			nextURI = request.getParameter("next");
			if(facade.checkUserAuth(userToken)) {
				request.getRequestDispatcher(nextURI).forward(request, response);
			} else {
				request.setAttribute("wrongPassword", false);
				request.getRequestDispatcher("WEB-INF/authentication.jsp").forward(request, response);
			}
			break;
		case "disconnectUser" :
			userName = request.getParameter("userName");
			facade.disconnectUser(userName);
			response.addCookie(new Cookie("TOKEN",""));
			response.sendRedirect("index.html");
			break;
		case "redirect" :
			redirectAddress = request.getParameter("address");
			userName = request.getParameter("userName");
			if(facade.checkUserAuth(userName)){
				request.getRequestDispatcher(redirectAddress+".html").forward(request, response);
			}
			break;
		case "authservice" :
			if(request.getCookies() != null && cookieValue(request.getCookies(),"TOKEN") != "") {
				String tokenValue = cookieValue(request.getCookies(),"TOKEN");
				if(facade.checkUserAuth(tokenValue)) {
					response.sendRedirect("home/homepage.html");
					return;
				}
			}
			request.setAttribute("wrongPassword", false);
			request.getRequestDispatcher("WEB-INF/authentication.jsp").forward(request, response);
			break;
		case "authLastName" :
			userName = request.getParameter("userName");
			String lastname = facade.getAuthLastName(userName);
			response.getWriter().append(lastname);
			break;
		case "authFirstName" :
			userName = request.getParameter("userName");
			String firstname = facade.getAuthFirstName(userName);
			response.getWriter().append(firstname);
			break;
		case "authScore" :
			userName = request.getParameter("userName");
			String score = facade.getAuthScore(userName);
			response.getWriter().append(score);
			break;
		case "authCredits" :
			userName = request.getParameter("userName");
			String credits = facade.getAuthCredits(userName);
			response.getWriter().append(credits);
			break;
		case "authUserPPic" :
			userName = request.getParameter("userName");
			String picture = facade.getAuthPPic(userName);
			response.getWriter().append(picture);
			break;
		case "buyItem" : 
			itemId =request.getParameter("itemID");
			userName = request.getParameter("userName");
			Boolean transactionComplete = facade.userGetItem(itemId,userName);
			response.getWriter().append(String.valueOf(transactionComplete));
		}	
	}	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public boolean requestPrivateRessources(HttpServletRequest request) {
		System.out.println(request.getRequestURI());
		return true;
	}
	
	public void checkAuthentication(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userToken = cookieValue(request.getCookies(),"TOKEN");
		if(userToken.equals("")) {
			response.sendRedirect("/index.html");
		} else {
			if (!facade.checkUserAuth(userToken)) {
				response.sendRedirect("/index.html");
			}
		}
	}

	public void redirectService(HttpServletRequest request,HttpServletResponse response, String adress) {
		try {
			request.getRequestDispatcher(adress).forward(request, response);
		} catch (ServletException e) {

		} catch (IOException e) {

		} 
	}

	public String cookieValue(Cookie[] userCookies,String name) {		
		for(int k = 0; k < userCookies.length; k++) {
			Cookie userCookie = userCookies[k];
			if (userCookie.getName().equals(name)) {
				return userCookie.getValue();
			}
		}
		return "";
	}
	
	
	private void launchGameServer() {
		System.out.println("\n\n\n\n\n\n\nHELLO\n\n");
		System.out.println(facade);
		GameSocketServer gameServer = new GameSocketServer();
		gameServer.setMainServer(gameServer);
		gameServer.setFacade(facade);
		System.out.println(facade);
	}
		
}
