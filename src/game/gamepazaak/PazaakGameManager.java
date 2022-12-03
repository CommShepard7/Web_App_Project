package game.gamepazaak;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Observer;
import javax.websocket.Session;

import database.gamedata.Player;
import game.gameinterfaces.GameAction;
import game.gameinterfaces.GameManager;

public class PazaakGameManager implements GameManager {

	private GameAction gameAction;
	private int gameActionValue;
	private Player player1;
	private Player player2;
	private ArrayList<String> playerDeck1 = new ArrayList<String>();
	private ArrayList<String> playerDeck2 = new ArrayList<String>();
	private ArrayList<String> gameDeck1 =  new ArrayList<String>();
	private ArrayList<String> gameDeck2 =  new ArrayList<String>();
	private ArrayList<Integer> gameCardSet;
	private Player activePlayer;
	private PazaakGameData gameData;

	public PazaakGameManager(Observer gameServer, Player player1, Player player2, ArrayList<Session> playerSessions) {
		this.player1 = player1;
		this.player2 = player2;
		this.gameData = new PazaakGameData(gameServer,playerSessions);
	}
	
	public void initGameCardSet() {
		this.gameCardSet = new ArrayList<Integer>();
		for(int k = 1; k <= 10; k++) {
			this.gameCardSet.add(k);
		}
	}
	
	@Override
	public ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(this.player1);
		players.add(this.player2);
		return players;
	}

	@Override
	public void setGameAction(GameAction gameAction) {
		this.gameAction = gameAction;
	}
	
	public void setGameActionValue(int value) {
		this.gameActionValue = value;
	}
	
	public String getActivePlayerName() {
		return this.activePlayer.getName();
	}
	
	public void setSpecialCards() {
		ArrayList<String> specialCards1 = new ArrayList<String>();
		for(int k = 0; k < 12; k++) {
			specialCards1.add(PazaakSpecialCards.values()[k].name());
		}
		ArrayList<String> specialCards2 = new ArrayList<String>(specialCards1);
		System.out.println(specialCards1);
		System.out.println(specialCards2);
		SecureRandom randomGenerator = new SecureRandom();
		int bound = 12;
		for(int k = 0; k < 4; k++) {
			int randomInt1 = randomGenerator.nextInt(bound);
			int randomInt2 = randomGenerator.nextInt(bound);
			String specialCard1 = specialCards1.get(randomInt1);
			String specialCard2 = specialCards2.get(randomInt2);
			playerDeck1.add(specialCard1);
			playerDeck2.add(specialCard2);
			specialCards1.remove(randomInt1);
			specialCards2.remove(randomInt2);
			bound--;
		} 
	}
	
	public String getRandomCard() {
		SecureRandom randomGenerator = new SecureRandom();
		int randomInt = randomGenerator.nextInt(10);
		return String.valueOf(this.gameCardSet.get(randomInt));
	}

	@Override
	public void launch() {
		this.player1.setScore(0);
		this.player2.setScore(0);
		initGameCardSet();
		setSpecialCards();
		String[] data = new String[12];
		data[0] = "GAME_START";
		data[1] = this.player1.getName();
		data[6] = this.player2.getName();
		for(int k = 2; k <=5 ;k++) {
			data[k] = this.playerDeck1.get(k-2);
		}
		for(int k = 7; k < 11;k++) {
			data[k] = this.playerDeck2.get(k-7);
		}
 		SecureRandom randomGenerator = new SecureRandom();
		int randomInt = randomGenerator.nextInt(2);
		if(randomInt == 0) {
			this.activePlayer = this.player1;
			data[11] = this.player1.getName();
		} else {
			this.activePlayer = this.player2;
			data[11] = this.player2.getName();
		}
		this.gameData.setData(data);
		this.gameData.setUpdate();
	}
	
	public boolean gameEnded() {
		return ((this.player1.getScore() > 20 && this.player1.getScore() > this.player2.getScore()) ||
				(this.player2.getScore() > 20 && this.player2.getScore() > this.player1.getScore()) ||
				(this.player1.getScore() > 20 && this.player2.getScore() == this.player1.getScore()) ||
				(this.player1.getScore() == 20 && this.player2.getScore() != 20) ||
				(this.player2.getScore() == 20 && this.player1.getScore() != 20) ||
				(this.player2.getScore() == 20 && this.player1.getScore() == 20) 
				);
	}
	
	public Player getWaitingPlayer() { 
		if(this.activePlayer == this.player1) {
			return this.player2;
		} else {
			return this.player2;
		}
	}
	
	public ArrayList<String> getActiveDeck() {
		if(this.activePlayer == this.player1) {
			return this.gameDeck1;
		} else {
			return this.gameDeck2;
		}
	}
	
	public void switchActivePlayer() {
		if(this.activePlayer == this.player1) {
			this.activePlayer = this.player2;
		} else {
			this.activePlayer = this.player1;
		}
	}
	
	@Override
	public void run() {
		this.launch();
		while(!this.gameEnded()) {
			System.out.println(this.activePlayer.getName() + " PLAYING");
			String randomCard = getRandomCard();
			getActiveDeck().add(randomCard);
			this.activePlayer.setScore(this.activePlayer.getScore()+Integer.parseInt(randomCard));
			String[] data = new String[5];
			data[0] = "GAME_ONGOING";
			data[1] = this.activePlayer.getName();
			data[2] = String.valueOf(this.activePlayer.getScore());
			data[3] = "RANDOM_CARD";
			data[4] = randomCard;
			this.gameData.setData(data);
			this.gameData.setUpdate();
			synchronized(this) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			switch(this.gameAction) {
				case MINUS:
					this.activePlayer.setScore(this.activePlayer.getScore()+this.gameActionValue);
					data[1] = this.activePlayer.getName();
					data[2] = String.valueOf(this.activePlayer.getScore());
					data[3] = "MINUS";
					data[4] = String.valueOf(this.gameActionValue);
					switchActivePlayer();
					this.gameData.setData(data);
					this.gameData.setUpdate();
					break;
				case PLUS:
					this.activePlayer.setScore(this.activePlayer.getScore()+this.gameActionValue);
					data[1] = this.activePlayer.getName();
					data[2] = String.valueOf(this.activePlayer.getScore());
					data[3] = "PLUS";
					data[4] = "+" + String.valueOf(this.gameActionValue);
					switchActivePlayer();
					this.gameData.setData(data);
					this.gameData.setUpdate();
					break;
				case ENDTURN:
					switchActivePlayer();
					data[0] = "SWITCH_PLAYER";
					data[1] = this.activePlayer.getName();
					this.gameData.setData(data);
					this.gameData.setUpdate();
				default:
					break;		
			}
		}
		String[] gameEndData = new String[2];
		System.out.println("Game ending");
		gameEndData[0] = "GAME_END";
		if((this.player1.getScore() <= 20) && (this.player2.getScore() > 20)) {
			gameEndData[1] = this.player1.getName();
			this.gameData.setGameWinner(player1);
		} else {
			gameEndData[1] = this.player2.getName();
			this.gameData.setGameWinner(player2);
		}
		this.gameData.setData(gameEndData);
		this.gameData.setUpdate();
	}
}
