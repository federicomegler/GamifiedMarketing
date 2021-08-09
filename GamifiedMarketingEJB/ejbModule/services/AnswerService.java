package services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import entities.Answer;
import entities.Product;
import entities.Question;
import entities.User;
import exceptions.OffensiveWordException;
import exceptions.ProductException;

@Stateless
public class AnswerService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	public void insertAnswer(String content, int questionID, String username ) throws ProductException
	{
		try {
			Answer a = new Answer();
			Question q=em.find(Question.class, questionID);
			User u= em.find(User.class, username);
			a.setContent(content);
			a.setQuestion(q);
			a.setUser(u);
			em.persist(a);
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to get the product of the day");
		}
	}
	
	public void insertAnswers(List<String> content, List<Integer> questionID, List<String> usernames, int num_quest) throws ProductException, OffensiveWordException {
		
		for(int i=0; i<num_quest; ++i) {
			try {
				Answer a = new Answer();
				Question q=em.find(Question.class, questionID.get(i));
				User u= em.find(User.class, usernames.get(i));
				a.setContent(content.get(i));
				a.setQuestion(q);
				a.setUser(u);
				em.persist(a);
				em.flush();
			}
			catch(PersistenceException e) {
				Throwable t = ExceptionUtils.getRootCause(e);

	            if (t instanceof SQLException) {
	                SQLException exception = (SQLException) t;

	                if (exception.getSQLState().equals("45000")) {
	                    throw new OffensiveWordException("Offensive word detected.");
	                }
	            }
			}
		}
		
		
	}
}






