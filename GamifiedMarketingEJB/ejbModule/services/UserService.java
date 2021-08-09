package services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.security.auth.login.CredentialException;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import entities.User;
import exceptions.CredentialsException;
import exceptions.RegistrationException;

@Stateless
public class UserService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;

	public UserService() {}
	
	public User checkCredentials(String name, String password) throws CredentialException, NonUniqueResultException{
		List<User> users = null;
		
		try {
			users = em.createNamedQuery("User.checkCredentials", User.class).setParameter("name", name).setParameter("password", password).getResultList();
		} catch (PersistenceException e) {
			throw new CredentialException("Failed to login");
		}
		if(users.isEmpty()) {
			return null;
		}
		else if(users.size() == 1) {
			return users.get(0);
		}
		throw new NonUniqueResultException("More than one user registered with same credentials");
	}
	
	public User registerUser(String username, String email, String password, String salt) throws RegistrationException{
		try {
			User user = new User(username, email, password, salt);
			em.persist(user);
			return user;
		}
		catch(PersistenceException e) {
			throw new RegistrationException("Registration failed");
		}
		
	}
	
	public String getSalt(String name) {
		List<String> salts = em.createNamedQuery("User.getSalt", String.class).setParameter("name", name).getResultList();
		if(!salts.isEmpty()) {
			return salts.get(0);
		}
		return null;
	}
	
	
	//This method is used by Register servlet in order to reject registretions with an existing username or email (avoid rise of exception for primary key)	
	public boolean existCredentials(String username, String email) throws CredentialsException{
		List<User> users = null;
		try {
			users = em.createNamedQuery("User.existCredentials", User.class).setParameter("username", username).setParameter("email", email).getResultList();
		} catch (PersistenceException e) {
			throw new CredentialsException("Unable to register! Server error");
		}
		if(users.isEmpty()) {
			return false;
		}
		return true;
	}
	
	public List<User> getLeaderboard(){
		List<User> users = em.createNamedQuery("User.getLeaderboard", User.class).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
		return users;
	}
	
	public User updatePassword(String password, String username) {
		User user = em.find(User.class, username);
		user.setPassword(password);
		em.merge(user);
		return user;
	}
	
	public User banUser(String username) {
		User user = em.find(User.class, username);
		user.setBan(1);
		em.merge(user);
		return user;
	}
}
