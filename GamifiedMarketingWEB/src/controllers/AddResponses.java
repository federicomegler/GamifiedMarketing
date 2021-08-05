package controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.AnswerService;
import services.StatisticalAnswerService;

/**
 * Servlet implementation class QuestionnaireResponse
 */
@WebServlet("/QuestionnaireResponse")
public class AddResponses extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name="service/StatisticalAnswerService")
    private StatisticalAnswerService sas;   
	@EJB(name="service/AnswerService")
	private AnswerService as;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddResponses() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
