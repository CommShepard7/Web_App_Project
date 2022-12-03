package game.gameinterfaces;

import java.util.ArrayList;
/* Hello 2*/
import database.gamedata.Player;

public interface Game {
	
	public ArrayList<Player> getPlayers();
	public void setPlayers(ArrayList<Player> players);
	public GameType getGameType();
	public boolean isEnded();
	
}
