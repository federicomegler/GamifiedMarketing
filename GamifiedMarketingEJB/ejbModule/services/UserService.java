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
	
	public User checkCredentials(String username, String password) throws Exception{
		List<User> users = null;
		try {
			System.out.println("entro");
			users = em.createNamedQuery("User.checkCredentials", User.class).setParameter(1, username).setParameter(2, password).getResultList();
			System.out.println(users.isEmpty());
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
