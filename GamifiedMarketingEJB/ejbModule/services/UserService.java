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
	
	public User checkCredentials(String name, String password) throws Exception{
		List<User> users = null;
		System.out.println(name);
		System.out.println(password);
		try {
			users = em.createNamedQuery("User.checkCredentials", User.class).setParameter("name", name).setParameter("password", password).getResultList();
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
		existCredentials(password, password);
		throw new Exception();
		//TODO throw specific exception for multiple users
	}
	
	public User registerUser(String username, String email, String password) throws Exception{
		User user = new User(username, email, password);
		em.persist(user);
		return user;
	}
	
	
	//This method is used by Register servlet in order to reject registretions with an existing username or email (avoid rise of exception for primary key)	
	public boolean existCredentials(String username, String email) throws Exception{
		List<User> users = null;
		try {
			users = em.createNamedQuery("User.existCredentials", User.class).setParameter("username", username).setParameter("email", email).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(users.isEmpty()) {
			return false;
		}
		return true;
	}
}
