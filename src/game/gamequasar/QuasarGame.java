package game.gamequasar;

import java.util.ArrayList;

import database.gamedata.Player;
import game.gameinterfaces.Game;
import game.gameinterfaces.GameType;

public class QuasarGame implements Game {

	Player player;
	
	public QuasarGame(Player player) {
		this.player = player;
	}
	
	@Override
	public ArrayList<Player> getPlayers() {		
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(this.player);
		return players;
	}

	@Override
	public void setPlayers(ArrayList<Player> players) {
		this.player = players.get(0);
	}
	
	@Override
	public GameType getGameType() {	
		return  GameType.QUASAR;
	}

	@Override
	public boolean isEnded() {
		return ((this.player.getScore() >= 17 && this.player.getScore() <= 20) || this.player.getScore() > 20);
	}

}
