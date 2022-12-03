package mainserver;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class ControllerFilter
 */
@WebFilter("/*")
public class ControllerFilter implements Filter {
	
	
    /**
     * Default constructor. 
     */
    public ControllerFilter() {

    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		System.out.println("Filter shutting down");
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String requestPath = httpRequest.getRequestURI();
		if(requestPath.contains("/home/")) {
			request.setAttribute("authRequired", true);
			String relativeRequestPath = requestPath.split("/WEBApp/")[1];
			System.out.println("\nFILTERING REQUEST : "+ httpRequest.getRequestURI() + " " + request + "\n");
			if(httpRequest.getCookies() != null) {
				((HttpServletResponse) response).sendRedirect("/WEBApp/Controller?op=authCheck&TOKEN="+cookieValue(httpRequest.getCookies(),"TOKEN")+"&next="+relativeRequestPath);
			} else {
				((HttpServletResponse) response).sendRedirect("/WEBApp/Controller?op=authCheck&TOKEN=");
			}
		} else {
			System.out.println("\nREQUEST FORWARDED TO SERVER : "+ httpRequest.getRequestURI() + "\n");
			chain.doFilter(request, response);
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
	
	public String gamePortValue(String requestPath) {
		String[] pathValues = requestPath.split("/gameserver/");
		return pathValues[1];
	}
	
	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		System.out.println("Filter initialization");
	}

}
