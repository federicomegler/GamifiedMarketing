package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import org.apache.commons.lang.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import entities.User;
import exceptions.ProductException;
import services.ProductService;
import services.QuestionService;
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
	private ProductService ps;
	@EJB(name="service/QuestionService")
	private QuestionService qs;
       
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
		
		
		if(session.isNew() || session.getAttribute("user") == null) {
			path = getServletContext().getContextPath();
			response.sendRedirect(path + "/index.html");
		}
		else {
			
			//per aggiungere un prodotto serve essere admin
			if(((User)session.getAttribute("user")).getAdmin() == 0) {
				response.sendRedirect("Home");
			}
			else {
				final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
				
				Part image = request.getPart("prodImage");
				String prodName = request.getParameter("productName");
				String EAN = request.getParameter("productEAN");
				String questionNumber = request.getParameter("questionNumber");
				String date = request.getParameter("date");
				
				//controllo che gli input non siano vuoti e che siano validi
				if(image == null || prodName == null || EAN == null || questionNumber == null || date == null
						|| prodName.isBlank() || EAN.isBlank() || questionNumber.isBlank() || date.isBlank() || !StringUtils.isNumeric(questionNumber) || 
						Integer.parseInt(questionNumber) < 1) {
					path = "/WEB-INF/ProductCreation.html";
					ctx.setVariable("user", ((User)session.getAttribute("user")));
					ctx.setVariable("error", "Invalid data entered.");
					templateEngine.process(path, ctx, response.getWriter());
				}
				else {
					//controllo formato immagine
					if(!image.getContentType().contains("image")) {
						path = "/WEB-INF/ProductCreation.html";
						ctx.setVariable("user", ((User)session.getAttribute("user")));
						ctx.setVariable("error", "Invalid image format.");
						templateEngine.process(path, ctx, response.getWriter());
					}
					else {
						int qn = Integer.parseInt(questionNumber);
						
						Date questionnaireDate = new Date();
						
						try {
							questionnaireDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
							
						} catch (ParseException e) {
							path = "/WEB-INF/ProductCreation.html";
							ctx.setVariable("user", ((User)session.getAttribute("user")));
							ctx.setVariable("error", "Invalid date format.");
							templateEngine.process(path, ctx, response.getWriter());
							return;
						}
						
						Calendar now = Calendar.getInstance();
						now.set(Calendar.HOUR_OF_DAY,0);
						now.set(Calendar.MINUTE,0);
						now.set(Calendar.SECOND,0);
						now.set(Calendar.MILLISECOND,0);
						
						//controllo data
						if(questionnaireDate.before(now.getTime())) {
							path = "/WEB-INF/ProductCreation.html";
							ctx.setVariable("user", ((User)session.getAttribute("user")));
							ctx.setVariable("error", "You cannot add a product on a date earlier than today.");
							templateEngine.process(path, ctx, response.getWriter());
							return;
						}
						try {
							if(ps.alreadyInserted(questionnaireDate)) {
								path = "/WEB-INF/ProductCreation.html";
								ctx.setVariable("user", ((User)session.getAttribute("user")));
								ctx.setVariable("error", "A product already exists for that date.");
								templateEngine.process(path, ctx, response.getWriter());
								return;
							}
						} catch (ProductException | IOException e1) {
							path = "/WEB-INF/ProductCreation.html";
							ctx.setVariable("user", ((User)session.getAttribute("user")));
							ctx.setVariable("error", "Unable to insert product! Database error.");
							templateEngine.process(path, ctx, response.getWriter());
							return;
						}
						
						
						InputStream imageData = image.getInputStream();
						byte[] imageBytes = ImageUtils.readImage(imageData);
						
						if(imageBytes.length == 0) {
							path = "/WEB-INF/ProductCreation.html";
							ctx.setVariable("user", ((User)session.getAttribute("user")));
							ctx.setVariable("error", "The image is empty.");
							templateEngine.process(path, ctx, response.getWriter());
						}
						else {
							try {
								List<String> questions = new ArrayList<String>();
								
								for(int i=1; i<=qn; i++) {
									String content = request.getParameter("Question"+Integer.toString(i));
									if(content == null || content.isBlank()) {
										path = "/WEB-INF/ProductCreation.html";
										ctx.setVariable("user", ((User)session.getAttribute("user")));
										ctx.setVariable("error", "You cannot insert a blank question.");
										templateEngine.process(path, ctx, response.getWriter());
										return;
									}
									questions.add(content);
								}
								
								ps.insertNewProduct(prodName, imageBytes, EAN, questionnaireDate, questions);
								
							} catch (ProductException e) {
								path = "/WEB-INF/ProductCreation.html";
								ctx.setVariable("error", "Unable to insert new product.");
								ctx.setVariable("user", session.getAttribute("user"));
								path = "WEB-INF/Home.html";
								templateEngine.process(path, ctx, response.getWriter());
								return;
							}
							response.sendRedirect("Home");
							
						}
					}
				}
			}
		}
	}
}
