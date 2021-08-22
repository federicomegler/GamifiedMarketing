package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import entities.Answer;
import entities.Product;
import entities.Question;
import javax.persistence.PersistenceException;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import exceptions.ProductException;
import exceptions.QuestionException;

@Stateless
public class QuestionService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	public Map<String, Map<String,String>> getQNAByProductId(int id) throws ProductException, QuestionException {
		Map<String, Map<String,String>> qna = new HashMap<String, Map<String,String>>();
		List<Question> questions = new ArrayList<Question>();
		
		Product product = null;
		try{
			product = em.find(Product.class, id);
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to find product");
		}
		
		try{
			questions = em.createNamedQuery("Question.getQuestionsByProductId", Question.class).setParameter("product", product).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
		}
		catch(PersistenceException e) {
			throw new QuestionException("Unable to get questions");
		}
		
		
		for(int i=0; i < questions.size(); ++i) {
			List<Answer> answers = questions.get(i).getAnswers();
			Map<String,String> userAnswer = new HashMap<String, String>();
			for(int j=0; j < answers.size(); ++j) {
				System.out.println(j + answers.get(j).getUser().getUsername() + answers.get(j).getContent());
				userAnswer.put(answers.get(j).getUser().getUsername(), answers.get(j).getContent());
			}
			System.out.println(questions.get(i).getContent());
			qna.put(questions.get(i).getContent(), userAnswer);
		}
		
		return qna;
	}
	
	
	public Map<String, String> getQNAByProductAndUser(int id, String username) throws ProductException, QuestionException {
		Product product = null;
		
		try{
			product = em.find(Product.class, id);
		}
		catch(PersistenceException e) {
			throw new ProductException("Unable to find product");
		}
		Map<String,String> qna = new HashMap<String, String>();
		
		List<Question> questions = new ArrayList<Question>();
		try{
			questions = em.createNamedQuery("Question.getQuestionsByProductId", Question.class).setParameter("product", product).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
		}
		catch(PersistenceException e) {
			throw new QuestionException("Unable to get questions");
		}
		
		for(int i=0; i < questions.size(); ++i) {
			List<Answer> answers = questions.get(i).getAnswers();
			for(int j=0; j < answers.size(); ++j) {
				if(answers.get(j).getUser().getUsername().equals(username)) {
					System.out.println(j + answers.get(j).getUser().getUsername() + answers.get(j).getContent());
					qna.put(questions.get(i).getContent(), answers.get(j).getContent());
				}
				
			}
		}
		
		return qna;
	}
	
	
	public List<Question> getQuestions(int prodID) throws QuestionException, ProductException {
		List<Question> questions= new ArrayList<Question>();
		Product product = null;
		try{
			product = em.find(Product.class, prodID);
		}
		catch (PersistenceException e) {
			throw new ProductException("Unable to find product");
		}
		
		try{
			questions = em.createNamedQuery("Question.getQuestionsByProductId", Question.class).setParameter("product", product).getResultList();
		}
		catch(PersistenceException e) {
			throw new QuestionException("Unable to get questions");
		}
		return questions;
	}
	
	
	public Boolean isValid(int questionID) throws QuestionException {
		List<Question> result = null;
		try{
			result = em.createQuery("SELECT q FROM Question q WHERE q.product in (SELECT p FROM Product p WHERE p.date=CURRENT_DATE) AND q.id=:questionID",Question.class).setParameter("questionID", questionID).getResultList();
		}
		catch(PersistenceException e) {
			throw new QuestionException("Unable to get question");
		}
		
		if(result.isEmpty()) {
			return false;
		}
		return true;
	}
	
	public void deleteQuestionnaire(int prod_id) throws QuestionException, ProductException {
		List<Question> questions =  getQuestions(prod_id);
		for(int i=0; i<questions.size(); ++i) {
			try {
				em.remove(questions.get(i));
			}
			catch(PersistenceException e) {
				throw new QuestionException("Unable to remove question");
			}
		}
		
		return;
	}
	
	public void deleteMyQuestionnaire(int prod_id, String username) throws QuestionException, ProductException {
		List<Question> questions =  getQuestions(prod_id);
		for(int i=0; i<questions.size(); ++i) {
			List<Answer> answers = questions.get(i).getAnswers();
			for(int j=0; j<answers.size(); ++j) {
				if(answers.get(j).getUser().getUsername().equals(username)) {
					try {
						em.remove(answers.get(j));
					}
					catch(PersistenceException e) {
						throw new QuestionException("Unable to remove question");
					}
				}
			}
		}
		
		return;
	}
}
