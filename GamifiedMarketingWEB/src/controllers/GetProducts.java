package controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import entities.Product;
import entities.User;
import exceptions.ProductException;
import services.ProductService;

/**
 * Servlet implementation class getProductInfo
 */
@WebServlet("/GetProducts")
public class GetProducts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "services/ProductService")
	private ProductService ps;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetProducts() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if(session.isNew() || session.getAttribute("user") == null) {
			String path = getServletContext().getContextPath();
			response.sendRedirect(path + "/index.html");
		}
		else{
			List<Product> products = null;
			try {
				products = ps.getAllProducts();
			} catch (ProductException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<List<String>> table = new ArrayList<>();
		    
			for(int i=0; i<products.size(); ++i) {
				List<String> elements = new ArrayList<String>();
				elements.add(Integer.toString(products.get(i).getId()));
				elements.add(products.get(i).getName());
				elements.add(new SimpleDateFormat("dd-MM-yyyy").format(products.get(i).getDate()));
				elements.add(products.get(i).getEan());
				table.add(elements);
			}
			
		    String json = new Gson().toJson(table);
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(json);
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
