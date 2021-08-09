package entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
public class OffensiveWord implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	private String word;
	
	public OffensiveWord(String word) {
		this.word = word;
	}
	
	public OffensiveWord() {}
}
