package services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import entities.Answer;
import entities.Product;
import entities.Question;
import entities.User;
import exceptions.ProductException;

@Stateless
public class AnswerService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	public void insertAnswer(String content, int questionID, int userID ) throws ProductException
	{
		try {
			Answer a = new Answer();
			Question q=em.find(Question.class, questionID);
			User u= em.find(User.class, userID);
			a.setContent(content);
			a.setQuestion(q);
			a.setUser(u);
			em.persist(a);
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to get the product of the day");
		}
	}
}
