package entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
		@NamedQuery(name = "User.checkCredentials", query = "SELECT u FROM User u WHERE (u.username = :name or u.email = :name) and u.password = :password"),
		@NamedQuery(name = "User.existCredentials", query="SELECT u FROM User u WHERE (u.username = :username or u.email = :email)")
})
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
	
	public User() {}
	
	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.admin = 0;
		this.ban = 0;
	}
	
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
