package database.userdata;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class UserToken {
	
	@Id
	private String tokenValue;
	private int userId;
	
	@OneToOne
	User user;
	
	public UserToken() {
		
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int id) {
		this.userId = id;
	}

	public String getTokenValue() {
		return tokenValue;
	}


	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	public User getUser(User user) {
		return this.user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

}
