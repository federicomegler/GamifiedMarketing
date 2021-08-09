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
import entities.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;

import entities.Product;
import entities.Question;
import exceptions.ProductException;
import exceptions.QuestionException;

@Stateless
public class QuestionService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	
	public Map<String, Map<String,String>> getQNAByProductId(int id){
		Map<String, Map<String,String>> qna = new HashMap<String, Map<String,String>>();
		List<Question> questions = new ArrayList<Question>();
		
		Product product = em.find(Product.class, id);
		
		questions = em.createNamedQuery("Question.getQuestionsByProductId", Question.class).setParameter("product", product).getResultList();
		
		
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
	
	
	public Map<String, String> getQNAByProductAndUser(int id, String username) {
		Product product = em.find(Product.class, id);
		Map<String,String> qna = new HashMap<String, String>();
		
		List<Question> questions = new ArrayList<Question>();
		questions = em.createNamedQuery("Question.getQuestionsByProductId", Question.class).setParameter("product", product).getResultList();
		
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
	
	
	public List<Question> getQuestions(int prodID) {
		List<Question> questions= new ArrayList<Question>();
		Product product = em.find(Product.class, prodID);
		questions = em.createNamedQuery("Question.getQuestionsByProductId", Question.class).setParameter("product", product).getResultList();
		return questions;
	}
	
	public void insertQuestionOfProduct(String content, int productID ) throws ProductException {
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
	
	
	public Boolean isValid(int questionID) {
		List<Question> result=em.createQuery("SELECT q from Question q where q.product in (select p from Product p where p.date=CURRENT_DATE) and q.id=:questionID",Question.class).setParameter("questionID", questionID).getResultList();
		if(result.isEmpty()) {
			return false;
		}
		return true;
	}
	
	public void deleteQuestionnaire(int prod_id) throws QuestionException {
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
	
	public void deleteMyQuestionnaire(int prod_id, String username) throws QuestionException {
		List<Question> questions =  getQuestions(prod_id);
		for(int i=0; i<questions.size(); ++i) {
			List<Answer> answers = questions.get(i).getAnswers();
			for(int j=0; j<answers.size(); ++j) {
				if(answers.get(j).getUser().getUsername().equals(username)) {
					try {
						em.remove(questions.get(i));
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
