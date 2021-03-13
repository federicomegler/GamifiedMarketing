package controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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
				String salt = generateSalt();
				try {
					String hash_password = get_SHA_512_Password(password, salt);
					user = us.registerUser(username, email, hash_password, salt);
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
	
	
	// Storing the plain password in the DB is unsecure, so it's better to store the hash. 
	// The salt is randomly generated and it's used to avoid that two identical password generates the same hash
	public String generateSalt() {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[64];
		random.nextBytes(salt);
		return salt.toString();
	}
	
	public String get_SHA_512_Password(String password, String salt) throws NoSuchAlgorithmException{
	    String generatedPassword = null;
	    
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt.getBytes(StandardCharsets.UTF_8));
        byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        
        for(int i=0; i< bytes.length ;i++){
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        generatedPassword = sb.toString();
	    return generatedPassword;
	}
}
