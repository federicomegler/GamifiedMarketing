package entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({
	@NamedQuery(name = "Log.alreadyLogged", query = "SELECT l FROM Log l WHERE l.user_log = :user AND l.date = CURRENT_DATE")
})
public class Log implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Temporal(TemporalType.DATE)
	private Date date;
	
	@ManyToOne
	@JoinColumn(name = "user_log")
	private User user_log;
	
	@ManyToOne
	@JoinColumn(name = "product_log")
	private Product product_log;
	
	public Product getProduct_log() {
		return product_log;
	}

	public void setProduct_log(Product product_log) {
		this.product_log = product_log;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public User getUser_log() {
		return user_log;
	}

	public void setUser_log(User user_log) {
		this.user_log = user_log;
	}

	
}
