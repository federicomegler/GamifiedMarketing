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

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import entities.Product;
import entities.User;
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
		
		//prova commit
		
		String path = "";
		HttpSession session = request.getSession();
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		if(session.isNew() || session.getAttribute("user") == null) {
			path = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(path);
		}
		else {
			Product prod = null;
			try {
				prod = ps.getProductOfTheDay();
			} catch (ProductException e) {
				ctx.setVariable("user", ((User)session.getAttribute("user")));
				ctx.setVariable("noproductfound", 1);
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
				ctx.setVariable("user", ((User)session.getAttribute("user")));
				ctx.setVariable("image", prod.getImageData());
				ctx.setVariable("noproductfound", 0);
				ctx.setVariable("ean", prod.getEan());
				ctx.setVariable("alreadylogged", ls.alreadyLogged(((User)session.getAttribute("user")).getUsername()));
				ctx.setVariable("comment", 0);
				path = "/WEB-INF/Home.html";
				templateEngine.process(path, ctx, response.getWriter());
			}
		}
	}
}
