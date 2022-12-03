package game.gameinterfaces;

import java.util.ArrayList;

import database.gamedata.Player;

public interface GameManager extends Runnable {
	
	public ArrayList<Player> getPlayers();
	public void setGameAction(GameAction gameAction);
	public void launch();

}
