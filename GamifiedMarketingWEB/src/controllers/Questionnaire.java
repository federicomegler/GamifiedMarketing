package controllers;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import entities.Product;
import entities.Question;
import entities.User;
import exceptions.ProductException;
import services.LogService;
import services.ProductService;
import services.QuestionService;


/**
 * Servlet implementation class Questionnaire
 */
@WebServlet("/Questionnaire")
public class Questionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name="service/QuestionService")
    private QuestionService qs;
	@EJB(name="service/ProductService")
    private ProductService ps;
	@EJB(name = "services/LogService")
	private LogService ls;
	
	private TemplateEngine templateEngine;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Questionnaire() {
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
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		Product p= null;
		HttpSession session = request.getSession();
		String path = getServletContext().getContextPath() + "/index.html";
		if(session.isNew() || session.getAttribute("user") == null) {
			path = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(path);
		}
		else {
			
			if(ls.alreadyLogged(  ((User)session.getAttribute("user")).getUsername()) || ((User)session.getAttribute("user")).getBan() == 1) {
				session.setAttribute("user", (User)session.getAttribute("user"));
				response.sendRedirect("Home");
			}
			else {
				try {
					p = ps.getProductOfTheDay();
				} catch (ProductException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(p == null) {
					// TODO mettere reindirizzamento ad home con errore
					response.getWriter().append("Served at: ").append("non ci sono prodotti del giorno");
				}
				else {
					List<Question> questions = qs.getQuestions(p.getId());
					
					ctx.setVariable("questions", questions);
					ctx.setVariable("nrQuestions", questions.size());
					ctx.setVariable("user", (User)session.getAttribute("user"));
					ctx.setVariable("server_error", 0);
					path = "/WEB-INF/Wizard.html";
					
					templateEngine.process(path, ctx, response.getWriter());
				}
			}
		}		
	}
}
