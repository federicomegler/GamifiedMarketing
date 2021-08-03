package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import entities.Product;
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
	
	public void insertNewProduct(String name, byte[] image, String ean, java.util.Date date) throws ProductException {
		
		try {
			Product product = new Product();
			
			product.setName(name);
			product.setDate(date);
			product.setImage(image);
			product.setEan(ean);
			em.persist(product);
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to get the product of the day");
		}
		
	}
	
	public List<Product> getAllProducts() throws ProductException{
		List<Product> products = new ArrayList<Product>();
		try {
			products = em.createNamedQuery("Product.getAllProducts", Product.class).getResultList();
		}
		catch(PersistenceException e){
			throw new ProductException("Unable to get products");
		}
		return products;
	}
	
}
