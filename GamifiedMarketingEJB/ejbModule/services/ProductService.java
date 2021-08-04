package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import entities.Answer;
import entities.Product;
import entities.Question;
import entities.User;
import exceptions.ProductException;

@Stateless
public class ProductService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	public Product getProductOfTheDay() throws ProductException{
		List<Product> products = null;
		try {
			
			products = em.createNamedQuery("Product.getProductOfTheDay", Product.class).getResultList();
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to get the product of the day");
		}
		if(products == null) {
			return null;
		}
		else if(products.isEmpty())
			return null;
		else
			return products.get(0);
	}
	
	public int insertNewProduct(String name, byte[] image, String ean, java.util.Date date) throws ProductException {
		
		try {
			Product product = new Product();
			
			product.setName(name);
			product.setDate(date);
			product.setImage(image);
			product.setEan(ean);
			em.persist(product);
			em.flush();
			return product.getId();
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to get the product of the day");
		}
		
	}
	
	public List<Product> getAllProducts() throws ProductException{
		List<Product> products = new ArrayList<Product>();
		try {
			products = em.createNamedQuery("Product.getAllProducts", Product.class).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
		}
		catch(PersistenceException e){
			throw new ProductException("Unable to get products");
		}
		return products;
	}
	
	
	public List<String> getUserSubmit(int id) {
		List<String> users = new ArrayList<String>();
		
		Product product = em.find(Product.class, id);
		
		
		List<User> part = em.createQuery("SELECT DISTINCT(a.user_answer) FROM Answer a JOIN Question q ON (a.question = q) WHERE q.product = :prod", User.class).setParameter("prod", product).getResultList();
		for(int j=0; j< part.size(); ++j) {
			users.add(part.get(j).getUsername());
		}
		
		return users;
	}
	
	public List<String> getUserCancelled(int id, List<String> usersSubmit){
		List<User> users = new ArrayList<User>();
		Product product = em.find(Product.class, id);
		
		users = em.createQuery("SELECT l.user_log FROM Log l WHERE l.product_log = :prod AND l.user_log NOT IN (SELECT DISTINCT(a.user_answer) FROM Answer a JOIN Question q ON (a.question = q) WHERE q.product = :prod)", User.class).setParameter("prod", product).getResultList();
		List<String> usernames = new ArrayList<String>();
		for(int i=0; i< users.size(); ++i) {
			usernames.add(users.get(i).getUsername());
		}
		
		return usernames;
	}
	
}
