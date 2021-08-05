package test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import entities.Product;
import entities.Question;
import entities.User;
import exceptions.ProductException;
import services.ProductService;
import services.QuestionService;

/**
 * Servlet implementation class testDate
 */
@WebServlet("/testDate")
@MultipartConfig
public class testDate extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name="service/QuestionService")
    private QuestionService qs;
	@EJB(name="service/ProductService")
    private ProductService ps;
	private TemplateEngine templateEngine;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public testDate() {
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
		//Part image = request.getPart("prodImage");
		/*java.util.Date questionnaireDate= new java.util.Date();
		Calendar cal= Calendar.getInstance();
		try {
			questionnaireDate = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("date"));
			cal.setTime(questionnaireDate);
			cal.set(Calendar.HOUR_OF_DAY, 12);
			questionnaireDate= cal.getTime();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		};*/
		
		/*try {
			qs.insertQuestionOfProduct("puntodidomanda ciao", 1);
		} catch (ProductException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.getWriter().append("Served at: ").append("vediamo se va");*/
		
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		Product p= null;
		try {
			p= ps.getProductOfTheDay();
		} catch (ProductException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(p==null)
		{
			response.getWriter().append("Served at: ").append("non ci sono prodotti del giorno");
		}else
		{
		List<Question> questions=qs.getQuestions(p.getId());
		
		ctx.setVariable("questions", questions);		
		
		String path = "/WEB-INF/Wizard.html";
		templateEngine.process(path, ctx, response.getWriter());
		//response.getWriter().append("Served at: ").append(questions.get(1).getContent());
		}
		
		/*String path = "/WEB-INF/Wizard.html";
		RequestDispatcher rd=request.getRequestDispatcher(path);
		rd.forward(request, response);*/
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
