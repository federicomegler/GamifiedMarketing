package controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
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

import entities.User;
import exceptions.QuestionException;
import services.QuestionService;

/**
 * Servlet implementation class DeleteQuestionnaire
 */
@WebServlet("/DeleteQuestionnaire")
public class DeleteQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/QuestionService")
	private QuestionService qs;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteQuestionnaire() {
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
		ServletContext servletContext = getServletContext();
		HttpSession session = request.getSession();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		if(session.isNew() || session.getAttribute("user") == null || ((User) session.getAttribute("user")).getAdmin() == 0) {
			path = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(path);
		}
		else {
			int product_id = Integer.parseInt((String)request.getParameter("id"));
			
			try {
				qs.deleteQuestionnaire(product_id);
			} catch (QuestionException e) {
				path = "/WEB-INF/deleteQuestionnaire.html";
				ctx.setVariable("user", ((User)session.getAttribute("user")));
				ctx.setVariable("delete_error", 1);
				ctx.setVariable("success", 0);
				templateEngine.process(path, ctx, response.getWriter());
			}
			
			path = "/WEB-INF/deleteQuestionnaire.html";
			ctx.setVariable("user", ((User)session.getAttribute("user")));
			ctx.setVariable("delete_error", 0);
			ctx.setVariable("success", 1);
			templateEngine.process(path, ctx, response.getWriter());
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