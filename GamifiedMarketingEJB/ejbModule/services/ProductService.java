package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.security.auth.login.CredentialException;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import entities.Answer;
import entities.Product;
import entities.Question;
import entities.User;
import exceptions.CredentialsException;
import exceptions.ProductException;

@Stateless
public class ProductService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	public Boolean alreadyInserted(Date date) throws ProductException {
		List<Product> products = new ArrayList<Product>();
		
		try{
			products = em.createQuery("SELECT p FROM Product p WHERE p.date = :date", Product.class).setParameter("date", date).getResultList();
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to get product");
		}
		
		if(products.isEmpty()) {
			return false;
		}
		return true;
	}
	
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
	
	public void insertNewProduct(String name, byte[] image, String ean, Date date, List<String> questions) throws ProductException {
		
		try {
			Product product = new Product();
			List<Question> questionList = new ArrayList<Question>();
			
			product.setName(name);
			product.setDate(date);
			product.setImage(image);
			product.setEan(ean);
			
			for(int i=0; i<questions.size(); ++i) {
				Question question = new Question();
				question.setContent(questions.get(i));
				question.setProductQuestion(product);
				questionList.add(question);
			}
			
			product.setQuestions(questionList);
			
			em.persist(product);
			em.flush();
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to insert the product of the day");
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
	
	
	public List<String> getUserSubmit(int id) throws ProductException {
		List<String> users = new ArrayList<String>();
		
		Product product = null;
		
		try{
			product = em.find(Product.class, id);
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to get product");
		}
		
		
		List<User> part = null;
		
		try{
			part = em.createQuery("SELECT DISTINCT(a.user_answer) FROM Answer a INNER JOIN a.question q WHERE q.product = :prod", User.class).setParameter("prod", product).getResultList();
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to get submits");
		}
		for(int j=0; j< part.size(); ++j) {
			users.add(part.get(j).getUsername());
		}
		
		return users;
	}
	
	
	public List<String> getUserCancelled(int id, List<String> usersSubmit) throws ProductException {
		List<User> users = new ArrayList<User>();
		Product product = null;
		
		try{
			product = em.find(Product.class, id);
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to get product");
		}
		
		try{
			users = em.createQuery("SELECT l.user_log FROM Log l WHERE l.product_log = :prod AND l.user_log NOT IN (SELECT DISTINCT(a.user_answer) FROM Answer a INNER JOIN a.question q WHERE q.product = :prod)", User.class).setParameter("prod", product).getResultList();
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to get deletion");
		}
		
		List<String> usernames = new ArrayList<String>();
		for(int i=0; i< users.size(); ++i) {
			usernames.add(users.get(i).getUsername());
		}
		
		return usernames;
	}
	
	public List<Product> getUserProducts(String username) throws CredentialsException, ProductException {
		List<Product> products = new ArrayList<Product>();
		
		User user = null;
		try{
			user = em.find(User.class, username);
		}
		catch(PersistenceException e) {
			throw new CredentialsException("Unable to get user");
		}
		
		try{
			products = em.createQuery("SELECT DISTINCT(p) FROM Product p INNER JOIN p.questions q INNER JOIN q.answers a WHERE a.user_answer = :user", Product.class).setParameter("user", user).getResultList();
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to get products");
		}
		
		return products;
	}
}
