package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import entities.Product;
import entities.User;
import exceptions.ProductException;
import services.LogService;
import services.ProductService;

/**
 * Servlet implementation class GetSubmitUser
 */
@WebServlet("/GetSubmitUser")
public class GetSubmitUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "services/ProductService")
	private ProductService ps;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetSubmitUser() {
        super();
        // TODO Auto-generated constructor stub
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
			List<String> usersSubmit = null;
			List<String> usersCancelled = null;
			
			usersSubmit = ps.getUserSubmit(Integer.parseInt(request.getParameter("id")));
			
			
			Map<String, List<List<String>>> tables = new HashMap<String, List<List<String>>>();
			
			List<List<String>> tableSubmit = new ArrayList<List<String>>();
		    
			for(int i=0; i<usersSubmit.size(); ++i) {
				List<String> elements = new ArrayList<String>();
				elements.add(usersSubmit.get(i));
				tableSubmit.add(elements);
			}
			tables.put("submit", tableSubmit);
			
			usersCancelled = ps.getUserCancelled(Integer.parseInt(request.getParameter("id")), usersSubmit);
			List<List<String>> tableCancelled = new ArrayList<List<String>>();
			
			for(int i=0; i<usersCancelled.size(); ++i) {
				List<String> elements = new ArrayList<String>();
				elements.add(usersCancelled.get(i));
				tableCancelled.add(elements);
			}
			
			tables.put("cancelled", tableCancelled);
			
		    String json = new Gson().toJson(tables);
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
