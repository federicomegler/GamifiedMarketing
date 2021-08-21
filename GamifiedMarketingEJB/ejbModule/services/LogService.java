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
import exceptions.CredentialsException;
import exceptions.LogException;
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
	
	public Boolean alreadyLogged(String username) throws CredentialsException, LogException {
		
		User user = null;
		try{
			user = em.find(User.class, username);
		}
		catch(PersistenceException e) {
			throw new CredentialsException("Unable to get user");
		}
		
		List<Log> l = null;
		try{
			l = em.createNamedQuery("Log.alreadyLogged", Log.class).setParameter("user", user).getResultList();
		}
		catch(PersistenceException e) {
			throw new LogException("Unable to get log");
		}
		
		if(l.isEmpty())
			return false;
		else
			return true;
		
	}
	
}
