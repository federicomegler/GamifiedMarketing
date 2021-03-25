package controllers;

import java.io.IOException;
import java.io.InputStream;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import services.ProductService;
import services.UserService;
import utils.ImageUtils;

/**
 * Servlet implementation class CreateImage
 */
@WebServlet("/CreateProduct")
@MultipartConfig
public class CreateProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;
	TemplateEngine templateEngine;
	@EJB(name = "service/ProductService")
	ProductService ps;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateProduct() {
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
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "";
		HttpSession session = request.getSession();
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		String imageName = request.getParameter("imagename");
		Part image = request.getPart("image");
		InputStream imageData = image.getInputStream();
		byte[] imageBytes = ImageUtils.readImage(imageData);
		if(imageBytes.length == 0) {
			ctx.setVariable("error", 1);
			ctx.setVariable("user", session.getAttribute("user"));
			path = "WEB-INF/Home.html";
			templateEngine.process(path, ctx, response.getWriter());
		}
		else {
			ps.insertNewProduct(imageName, imageBytes, "123456");
			ctx.setVariable("user", session.getAttribute("user"));
			path = "Home";
			templateEngine.process(path, ctx, response.getWriter());
		}
	}

}
