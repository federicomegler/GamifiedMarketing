package services;



import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import entities.Product;
import entities.Question;
import exceptions.ProductException;

@Stateless
public class QuestionService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	public void insertQuestionOfProduct(String content, int productID ) throws ProductException
	{
		try {
			Question q = new Question();
			Product p=em.find(Product.class, productID);
			q.setContent(content);
			q.setProductQuestion(p);
			em.persist(q);
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to get the product of the day");
		}
	}
}
