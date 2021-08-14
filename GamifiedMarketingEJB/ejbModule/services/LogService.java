package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

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
		
		try {
			em.persist(log);
		}
		catch (PersistenceException e) {
			throw new RuntimeException("rollback");
		}
	}
	
	public Boolean alreadyLogged(String username) {
		
		User user = em.find(User.class, username);
		
		List<Log> l = em.createNamedQuery("Log.alreadyLogged", Log.class).setParameter("user", user).getResultList();
		
		if(l.isEmpty())
			return false;
		else
			return true;
		
	}
	
}
