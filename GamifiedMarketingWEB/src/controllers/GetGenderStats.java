package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import entities.User;
import exceptions.StatisticsException;
import services.StatsService;

/**
 * Servlet implementation class GetGenderStats
 */
@WebServlet("/GetGenderStats")
public class GetGenderStats extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@EJB(name = "services/StatsService")
	private StatsService ss;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetGenderStats() {
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
			if(((User)session.getAttribute("user")).getAdmin() == 0) {
				response.sendRedirect("Home");
			}
			else {
				if(((User)session.getAttribute("user")).getAdmin() == 0) {
					String json = new Gson().toJson("error");
				    System.out.println(json);
				    response.setContentType("application/json");
				    response.setCharacterEncoding("UTF-8");
				    response.getWriter().write(json);
				}
				else {
					Map<String, Long> genders = new HashMap<String, Long>();
				
					try {
						genders = ss.getGenderDistribution();
					} catch (StatisticsException e) {
						String json = new Gson().toJson("error");
					    System.out.println(json);
					    response.setContentType("application/json");
					    response.setCharacterEncoding("UTF-8");
					    response.getWriter().write(json);
					    return;
					}
				
					String json = new Gson().toJson(genders);
					System.out.println(json);
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write(json);
				}
				
			}
			
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
