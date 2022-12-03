package database.userdata;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;


@Entity
public class SecurityManager {
	
	@Id
	private String id;
	
	private String password;
	
	@OneToMany
	private List<User> users = new ArrayList<User>();
	
	public SecurityManager() {
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public List<User> getUsers() {
		return this.users;
	}
	
	public void setUsers(List<User> users) {
		this.users = users;
	}
}
