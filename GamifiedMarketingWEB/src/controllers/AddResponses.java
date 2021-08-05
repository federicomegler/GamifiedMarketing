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

import org.apache.cxf.transport.http.HTTPSession;

import entities.User;
import exceptions.ProductException;
import services.AnswerService;
import services.ProductService;
import services.QuestionService;
import services.StatisticalAnswerService;

/**
 * Servlet implementation class QuestionnaireResponse
 */
@WebServlet("/AddResponses")
public class AddResponses extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name="service/StatisticalAnswerService")
    private StatisticalAnswerService sas;   
	@EJB(name="service/AnswerService")
	private AnswerService as;
	@EJB(name="service/QuestionService")
	private QuestionService qs;
	@EJB(name="service/ProductService")
	private ProductService ps;
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
		
		HttpSession session= request.getSession();
		String nrans=request.getParameter("nrQuestions");
		int nr=Integer.parseInt(nrans);
		String resp;
		for(int i=1;i<=nr;i++)
		{
			try {
				if(qs.isValid(Integer.parseInt(request.getParameter("q"+Integer.toString(i)))))
				{
					resp=request.getParameter("Question"+Integer.toString(i));
					if(!resp.isBlank())
					as.insertAnswer(request.getParameter("Question"+Integer.toString(i)), Integer.parseInt(request.getParameter("q"+Integer.toString(i))), ((User) session.getAttribute("user")).getUsername());
				}
				else
				{
					throw new Exception("stai provando a rispondere ad una domanda non disponibile");
				}
			} catch (ProductException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String expL=request.getParameter("expLevel");
		int expLev=0;
		if(expL=="Low")
		{
			expLev=1;
		}
		else if(expL=="Medium")
		{
			expLev=2;
		}
		else if(expL=="High")
		{
			expLev=3;
		}
		else
		{
			//print errore
		}
		
		try {
			sas.insertStatisticalAnswer(Integer.parseInt(request.getParameter("age")), 
					request.getParameter("genderRadio").charAt(0), 
					expLev, 
					ps.getProductOfTheDay().getId(), 
					((User) session.getAttribute("user")).getUsername());
		} catch (NumberFormatException | ProductException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		System.out.println(request.getParameter("age"));
		System.out.println(request.getParameter("genderRadio").charAt(0));
		System.out.println(request.getParameter("expLevel"));
		try {
			System.out.println(ps.getProductOfTheDay().getId());
			System.out.println(((User) session.getAttribute("user")).getUsername());
		} catch (ProductException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
