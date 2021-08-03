package entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Base64;

@Entity
@NamedQueries({
	@NamedQuery(name = "Product.getProductOfTheDay", query = "SELECT p FROM Product p WHERE p.date = CURRENT_DATE"),
	@NamedQuery(name = "Product.getAllProducts", query = "SELECT p FROM Product p")
	})
public class Product implements Serializable{
	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String name;
	
	@Basic(fetch = FetchType.LAZY)
	@Lob
	private byte[] image;

	@Temporal(TemporalType.DATE)
	Date date;
	
	@OneToMany(mappedBy = "product")
	private List<Question> questions;
	
	@OneToMany(mappedBy = "product")
	private List<StatisticalAnswer> statistical_answers;
	
	private String ean;
	
	
	public int getId() {
		return id;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getImageData() {
		return Base64.getMimeEncoder().encodeToString(this.image);
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}
	
}
