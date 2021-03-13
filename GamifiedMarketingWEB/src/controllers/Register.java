package controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import entities.User;
import services.UserService;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
    TemplateEngine templateEngine;
    @EJB(name = "services/UserService")
    private UserService us;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
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
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		String email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		String confirm = StringEscapeUtils.escapeJava(request.getParameter("confirmpassword"));
		
		try {
			if(us.existCredentials(username, email) || !password.equals(confirm) || password.length() <= 8) {
				String path = "/WEB-INF/Register.html";
				request.setAttribute("error", 1);
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				templateEngine.process(path, ctx, response.getWriter());
			}
			else {
				User user = null;
				try {
					user = us.registerUser(username, email, password);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				if(user == null) {
					String path = "/WEB-INF/Register.html";
					request.setAttribute("error", 1);
					ServletContext servletContext = getServletContext();
					final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
					templateEngine.process(path, ctx, response.getWriter());
				}
				else {
					request.getSession().setAttribute("user", user);
					String path = getServletContext().getContextPath() + "/Home";
					response.sendRedirect(path);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
