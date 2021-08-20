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

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

import entities.User;
import exceptions.ProductException;
import exceptions.QuestionException;
import services.QuestionService;

/**
 * Servlet implementation class GetMyQNA
 */
@WebServlet("/GetMyQNA")
public class GetMyQNA extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "services/QuestionServices")
    private QuestionService qs;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetMyQNA() {
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
			String id = request.getParameter("id");
			if(id == null || id.isBlank() || !StringUtils.isNumeric(id)) {
				String json = new Gson().toJson("error");
			    System.out.println(json);
			    response.setContentType("application/json");
			    response.setCharacterEncoding("UTF-8");
			    response.getWriter().write(json);
			}
			else {
				Map<String, String> qna = new HashMap<String, String>();
				
				try {
					qna = qs.getQNAByProductAndUser(Integer.parseInt(id), ((User)session.getAttribute("user")).getUsername());
				} catch (NumberFormatException | ProductException | QuestionException e) {
					String json = new Gson().toJson("error");
				    System.out.println(json);
				    response.setContentType("application/json");
				    response.setCharacterEncoding("UTF-8");
				    response.getWriter().write(json);
				    return;
				}
				
			    String json = new Gson().toJson(qna);
			    System.out.println(json);
			    response.setContentType("application/json");
			    response.setCharacterEncoding("UTF-8");
			    response.getWriter().write(json);
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
