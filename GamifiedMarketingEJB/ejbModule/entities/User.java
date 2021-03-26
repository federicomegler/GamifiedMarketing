package entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({
		@NamedQuery(name = "User.checkCredentials", query = "SELECT u FROM User u WHERE (u.username = :name or u.email = :name) and u.password = :password"),
		@NamedQuery(name = "User.existCredentials", query="SELECT u FROM User u WHERE (u.username = :username or u.email = :email)"),
		@NamedQuery(name = "User.getSalt", query="SELECT u.salt FROM User u WHERE (u.username = :name or u.email = :name)"),
		@NamedQuery(name = "User.getLeaderboard", query = "SELECT u FROM User u ORDER BY u.points DESC")
})
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	private String username;
	
	private String email;
	private String password;
	private String salt;
	private int ban;
	private int admin;
	private int points;
	
	@OneToMany(mappedBy = "user")
	private List<Answer> answers;
	
	@OneToMany(mappedBy = "user")
	private List<StatisticalAnswer> statistical_answers;
	
	@OneToMany(mappedBy = "user")
	private List<Review> reviews;
	
	@OneToMany(mappedBy = "user")
	private List<Log> logs;
	
	public User() {}
	
	public User(String username, String email, String password, String salt) {
		this.username = username;
		this.email = email;
		this.setPassword(password);
		this.setSalt(salt);
		this.setAdmin(0);
		this.setBan(0);
	}
	
	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
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

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
}