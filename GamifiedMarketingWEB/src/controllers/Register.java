package controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

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
import exceptions.CredentialsException;
import exceptions.RegistrationException;
import services.UserService;
import utils.LoginUtils;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
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
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		if(request.getParameter("username") == null || request.getParameter("email") == null || 
				request.getParameter("password") == null || request.getParameter("confirmpassword") == null
				|| request.getParameter("username").isBlank() || request.getParameter("email").isBlank() || 
				request.getParameter("password").isBlank() || request.getParameter("confirmpassword").isBlank()) {
			
			String path = "/WEB-INF/Register.html";
			ctx.setVariable("error", 1);
			ctx.setVariable("errormsg", "Missing parameters");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		
		String username = StringEscapeUtils.escapeHtml(request.getParameter("username"));
		String email = StringEscapeUtils.escapeHtml(request.getParameter("email"));
		String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		String confirm = StringEscapeUtils.escapeJava(request.getParameter("confirmpassword"));
		
		
		try {
			if(us.existCredentials(username, email)) {
				String path = "/WEB-INF/Register.html";
				ctx.setVariable("error", 1);
				ctx.setVariable("errormsg", "Username or email already exists");
				templateEngine.process(path, ctx, response.getWriter());
			}
			else if(!password.equals(confirm)) {
				String path = "/WEB-INF/Register.html";
				ctx.setVariable("error", 1);
				ctx.setVariable("errormsg", "Passwords do not match");
				templateEngine.process(path, ctx, response.getWriter());
			}
			else if(password.length() < 8) {
				String path = "/WEB-INF/Register.html";
				ctx.setVariable("error", 1);
				ctx.setVariable("errormsg", "The password is too short (min 8 characters)");
				templateEngine.process(path, ctx, response.getWriter());
			}
			else if(!LoginUtils.validate(email)) {
				String path = "/WEB-INF/Register.html";
				ctx.setVariable("error", 1);
				ctx.setVariable("errormsg", "Invalid email");
				templateEngine.process(path, ctx, response.getWriter());
			}
			else {
				User user = null;
				String salt = LoginUtils.generateSalt();
				try {
					String hash_password = LoginUtils.get_SHA_512_Password(password, salt);
					user = us.registerUser(username, email, hash_password, salt);
				} catch (RegistrationException e) {
					String path = "/WEB-INF/Register.html";
					ctx.setVariable("error", 1);
					ctx.setVariable("errormsg", e.getMessage());
					templateEngine.process(path, ctx, response.getWriter());
					return;
				} catch (NoSuchAlgorithmException e) {
					String path = "/WEB-INF/Register.html";
					ctx.setVariable("error", 1);
					ctx.setVariable("errormsg", "Server error! Try again");
					templateEngine.process(path, ctx, response.getWriter());
					return;
				}
				
				if(user == null) {
					String path = "/WEB-INF/Register.html";
					ctx.setVariable("error", 1);
					ctx.setVariable("errormsg", "Server error! Try again");
					templateEngine.process(path, ctx, response.getWriter());
				}
				else {
					request.getSession().setAttribute("user", user);
					String path = getServletContext().getContextPath() + "/Home";
					response.sendRedirect(path);
				}
			}
		} 
		catch (CredentialsException e) {
			String path = "/WEB-INF/Register.html";
			ctx.setVariable("error", 1);
			ctx.setVariable("errormsg", e.getMessage());
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
	}
}
