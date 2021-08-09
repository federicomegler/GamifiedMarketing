package services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.OffensiveWord;

@Stateless
public class OffensiveWordService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	EntityManager em;
	//this is the query: select word from gamifiedmarketingdb.offensiveword where "this is a string that contain a bad word" like concat("%", word, "%");
	
	
}
