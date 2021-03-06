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
import exceptions.CredentialsException;
import exceptions.LogException;
import exceptions.ProductException;
import exceptions.QuestionException;
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
			
			try {
				if(ls.alreadyLogged(  ((User)session.getAttribute("user")).getUsername()) || ((User)session.getAttribute("user")).getBan() == 1) {
					session.setAttribute("user", (User)session.getAttribute("user"));
					response.sendRedirect("Home");
				}
				else {
					try {
						p = ps.getProductOfTheDay();
					} catch (ProductException e) {
						ctx.setVariable("user", ((User)session.getAttribute("user")));
						ctx.setVariable("noproductfound", 1);
						ctx.setVariable("comment", 0);
						path = "/WEB-INF/Home.html";
						templateEngine.process(path, ctx, response.getWriter());
						return;
					}
					if(p == null) {
						ctx.setVariable("user", ((User)session.getAttribute("user")));
						ctx.setVariable("noproductfound", 1);
						ctx.setVariable("comment", 0);
						path = "/WEB-INF/Home.html";
						templateEngine.process(path, ctx, response.getWriter());
					}
					else {
						List<Question> questions = null;
						
						
						try {
							questions = qs.getQuestions(p.getId());
						} catch (QuestionException | ProductException e) {
							ctx.setVariable("user", ((User)session.getAttribute("user")));
							ctx.setVariable("noproductfound", 1);
							ctx.setVariable("comment", 0);
							ctx.setVariable("server_error", 1);
							path = "/WEB-INF/Home.html";
							templateEngine.process(path, ctx, response.getWriter());
							return;
						}
						
						
						ctx.setVariable("questions", questions);
						ctx.setVariable("nrQuestions", questions.size());
						ctx.setVariable("user", (User)session.getAttribute("user"));
						ctx.setVariable("server_error", 0);
						path = "/WEB-INF/Wizard.html";
						
						templateEngine.process(path, ctx, response.getWriter());
					}
				}
			} catch (CredentialsException | LogException | IOException e) {
				ctx.setVariable("user", ((User)session.getAttribute("user")));
				ctx.setVariable("noproductfound", 1);
				ctx.setVariable("comment", 0);
				ctx.setVariable("server_error", 1);
				path = "/WEB-INF/Home.html";
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
		}		
	}
}
