package controllers;

import java.io.IOException;

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
import entities.User;
import exceptions.CredentialsException;
import exceptions.LogException;
import exceptions.ProductException;
import services.LogService;
import services.ProductService;
import services.ReviewService;

/**
 * Servlet implementation class Home
 */
@WebServlet("/Home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/ProductService")
	private ProductService ps;
	@EJB(name = "services/ReviewService")
	private ReviewService rs;
	@EJB(name = "services/LogService")
	private LogService ls;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Home() {
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
		
		
		
		String path = "";
		HttpSession session = request.getSession();
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		//first of all check if the user is logged, if not redirect to index page
		if(session.isNew() || session.getAttribute("user") == null) {
			path = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(path);
		}
		else {
			Product prod = null;
			try {
				//retrive the product of the day
				prod = ps.getProductOfTheDay();
			} catch (ProductException e) {
				//if the product is not found an exception is raised
				ctx.setVariable("user", ((User)session.getAttribute("user")));
				ctx.setVariable("noproductfound", 1); // 1 means that the product is not found
				ctx.setVariable("comment", 0); 
				path = "/WEB-INF/Home.html";
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
			if(prod == null) {
				ctx.setVariable("user", ((User)session.getAttribute("user")));
				ctx.setVariable("noproductfound", 1);
				ctx.setVariable("comment", 0);
				path = "/WEB-INF/Home.html";
				templateEngine.process(path, ctx, response.getWriter());
			}
			else {
				//if the product is not null se the variable to be displayed on the hmtl page
				ctx.setVariable("user", ((User)session.getAttribute("user")));
				ctx.setVariable("image", prod.getImageData());
				ctx.setVariable("noproductfound", 0);
				ctx.setVariable("ean", prod.getEan());
				try {
					//variable used to check if the user has already reply to the questionnaire
					ctx.setVariable("alreadylogged", ls.alreadyLogged(((User)session.getAttribute("user")).getUsername()));
				} catch (CredentialsException | LogException e) {
					ctx.setVariable("alreadylogged", true);
				}
				ctx.setVariable("comment", 0);
				path = "/WEB-INF/Home.html";
				templateEngine.process(path, ctx, response.getWriter());
			}
		}
	}
}
