package entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "User.checkCredentials", query = "SELECT u FROM User u WHERE (u.username = :name or u.email = :name) and u.password = :password")
@IdClass(UserId.class)
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	private String username;
	@Id
	private String email;
	
	private String password;
	private int ban;
	private int admin;
	
	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getBan() {
		return ban;
	}

	public void setBan(int ban) {
		this.ban = ban;
	}

	public int getAdmin() {
		return admin;
	}

	public void setAdmin(int admin) {
		this.admin = admin;
	}
}
