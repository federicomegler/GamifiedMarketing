package services;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.Product;

@Stateless
public class ProductService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	public Product getProductOfTheDay() {
		Product product = null;
		product = em.createNamedQuery("Product.getProductOfTheDay", Product.class).getSingleResult();
		return product;
	}
	
	public void insertNewProduct(String name, byte[] image) {
		Product product = new Product();
		Date date = new Date(System.currentTimeMillis());
		product.setName(name);
		product.setDate(date);
		product.setImage(image);
		em.persist(product);
	}
}
