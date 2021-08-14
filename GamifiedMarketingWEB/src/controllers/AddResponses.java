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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.transport.http.HTTPSession;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import entities.Product;
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
		String path = "";
		HttpSession session = request.getSession();
		
		if(session.isNew() || session.getAttribute("user") == null) {
			path = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(path);
		}
		else {
			if(ls.alreadyLogged(  ((User)session.getAttribute("user")).getUsername()) || ((User)session.getAttribute("user")).getBan() == 1 ) {
				session.setAttribute("user", (User)session.getAttribute("user"));
				response.sendRedirect("Home");
			}
			else {
				final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
				
				if(request.getParameter("nrQuestions") == null) {
					response.sendRedirect("Questionnaire");
					return;
				}
				
				int nr = Integer.parseInt(request.getParameter("nrQuestions"));
				
				List<String> answers = new ArrayList<String>();
				List<Integer> prod_ids = new ArrayList<Integer>();
				Product prod_day = null;
				
				try {
					prod_day = ps.getProductOfTheDay();
				} catch (ProductException e2) {
					ctx.setVariable("questions", null);
					ctx.setVariable("nrQuestions", 0);
					ctx.setVariable("server_error", 1);
					ctx.setVariable("user", (User)session.getAttribute("user"));
					path = "/WEB-INF/Wizard.html";
					templateEngine.process(path, ctx, response.getWriter());
				}
				
				if(prod_day != null) {
					
					//controllo input e creazione delle liste
					
					for(int i=1;i<=nr;i++) {
						//Ccrea liste da mandare ad AnswerService
						if(request.getParameter("q" + Integer.toString(i)) == null || request.getParameter("Question" + Integer.toString(i)) == null) {
							response.sendRedirect("Questionnaire");
							return;
						}
						
						if(qs.isValid(Integer.parseInt(request.getParameter("q"+Integer.toString(i))))) { //controllo che l'id sia valido
							
							
							String ans = StringEscapeUtils.escapeHtml(request.getParameter("Question" + Integer.toString(i)));
							
							if(ans.isBlank()) {
								//se una risposta è vuota
								ctx.setVariable("questions", prod_day.getQuestions());
								ctx.setVariable("nrQuestions", nr);
								ctx.setVariable("server_error", 1);
								ctx.setVariable("user", (User)session.getAttribute("user"));
								path = "/WEB-INF/Wizard.html";
								templateEngine.process(path, ctx, response.getWriter());
								return;
							}
							else {
								int prod_id = Integer.parseInt(request.getParameter("q"+Integer.toString(i)));
								answers.add(ans);
								prod_ids.add(prod_id);
							}
						}
						else
						{
							ctx.setVariable("questions", prod_day.getQuestions());
							ctx.setVariable("nrQuestions", nr);
							ctx.setVariable("server_error", 1);
							ctx.setVariable("user", (User)session.getAttribute("user"));
							path = "/WEB-INF/Wizard.html";
							templateEngine.process(path, ctx, response.getWriter());
							return;
						}
						
					}
					
					
				}
				
				//estraggo e controllo input utente per statistiche
				
				if(request.getParameter("expLevel") == null || request.getParameter("age") == null || request.getParameter("genderRadio") == null) {
					ctx.setVariable("questions", prod_day.getQuestions());
					ctx.setVariable("nrQuestions", nr);
					ctx.setVariable("server_error", 1);
					ctx.setVariable("user", (User)session.getAttribute("user"));
					path = "/WEB-INF/Wizard.html";
					templateEngine.process(path, ctx, response.getWriter());
				}
				else {
					String expL = request.getParameter("expLevel");
					Integer expLev=0;
					Character gender = ' '; 
					Integer age = -1;
					
					
					if(request.getParameter("age") != "" && StringUtils.isNumeric(request.getParameter("age"))) {
						age = Integer.parseInt(request.getParameter("age"));
					}
					
					
					//in gender può esserci solo M, F o none (' ')
					switch (request.getParameter("genderRadio")) {
					case "M": gender = 'M';
					case "F": gender = 'F';
					default: gender = ' ';
						
					}
					
					switch (expL) {
					
						case "Low": expLev = 1;
						case "Medium": expLev = 2;
						case "High" : expLev = 3;
						default: expLev = -1;
						
					}
					
					//if everything is ok insert answers
					try {
						as.insertAnswers(answers, prod_ids, ((User)session.getAttribute("user")).getUsername(), nr, age, gender, expLev );
					}
					catch (OffensiveWordException e) {
						try {
							ls.insertLog(((User)session.getAttribute("user")).getUsername() );
							as.insertStatisticalAnswer(age, gender, expLev, (User)session.getAttribute("user"));
						} catch (ProductException e1) {
							System.out.println("exception");
							ctx.setVariable("questions", prod_day.getQuestions());
							ctx.setVariable("nrQuestions", nr);
							ctx.setVariable("server_error", 1);
							ctx.setVariable("user", (User)session.getAttribute("user"));
							path = "/WEB-INF/Wizard.html";
						}
						User user = us.banUser( ((User)session.getAttribute("user")).getUsername() );
						session.setAttribute("user", user);
						response.sendRedirect("Home");
					}
					catch (ProductException e) {
						e.printStackTrace();
						System.out.println("prod exception");
						ctx.setVariable("questions", prod_day.getQuestions());
						ctx.setVariable("nrQuestions", nr);
						ctx.setVariable("server_error", 1);
						ctx.setVariable("user", (User)session.getAttribute("user"));
						path = "/WEB-INF/Wizard.html";
						templateEngine.process(path, ctx, response.getWriter());
					} 
					catch (Exception e) {
						System.out.println("exception");
						ctx.setVariable("questions", prod_day.getQuestions());
						ctx.setVariable("nrQuestions", nr);
						ctx.setVariable("server_error", 1);
						ctx.setVariable("user", (User)session.getAttribute("user"));
						path = "/WEB-INF/Wizard.html";
					}
					
					ctx.setVariable("user", ((User)session.getAttribute("user")));
					path = "/WEB-INF/thanks.html";
					templateEngine.process(path, ctx, response.getWriter());
				}
				
				
			}
			
		}
		
		
	}

}
