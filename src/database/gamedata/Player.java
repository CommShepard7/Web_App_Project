package database.gamedata;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import database.userdata.User;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Player {
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
    private String name;
    private int creditsValue;
    private int score;
    private String userPPic;
    
    @OneToOne
    private User user;
    
    @ManyToMany(mappedBy="players",fetch=FetchType.EAGER)
    private List<GameSession> gameSessions = new ArrayList<GameSession>();
    
   
    public Player() {
    	
    }
    
	public ArrayList<GameSession> getGameSessions() {
		return new ArrayList<GameSession>(gameSessions);
	}

	public void setGameSessions(List<GameSession> gameSessions) {
		this.gameSessions = gameSessions;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getCreditsValue() {
		return creditsValue;
	}
	
	public void setCreditsValue(int creditsValue) {
		this.creditsValue = creditsValue;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
    
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public User getUser() {
		return this.user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	public String getUserPPic() {
		return userPPic;
	}

	public void setUserPPic(String userPPic) {
		this.userPPic = userPPic;
	}
    
}