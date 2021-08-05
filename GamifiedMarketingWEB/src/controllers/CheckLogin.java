package controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.ejb.EJB;
import javax.persistence.NonUniqueResultException;
import javax.security.auth.login.CredentialException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import entities.User;
import services.UserService;
import utils.LoginUtils;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "services/UserService")
    private UserService us;
	private TemplateEngine templateEngine;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckLogin() {
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
    

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "";
		User user = null;
		
		try {
			String salt = us.getSalt(request.getParameter("username"));
			if(salt != null) {
				String password = LoginUtils.get_SHA_512_Password(request.getParameter("password"), salt);
				user = us.checkCredentials(request.getParameter("username"), password);
			}
		} catch (CredentialException e) {
			final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
			path = "/index.html";
			response.getWriter().print("<script> alert('" + e.getMessage() + "') </script>");
			templateEngine.process(path, ctx, response.getWriter());
		} catch (NoSuchAlgorithmException e) {
			final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
			path = "/index.html";
			response.getWriter().print("<script> alert('server error! Try again') </script>");
			templateEngine.process(path, ctx, response.getWriter());
		}
		catch(NonUniqueResultException e){
			final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
			path = "/index.html";
			response.getWriter().print("<script> alert('" + e.getMessage() + "') </script>");
			templateEngine.process(path, ctx, response.getWriter());
		}
		
		
		if(user == null) {
			final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
			path = "/index.html";
			response.getWriter().print("<script> alert('Wrong username or password') </script>");
			templateEngine.process(path, ctx, response.getWriter());
		}
		else {
			request.getSession().setAttribute("user", user);
			path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		}
	}
}


