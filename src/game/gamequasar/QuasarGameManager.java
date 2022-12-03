package game.gamequasar;

import java.security.SecureRandom;
import java.util.ArrayList;

import javax.websocket.Session;

import database.gamedata.Player;
import game.gameinterfaces.Game;
import game.gameinterfaces.GameAction;
import game.gameinterfaces.GameManager;
import gameserver.GameSocketServer;



public class QuasarGameManager implements GameManager {
	
	private Game gameInstance;
	private Player player;
	public GameAction gameAction;
	public boolean gameWon;
	private QuasarGameData gameData;
	public ArrayList<String> resultBuffer = new ArrayList<String>();
	private SecureRandom randomGenerator = new SecureRandom();
	
	public QuasarGameManager(Player player,GameSocketServer gameServer, Session playerSession) {
		this.player = player;
		this.gameData = new QuasarGameData(gameServer,playerSession);
	}
	
	@Override
	public void setGameAction(GameAction gameAction) {
		this.gameAction = gameAction;
	}
	
	@Override
	public ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(this.player);
		return players;
	}

	@Override
	public void launch() {
		this.player.setScore(0);
		this.gameInstance = new QuasarGame(this.player);	
	}
	
	@Override
	public void run() {
		launch();
		while(!this.gameInstance.isEnded()) {
				synchronized(this) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				int randomInt = 0;
				switch(this.gameAction) {
					case ONEToEIGHT:
						randomInt = randomGenerator.nextInt(8) + 1;
						break;
					case FOURToSEVEN:
						randomInt = randomGenerator.nextInt(4) + 4;
						break;
					default:
						randomInt = 0;
				}
				String actionResult = String.valueOf(this.player.getScore()+randomInt);
				String[] data = new String[2];
				data[0] = String.valueOf(randomInt);
				data[1] = actionResult;
				this.gameData.setData(data);
				this.gameData.setUpdate();
				this.player.setScore(this.player.getScore()+randomInt);
				System.out.println(this.player.getScore());
	    }
		System.out.println("Game ended");
		int playerScore = this.player.getScore();
		this.gameData.setGameEnded();
		if(playerScore >= 17 && playerScore <= 20) {
			this.gameData.setGameWon();
		}
		this.gameData.setUpdate();
	}
}
