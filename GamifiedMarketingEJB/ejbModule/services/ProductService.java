package services;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.Product;

@Stateless
public class ProductService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	public Product getProductOfTheDay() {
		List<Product> products;
		products = em.createNamedQuery("Product.getProductOfTheDay", Product.class).getResultList();
		if(products.isEmpty())
			return null;
		else
			return products.get(0);
	}
	
	public void insertNewProduct(String name, byte[] image, String ean) {
		Product product = new Product();
		Date date = new Date(System.currentTimeMillis());
		product.setName(name);
		product.setDate(date);
		product.setImage(image);
		product.setEan(ean);
		em.persist(product);
	}
}
