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
	
	
}
