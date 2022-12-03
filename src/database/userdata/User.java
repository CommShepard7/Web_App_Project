package database.userdata;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import database.gamedata.Player;

@Entity
public class User {
	
	private String firstName;
	private String lastName;
	private String userName;
	private boolean auth;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int userId;
	
	@OneToOne(mappedBy = "user")
	private UserToken userToken;
	
	@OneToOne(mappedBy = "user")
	private Player player;
	
	@OneToOne(mappedBy = "inventoryUser")
	private UserInventory inventory;
	
	public User() {
		
	}
	
	public UserToken getUserToken() {
		return userToken;
	}

	public void setUserToken(UserToken userToken) {
		this.userToken = userToken;
	}

	public UserInventory getInventory() {
		return inventory;
	}

	public void setInventory(UserInventory inventory) {
		this.inventory = inventory;
	}
	
	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public UserToken getToken() {
		return this.userToken;
	}

	public void setToken(UserToken userToken) {
		this.userToken = userToken;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
}
