
package entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Answer implements Serializable{
	private static final long serialVersionUID = 1L;

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String content;
	
	@ManyToOne
	@JoinColumn(name = "user_answer")
	private User user_answer;

	@ManyToOne
	@JoinColumn(name = "question")
	private Question question;
	
	public User getUser() {
		return user_answer;
	}

	public void setUser(User user) {
		this.user_answer = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public void setQuestion(Question q)
	{
		this.question=q;
	}
	
	
}

