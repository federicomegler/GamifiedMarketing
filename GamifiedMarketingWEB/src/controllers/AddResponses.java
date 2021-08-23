package controllers;

import java.io.IOException;
import java.util.ArrayList;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import entities.Product;
import entities.User;
import exceptions.AnswerException;
import exceptions.CredentialsException;
import exceptions.LogException;
import exceptions.OffensiveWordException;
import exceptions.ProductException;
import exceptions.QuestionException;
import services.AnswerService;
import services.LogService;
import services.ProductService;
import services.QuestionService;
import services.UserService;

/**
 * Servlet implementation class QuestionnaireResponse
 */
@WebServlet("/AddResponses")
public class AddResponses extends HttpServlet {
	private static final long serialVersionUID = 1L;  
	@EJB(name="service/AnswerService")
	private AnswerService as;
	@EJB(name="service/QuestionService")
	private QuestionService qs;
	@EJB(name="service/ProductService")
	private ProductService ps;
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
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		if(session.isNew() || session.getAttribute("user") == null) {
			path = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(path);
		}
		else {
			
			try {
				
				
				if(ls.alreadyLogged(  ((User)session.getAttribute("user")).getUsername()) || ((User)session.getAttribute("user")).getBan() == 1 ) {
					//se ha gia aggiunto una risposta o è bloccato
					response.sendRedirect("Home");
					return;
				}
				else {
					
					
					if(request.getParameter("nrQuestions") == null || !StringUtils.isNumeric(request.getParameter("nrQuestions"))) {
						//controllo che nr sia un numero e non nullo
						response.sendRedirect("Questionnaire");
						return;
					}
					
					int nr = Integer.parseInt(request.getParameter("nrQuestions"));
					
					List<String> answers = new ArrayList<String>();
					List<Integer> prod_ids = new ArrayList<Integer>();
					Product prod_day = null;
					
					try {
						//ottengo il prodotto del giorno
						prod_day = ps.getProductOfTheDay();
						
					} catch (ProductException e2) {
						ctx.setVariable("questions", null);
						ctx.setVariable("nrQuestions", 0);
						ctx.setVariable("server_error", 1);
						ctx.setVariable("user", (User)session.getAttribute("user"));
						path = "/WEB-INF/Wizard.html";
						templateEngine.process(path, ctx, response.getWriter());
						return;
					}
					
					if(prod_day != null) {
						
						//controllo input e creazione delle liste
						
						for(int i=1;i<=nr;i++) {
							//Crea liste da mandare ad AnswerService
							if(request.getParameter("q" + Integer.toString(i)) == null || request.getParameter("Question" + Integer.toString(i)) == null || !StringUtils.isNumeric(request.getParameter("q" + Integer.toString(i)))) {
								response.sendRedirect("Questionnaire");
								return;
							}
							
							
							
							try {
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
							} catch (NumberFormatException | QuestionException | IOException e) {
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
					else {
						response.sendRedirect("Home");
						return;
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
							if(age < 16 || age > 110) {
								age = -1;
							}
						}
						
						
						//in gender può esserci solo M, F o none (' ')
						switch (request.getParameter("genderRadio")) {
						case "M": {gender = 'M'; break;}
						case "F": {gender = 'F'; break;}
						default: {gender = ' '; break;}
							
						}
						//in expLevel può esserci solo Low , Medium e High
						switch (expL) {
							case "Low": {expLev = 1; break;}
							case "Medium": {expLev = 2; break;}
							case "High" : {expLev = 3; break;}
							default: {expLev = -1; break;}
							
						}
						
						
						//if everything is ok insert answers
						try {
							as.insertAnswers(answers, prod_ids, ((User)session.getAttribute("user")).getUsername(), nr, age, gender, expLev );
						}
						catch (OffensiveWordException e) {
							//eccezione lanciata poiché il trigger ha individuato una parola offensiva
							try {
								ls.insertLog(((User)session.getAttribute("user")).getUsername() );
								as.insertStatisticalAnswer(age, gender, expLev, (User)session.getAttribute("user"));
							} catch (ProductException | AnswerException e1) {
								System.out.println("exception");
								//L'utente va comunque bannato
								User user = null;
								try {
									user = us.banUser( ((User)session.getAttribute("user")).getUsername() );
								} catch (CredentialsException e2) {
									response.sendRedirect("Home");
									return;
								}
								
								session.setAttribute("user", user);
								ctx.setVariable("questions", prod_day.getQuestions());
								ctx.setVariable("nrQuestions", nr);
								ctx.setVariable("server_error", 1);
								ctx.setVariable("user", (User)session.getAttribute("user"));
								path = "/WEB-INF/Wizard.html";
								templateEngine.process(path, ctx, response.getWriter());
								return;
							}
							
							User user = null;
							try {
								user = us.banUser( ((User)session.getAttribute("user")).getUsername() );
							} catch (CredentialsException e1) {
								response.sendRedirect("Home");
								return;
							}
							session.setAttribute("user", user);
							response.sendRedirect("Home");
							return;
						}
						catch (ProductException | AnswerException e) {
							e.printStackTrace();
							System.out.println("prod exception");
							ctx.setVariable("questions", prod_day.getQuestions());
							ctx.setVariable("nrQuestions", nr);
							ctx.setVariable("server_error", 1);
							ctx.setVariable("user", (User)session.getAttribute("user"));
							path = "/WEB-INF/Wizard.html";
							templateEngine.process(path, ctx, response.getWriter());
							return;
						} 
						
						//se tutto va a buon fine
						ctx.setVariable("user", ((User)session.getAttribute("user")));
						path = "/WEB-INF/thanks.html";
						templateEngine.process(path, ctx, response.getWriter());
					}
					
					
				}
			} catch (NumberFormatException | CredentialsException | LogException | IOException e) {
				ctx.setVariable("questions", null);
				ctx.setVariable("nrQuestions", 0);
				ctx.setVariable("server_error", 1);
				ctx.setVariable("user", (User)session.getAttribute("user"));
				path = "/WEB-INF/Wizard.html";
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
			
		}
		
		
	}

}
