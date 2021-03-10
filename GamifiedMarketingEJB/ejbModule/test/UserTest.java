package test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import entities.User;

public class UserTest {
	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("GamifiedMarketingEJB");
		EntityManager em = emf.createEntityManager();
		
		//Testing the namede query checkCredentials
		List<User> users = em.createNamedQuery("User.checkCredentials", User.class).setParameter(1, "fedemeg").setParameter(2, "federico").getResultList();
		System.out.println(users.get(0).getUsername());
		
		em.close();
		emf.close();
	}
}
