package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.cxf.transport.http.HTTPSession;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import entities.User;
import exceptions.OffensiveWordException;
import exceptions.ProductException;
import services.AnswerService;
import services.LogService;
import services.OffensiveWordService;
import services.ProductService;
import services.QuestionService;
import services.StatisticalAnswerService;
import services.UserService;

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
	@EJB(name = "services/OffensiveWordService")
	private OffensiveWordService os;
	@EJB(name = "service/UserService")
	private UserService us;
	@EJB(name = "services/LogService")
	private LogService ls;
	
	private TemplateEngine templateEngine;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddResponses() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		HttpSession session= request.getSession();
		String nrans=request.getParameter("nrQuestions");
		int nr=Integer.parseInt(nrans);
		
		List<String> answers = new ArrayList<String>();
		List<Integer> prod_ids = new ArrayList<Integer>();
		List<String> users = new ArrayList<String>();
		
		for(int i=1;i<=nr;i++)
		{
			try {
				if(qs.isValid(Integer.parseInt(request.getParameter("q"+Integer.toString(i)))))
				{
					answers.add(request.getParameter("Question" + Integer.toString(i)));
					prod_ids.add(Integer.parseInt(request.getParameter("q"+Integer.toString(i))));
					users.add(((User) session.getAttribute("user")).getUsername());
				}
				else
				{
					// TODO rispondere con errore non con eccezione
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
		
		try {
			as.insertAnswers(answers, prod_ids, users, nr);
		} catch (ProductException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OffensiveWordException e) {
			User user = us.banUser( ((User)session.getAttribute("user")).getUsername() );
			session.setAttribute("user", user);
			response.sendRedirect("Home");
			e.printStackTrace();
		}
		
		
		String expL=request.getParameter("expLevel");
		int expLev=0;
		
		switch (expL) {
		
			case "Low": expLev = 1;
			case "Medium": expLev = 2;
			case "High" : expLev = 3;
			default: expLev = 1; // TODO print errore;
			
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
		
		
		try {
			ls.insertLog(((User) session.getAttribute("user")).getUsername());
		} catch (ProductException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
