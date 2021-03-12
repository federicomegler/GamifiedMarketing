package services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.User;

@Stateless
public class UserService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;

	public UserService() {}
	
	public User checkCredentials(String username, String password) throws Exception{
		List<User> users = null;
		System.out.println(username);
		System.out.println(password);
		try {
			users = em.createNamedQuery("User.checkCredentials", User.class).setParameter("name", username).setParameter("password", password).getResultList();
		} catch (Exception e) {
			// TODO: handle exception with a specific class
			e.printStackTrace();
		}
		if(users == null) {
			System.out.println("error");
			return null;
		}
		if(users.isEmpty()) {
			return null;
		}
		else if(users.size() == 1) {
			return users.get(0);
		}
		
		throw new Exception();
		//TODO throw specific exception for multiple users
	}
}
