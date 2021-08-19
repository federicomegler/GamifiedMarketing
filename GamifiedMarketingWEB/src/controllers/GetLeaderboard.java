package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import entities.Review;
import entities.User;
import services.UserService;

/**
 * Servlet implementation class GetLeaderboard
 */
@WebServlet("/GetLeaderboard")
public class GetLeaderboard extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "services/UserService")
	private UserService us;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetLeaderboard() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if(session.isNew() || session.getAttribute("user") == null) {
			String path = getServletContext().getContextPath();
			response.sendRedirect(path + "/index.html");
		}
		else{
			List<User> users = us.getLeaderboard();
			List<List<String>> table = new ArrayList<List<String>>();
		    
			for(int i=0; i<users.size(); ++i) {
				List<String> elements = new ArrayList<String>();
				elements.add(Integer.toString(i+1));
				elements.add(users.get(i).getUsername());
				System.out.println(users.get(i).getPoints());
				elements.add(Integer.toString(users.get(i).getPoints()));
				table.add(elements);
			}
			
		    String json = new Gson().toJson(table);
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(json);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
