package game.gameinterfaces;

import java.util.Observable;
import java.util.Observer;



public abstract class GameData extends Observable {
	
	Observer gameServer;
	GameType gameType;
	String[] gameData;
	boolean gameEnded = false;
	boolean gameWon = false;
	
	public GameData(Observer gameServer) {
		this.gameServer = gameServer;
		this.addObserver(gameServer);
	}
	
	public void setUpdate() {
		this.setChanged();
		this.notifyObservers();
	}
	
	public boolean gameWon() {
		return this.gameWon;
	}
	
	public void setGameEnded() {
		this.gameEnded = true;
	}

	public void setGameWon() {
		this.gameWon = true;
	}

	public boolean gameEnded() {
		return this.gameEnded;
	}
	
	public String[] getData() {
		return this.gameData;
	}
	
	public void setData(String[] data) {
		this.gameData = data;
	}
	
	public abstract GameType getGameType();

	public abstract <T> T getSessions();

	
}
