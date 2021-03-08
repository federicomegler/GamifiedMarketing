package controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entities.User;
import services.UserService;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "services/UserService")
    private UserService us;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckLogin() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() {
    	
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "";
		User user = null;
		try {
			user = us.checkCredentials(request.getParameter("username"), request.getParameter("password"));
		} catch (Exception e) {
			// TODO: handle exception for specific class
		}
		if(user == null) {
			//TODO manage the situation where the user is null
		}
		else {
			request.getSession().setAttribute("user", user);
			path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		}
	}

}
