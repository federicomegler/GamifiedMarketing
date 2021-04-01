package services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import entities.Review;
import entities.User;
import exceptions.ReviewException;

@Stateless
public class ReviewService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;
	
	public ReviewService() {}
	
	public List<Review> getReviewsOfProductOfTheDay() throws ReviewException{
		List<Review> reviews = null;
		try {
			reviews = em.createNamedQuery("Review.getReviewsOfProductOfTheDay", Review.class).getResultList();
		}
		catch(PersistenceException e) {
			throw new ReviewException("Unable to load reviews");
		}
		return reviews;
	}
	
	public void createComment(String comment, String username, String ean) throws ReviewException {
		Review review = new Review();
		try {
			User user = em.find(User.class, username);
			review.setContent(comment);
			review.setEan(ean);
			review.setUser(user);
			em.persist(review);
		}
		catch(PersistenceException e) {
			throw new ReviewException("Unable to create comment");
		}
	}
}
