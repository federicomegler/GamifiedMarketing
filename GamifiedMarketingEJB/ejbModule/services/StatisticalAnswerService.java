package services;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.Product;
import entities.StatisticalAnswer;
import entities.User;

@Stateless
public class StatisticalAnswerService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	public void insertStatisticalAnswer(int age, char gender, int expertiseLevel, int productID, String username)
	{
		User u=em.find(User.class, username);
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
