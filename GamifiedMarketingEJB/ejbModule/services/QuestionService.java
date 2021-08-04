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
	
	public Map<String, Map<String,String>> getQuestionsByProductId(int id){
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
