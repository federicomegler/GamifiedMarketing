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
import exceptions.ReviewException;
import services.ReviewService;

/**
 * Servlet implementation class GetReviews
 */
@WebServlet("/GetReviews")
public class GetReviews extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "services/ReviewService")
	private ReviewService rs;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetReviews() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if(session.isNew() || session.getAttribute("user") == null) {
			String path = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(path);
		}
		else {
			List<Review> reviews = null;
			List<List<String>> table = new ArrayList<>();
			
			
			try {
				reviews = rs.getReviewsOfProductOfTheDay();
			} catch (ReviewException e) {
				List<String> elements = new ArrayList<String>();
				elements.add("");
				elements.add("Unable to load reviews");
				table.add(elements);
				String json = new Gson().toJson(table);
			    response.setContentType("application/json");
			    response.setCharacterEncoding("UTF-8");
			    response.getWriter().write(json);
			}
		    
			for(Review r : reviews) {
				List<String> elements = new ArrayList<String>();
				elements.add(r.getUser().getUsername());
				elements.add(r.getContent());
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
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
