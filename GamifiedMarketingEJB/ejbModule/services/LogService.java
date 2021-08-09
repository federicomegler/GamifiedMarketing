package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.Log;
import entities.Product;
import entities.User;
import exceptions.ProductException;

@Stateless
public class LogService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	@EJB(name = "services/ProductService")
	private ProductService ps;
	
	public void insertLog(String username) throws ProductException {
		Log log = new Log();
		
		User user = em.find(User.class, username);
		Product product = ps.getProductOfTheDay();
		
		
		log.setDate(new Date());
		log.setUser_log(user);
		log.setProduct_log(product);
		em.persist(log);
	}
	
}
