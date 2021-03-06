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
import exceptions.ReviewException;
import services.LogService;
import services.ProductService;
import services.ReviewService;

/**
 * Servlet implementation class Comment
 */
@WebServlet("/Comment")
public class Comment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name="services/ReviewService")
	private ReviewService rs;
	@EJB(name = "services/ProductService")
	private ProductService ps;
	@EJB(name = "services/LogService")
	private LogService ls;
	
	
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Comment() {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		String path = "";
		
		//check if the user is logged
		if(session.isNew() || session.getAttribute("user") == null) {
			path = getServletContext().getContextPath();
			response.sendRedirect(path + "/index.html");
		}
		else {
			//take the parameter sent by the form
			String comment = request.getParameter("review");
			String ean = request.getParameter("ean");
			String username = ((User)session.getAttribute("user")).getUsername();
			
			Product prod = null;
			try {
				//getting the product of the day
				prod = ps.getProductOfTheDay();
			} catch (ProductException e) {
				//if not found raise an exception
				ctx.setVariable("user", ((User)session.getAttribute("user")));
				ctx.setVariable("noproductfound", 1);
				ctx.setVariable("comment_err", 2); //if the product is not found maybe i'm trying to comment a product with different id that does not exist
				path = "/WEB-INF/Home.html";
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
			
			if(prod == null) {
				ctx.setVariable("user", ((User)session.getAttribute("user")));
				ctx.setVariable("noproductfound", 1);
				ctx.setVariable("comment_err", 2);
				path = "/WEB-INF/Home.html";
				templateEngine.process(path, ctx, response.getWriter());
			}
			else {
				try {
					if(comment == null || ean == null || !ean.equals(prod.getEan()) || comment.isBlank()) {
						ctx.setVariable("user", ((User)session.getAttribute("user")));
						ctx.setVariable("image", prod.getImageData());
						ctx.setVariable("noproductfound", 0);
						ctx.setVariable("ean", prod.getEan());
						ctx.setVariable("alreadylogged", ls.alreadyLogged(((User)session.getAttribute("user")).getUsername()));
						ctx.setVariable("comment_err", 2);
						path = "/WEB-INF/Home.html";
						templateEngine.process(path, ctx, response.getWriter());
					}
					else {
						try {
							//comment insertion
							rs.createComment(comment, username, ean);
						} catch (ReviewException e) {
							//if there is and error during the comment creation ad exceptions is raised
							ctx.setVariable("user", ((User)session.getAttribute("user")));
							ctx.setVariable("image", prod.getImageData());
							ctx.setVariable("noproductfound", 0);
							ctx.setVariable("ean", prod.getEan());
							ctx.setVariable("alreadylogged", ls.alreadyLogged(((User)session.getAttribute("user")).getUsername()));
							ctx.setVariable("comment_err", 2);
							path = "/WEB-INF/Home.html";
							templateEngine.process(path, ctx, response.getWriter());
							return;
						}
						//if no error 
						ctx.setVariable("user", ((User)session.getAttribute("user")));
						ctx.setVariable("image", prod.getImageData());
						ctx.setVariable("noproductfound", 0);//this mean that the product is found
						ctx.setVariable("ean", prod.getEan());
						ctx.setVariable("alreadylogged", ls.alreadyLogged(((User)session.getAttribute("user")).getUsername()));
						ctx.setVariable("comment_err", 1);//this mean that the comment is correctly inserted
						path = "/WEB-INF/Home.html";
						templateEngine.process(path, ctx, response.getWriter());
					}
				}
				catch(CredentialsException | LogException e) {
					ctx.setVariable("user", ((User)session.getAttribute("user")));
					ctx.setVariable("image", prod.getImageData());
					ctx.setVariable("noproductfound", 0);
					ctx.setVariable("ean", prod.getEan());
					ctx.setVariable("alreadylogged", true);
					path = "/WEB-INF/Home.html";
					templateEngine.process(path, ctx, response.getWriter());
					return;
				}
			}
			
		}
	}

}
