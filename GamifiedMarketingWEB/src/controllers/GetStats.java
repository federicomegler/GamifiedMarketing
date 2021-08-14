package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import entities.User;
import services.StatsService;

/**
 * Servlet implementation class GetStats
 */
@WebServlet("/GetStats")
public class GetStats extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "services/StatsService")
	private StatsService ss;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetStats() {
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
			
			Map<String, Integer> logs = new HashMap<String, Integer>();
			
			logs = ss.getLogsLast7Days();
			TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(logs);
			
			
		    String json = new Gson().toJson(sorted_map);
		    System.out.println(json);
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(json);
			}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
