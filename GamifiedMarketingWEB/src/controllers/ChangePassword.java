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
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import entities.User;
import services.UserService;
import utils.LoginUtils;

/**
 * Servlet implementation class ChangePassword
 */
@WebServlet("/ChangePassword")
public class ChangePassword extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "services/UserService")
	private UserService us;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangePassword() {
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
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if(session.isNew() || session.getAttribute("user") == null) {
			String path = getServletContext().getContextPath();
			response.sendRedirect(path + "/index.html");
		}
		else{
			User user = null;
			final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
			if(request.getParameter("password") == null || request.getParameter("oldpassword") == null || request.getParameter("confirmpassword") == null) {
				ctx.setVariable("result", -1);
				ctx.setVariable("resultmsg", "New passwords do not match!");
				ctx.setVariable("user", (User)session.getAttribute("user"));
				String path = "/WEB-INF/Profile.html";
				templateEngine.process(path, ctx, response.getWriter());
			}
			else {
				if(((String)request.getParameter("password")).length() < 8 || !((String)request.getParameter("password")).equals(((String)request.getParameter("confirmpassword")))) {
					ctx.setVariable("result", -1);
					ctx.setVariable("resultmsg", "New passwords do not match!");
					ctx.setVariable("user", (User)session.getAttribute("user"));
					String path = "/WEB-INF/Profile.html";
					templateEngine.process(path, ctx, response.getWriter());
				}
				else {
				String oldpassword = (String)request.getParameter("oldpassword");
				
				try {
					user = us.checkCredentials(((User)session.getAttribute("user")).getUsername(), LoginUtils.get_SHA_512_Password(oldpassword, ((User)session.getAttribute("user")).getSalt()));
					System.out.println(user);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(user == null) {
					ctx.setVariable("result", -1);
					ctx.setVariable("user", (User)session.getAttribute("user"));
					ctx.setVariable("resultmsg", "Wrong old password!");
					String path = "/WEB-INF/Profile.html";
					templateEngine.process(path, ctx, response.getWriter());
				}
				else {
					String newpassword = null;
					try {
						newpassword = LoginUtils.get_SHA_512_Password((String)request.getParameter("password"), user.getSalt());
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
					
					
					user = us.updatePassword(newpassword, user.getUsername());
					
					if(user != null && newpassword != null) {
						session.setAttribute("user", user);
						ctx.setVariable("result", 1);
						ctx.setVariable("user", (User)session.getAttribute("user"));
						ctx.setVariable("resultmsg", "Success!");
						String path = "/WEB-INF/Profile.html";
						templateEngine.process(path, ctx, response.getWriter());
					}
					else {
						ctx.setVariable("result", -1);
						ctx.setVariable("user", (User)session.getAttribute("user"));
						ctx.setVariable("resultmsg", "Server error! Try again.");
						String path = "/WEB-INF/Profile.html";
						templateEngine.process(path, ctx, response.getWriter());
					}
				}
			}
			}
			
		}
		
	}

}
