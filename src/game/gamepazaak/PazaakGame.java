package game.gamepazaak;

import java.util.ArrayList;

import database.gamedata.Player;
import game.gameinterfaces.Game;
import game.gameinterfaces.GameType;

public class PazaakGame implements Game {
	
	private Player player1;
	private Player player2;
	private Player activePlayer;
	private Player gameWinner;
	private int playerScore1;
	private int playerScore2;

	public PazaakGame(Player player1,Player player2) {
		this.player1 = player1;
		this.player2 = player2;
	}
	
	@Override
	public ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(player1);
		players.add(player2);
		return players;
	}

	@Override
	public void setPlayers(ArrayList<Player> players) {
		this.player1 = players.get(0);
		this.player2 = players.get(1);
	}

	@Override
	public GameType getGameType() {
		return GameType.PAZAAK;
	}
	
	@Override
	public boolean isEnded() {
		return (this.getPlayerScore1() >= 20 || this.getPlayerScore2() >= 20);
	}

	public Player getActivePlayer() {
		return this.activePlayer;
	}
	
	public void setActivePlayer(Player activePlayer) {
		this.activePlayer = activePlayer;
	}
	
	public int getPlayerScore1() {
		return this.playerScore1;
	}

	public int getPlayerScore2() {
		return this.playerScore2;
	}
	
	public Player getGameWinner() {
		return this.gameWinner;
	}
	
	public void setGameWinner(Player player) {
		this.gameWinner = player;
	}

}
