package entities;

import java.io.Serializable;

public class UserId implements Serializable{
	private static final long serialVersionUID = 1L;

	private String username;
	
	private String email;
	
	public UserId() {}
	public UserId(String username, String email) {
		this.username = username;
		this.email = email;
	}
	
	public String getUsername() {
		return username;
	}
	public String getEmail() {
		return email;
	}
	
	public boolean equals(Object o) {
		return ((o instanceof UserId) && username.equals(((UserId)o).getUsername()) && email.equals(((UserId)o).getEmail()));
	}
	
	public int hashCode() {
		return username.hashCode() + email.hashCode();
	}
}
