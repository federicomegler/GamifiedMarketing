package services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import entities.User;
import exceptions.CredentialsException;
import exceptions.RegistrationException;
import exceptions.StatisticsException;

@Stateless
public class UserService {
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;

	public UserService() {}
	
	public User checkCredentials(String name, String password) throws CredentialsException, NonUniqueResultException{
		List<User> users = null;
		
		try {
			users = em.createNamedQuery("User.checkCredentials", User.class).setParameter("name", name).setParameter("password", password).getResultList();
		} catch (PersistenceException e) {
			throw new CredentialsException("Failed to login");
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
	
	public String getSalt(String name) throws CredentialsException {
		List<String> salts = null;
		try {
			salts = em.createNamedQuery("User.getSalt", String.class).setParameter("name", name).getResultList();
		}
		catch(PersistenceException e) {
			throw new CredentialsException("Unable to get salt! Server error");
		}
		
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
	
	public List<User> getLeaderboard() throws StatisticsException{
		List<User> users = null;
		try {
			users = em.createNamedQuery("User.getLeaderboard", User.class).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
		}
		catch(PersistenceException e) {
			throw new StatisticsException("Unable to load leaderboard");
		}
		
		return users;
	}
	
	public User updatePassword(String password, String username) throws CredentialsException {
		
		User user = null;
		try {
		user = em.find(User.class, username);
		user.setPassword(password);
		em.merge(user);
		}
		catch(PersistenceException e) {
			throw new CredentialsException("Unable to change password! Server error");
		}
		return user;
	}
	
	public User banUser(String username) throws CredentialsException {
		User user = null;
		try {
			user = em.find(User.class, username);
			user.setBan(1);
			em.merge(user);
		}
		catch(PersistenceException e) {
			throw new CredentialsException("Unable to ban user! Server error");
		}
		
		return user;
	}
	
	public User refreshUser(User user) throws CredentialsException {
		try {
			user = em.find(User.class, user.getUsername());
		}
		catch(PersistenceException e) {
			throw new CredentialsException("Unable to refresh user");
		}
		return user;
	}
}
