package entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="statistical_answer")
public class StatisticalAnswer implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private int age;
	private char gender;
	private int expertise_level;
	
	@ManyToOne
	@JoinColumn(name = "product")
	private Product product;
	
	@ManyToOne
	@JoinColumn(name = "user")
	private User user_stat;
	
	
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public char getGender() {
		return gender;
	}
	public void setGender(char gender) {
		this.gender = gender;
	}
	public int getExpertise_level() {
		return expertise_level;
	}
	public void setExpertise_level(int expertise_level) {
		this.expertise_level = expertise_level;
	}
	
	public void setUser(User u)
	{
		this.user_stat=u;
	}
	
	public void setProduct(Product p)
	{
		this.product=p;
	}
}