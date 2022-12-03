package game.gamepazaak;

import game.gameinterfaces.GameData;
import game.gameinterfaces.GameType;
import java.util.ArrayList;
import java.util.Observer;

import javax.websocket.Session;

import database.gamedata.Player;

public class PazaakGameData extends GameData {
	
	private ArrayList<Session> playerSessions;
	private Player gameWinner;
	
	public PazaakGameData(Observer gameServer, ArrayList<Session> sessions) {
		super(gameServer);
		this.playerSessions = sessions;
	}
	
	
	public Player getGameWinner() {
		return this.gameWinner;
	}
	
	public void setGameWinner(Player player) {
		this.gameWinner = player;
	}
	
	@Override
	public GameType getGameType() {		
		return GameType.PAZAAK;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Session> getSessions() {
		return this.playerSessions;
	}

}
