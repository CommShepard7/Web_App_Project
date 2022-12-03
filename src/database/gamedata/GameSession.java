package database.gamedata;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import game.gameinterfaces.GameType;

import java.util.List;
import java.util.ArrayList;

@Entity
public class GameSession {
	
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    private String gameType;
    
    @ManyToMany(cascade=CascadeType.MERGE)
    private List<Player> players = new ArrayList<Player>();

    @OneToOne(cascade=CascadeType.MERGE)
    private Player gameWinner;
    
    public GameSession() {
    	
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getGameType() {
        return this.gameType;
    }

    public ArrayList<Player> getPlayers() {
        return (ArrayList<Player>) this.players;
    }

    public void setPlayers(ArrayList<Player> players) {
    	this.players = players;
    }
        
    public void setId(int id) {
        this.id = id;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType.name();
    }
    
    public Player getGameWinner() {
    	return this.gameWinner;
    }
    
    public void setGameWinner(Player player) {
    	this.gameWinner = player;
    }
 
}
