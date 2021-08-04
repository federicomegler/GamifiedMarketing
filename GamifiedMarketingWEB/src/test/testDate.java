package test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import exceptions.ProductException;
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
    /**
     * @see HttpServlet#HttpServlet()
     */
    public testDate() {
        super();
        // TODO Auto-generated constructor stub
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
		
		try {
			qs.insertQuestionOfProduct("puntodidomanda ciao", 1);
		} catch (ProductException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.getWriter().append("Served at: ").append("vediamo se va");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
