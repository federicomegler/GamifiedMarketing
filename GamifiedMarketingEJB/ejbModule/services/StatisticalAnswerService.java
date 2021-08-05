package services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.Product;
import entities.StatisticalAnswer;
import entities.User;

public class StatisticalAnswerService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	public void insertStatisticalAnswer(int age, char gender, int expertiseLevel, int productID, int userID)
	{
		User u=em.find(User.class, userID);
		Product p= em.find(Product.class, productID);
		StatisticalAnswer sa=new StatisticalAnswer();
		sa.setAge(age);
		sa.setExpertise_level(expertiseLevel);
		sa.setGender(gender);
		sa.setProduct(p);
		sa.setUser(u);
		em.persist(sa);
	}
}
