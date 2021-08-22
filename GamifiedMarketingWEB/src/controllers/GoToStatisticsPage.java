package controllers;

import java.io.IOException;

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
import exceptions.StatisticsException;
import services.StatsService;

/**
 * Servlet implementation class GoToStatisticsPage
 */
@WebServlet("/GoToStatisticsPage")
public class GoToStatisticsPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    @EJB(name = "services/StatsService")
    private StatsService ss;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToStatisticsPage() {
        super();
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
		HttpSession session = request.getSession();
		if(session.isNew() || session.getAttribute("user") == null) {
			String path = getServletContext().getContextPath();
			response.sendRedirect(path + "/index.html");
		}
		else{
			if(((User)session.getAttribute("user")).getAdmin() == 0) {
				response.sendRedirect("Home");
			}
			else {
				String path = "/WEB-INF/Statistics.html";
				final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
				Double avgExp = null;
				Double avgAge = null;
				Long totalLogs = null;
				try {
					avgExp = ss.getAvgEXP();
					avgAge = ss.getAvgAge();
					totalLogs = ss.getTotalLogs();
				}
				catch(StatisticsException e) {
					ctx.setVariable("user", ((User)session.getAttribute("user")));
					ctx.setVariable("avgAge", "Failed to load");
					ctx.setVariable("avgExp", "Failed to load");
					ctx.setVariable("totalLogs", "Failed to load");
					templateEngine.process(path, ctx, response.getWriter());
					return;
				}
				
				
				if(avgExp < 0.5) {
					ctx.setVariable("avgExp", "Low");
				}
				else if(avgExp >= 0.5 && avgExp < 1.5) {
					ctx.setVariable("avgExp", "Medium");
				}
				else {
					ctx.setVariable("avgExp", "High");
				}
				
				
				
				ctx.setVariable("user", ((User)session.getAttribute("user")));
				ctx.setVariable("avgAge", avgAge);
				ctx.setVariable("totalLogs", totalLogs);
				templateEngine.process(path, ctx, response.getWriter());
			}
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
