package services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.Review;

@Stateless
public class ReviewService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;
	
	public ReviewService() {}
	
	public List<Review> getReviewsOfProductOfTheDay() {
		List<Review> reviews = em.createNamedQuery("Review.getReviewsOfProductOfTheDay", Review.class).getResultList();
		return reviews;
	}
}
