package controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entities.User;
import exceptions.ReviewException;
import services.ReviewService;

/**
 * Servlet implementation class Comment
 */
@WebServlet("/Comment")
public class Comment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name="services/ReviewService")
	private ReviewService rs;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Comment() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		if(session.isNew() || session.getAttribute("user") == null) {
			String path = getServletContext().getContextPath();
			response.sendRedirect(path + "/index.html");
		}
		else {
			String comment = request.getParameter("review");
			String ean = request.getParameter("ean");
			String username = ((User)request.getSession().getAttribute("user")).getUsername();
			
			if(comment == null || ean == null) {
				String path = getServletContext().getContextPath() + "/Home";
				response.sendRedirect(path);
			}
			else {
				try {
					rs.createComment(comment, username, ean);
				} catch (ReviewException e) {
					e.printStackTrace();
				}
				String path = getServletContext().getContextPath() + "/Home";
				response.sendRedirect(path);
			}
		}
	}

}
