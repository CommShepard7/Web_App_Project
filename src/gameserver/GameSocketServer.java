package gameserver;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.io.IOException;
import java.util.ArrayList;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.*;

import database.gamedata.Player;
import game.gameinterfaces.GameAction;
import game.gameinterfaces.GameData;
import game.gameinterfaces.GameManager;
import game.gameinterfaces.GameType;
import game.gamepazaak.PazaakGameData;
import game.gamepazaak.PazaakGameManager;
import game.gamequasar.QuasarGameData;
import game.gamequasar.QuasarGameManager;
import mainserver.Facade;

@ServerEndpoint(value = "/gameserver/{gamePort}",configurator = GameSocketServer.WebSocketConfigurator.class)
public class GameSocketServer implements Observer {
	
	private static GameSocketServer mainServer;
	private HashMap<Session,GameManager> sessionGameManagers = new HashMap<Session,GameManager>();
	private HashMap<Session,Thread> sessionGameThreads = new HashMap<Session,Thread>();
	private HashMap<Integer,Session> waitingRoom = new HashMap<Integer,Session>();
	private HashMap<Session,Player> playerSession = new HashMap<Session,Player>();
	private static Facade facade;
	
	public GameSocketServer() {
		
	}
	
	@OnOpen
	public void openEvent(Session session,@PathParam("gamePort") int gamePort) throws IOException, ClassNotFoundException {
		session.getUserProperties().put("portNumber", gamePort);
		System.out.println("New player joined");
	}
	
	@OnMessage
	public void messageEvent(Session session, String message,@PathParam("gamePort") int gamePort) throws IOException {
		String messageType = getMessageType(message);
		switch(messageType) {
			case("GAME_START"):
				gameStartHandler(session,message,gamePort);
				break;
			case("GAME_CANCEL"):
				gameCancelHandler(session,gamePort);
				break;
			case("CONNECTION_DATA"):
				connectionHandler(session, message, gamePort);
				break;
			case("GAME_DATA"):
				gameDataHandler(session,message);
				break;
			default:
				break;
		}
	}

	public void gameDataHandler(Session session, String message) {
		String[] gameData =  message.split("GAME_ACTION:");
		GameType gameType = GameType.valueOf(gameData[1].split(":")[0]);
		String gameAction;
		GameManager gameManager;
		Thread gameThread;
		switch(gameType) {
			case PAZAAK:
				gameManager = sessionGameManagers.get(session);
				if(playerSession.get(session).getName() == ((PazaakGameManager) gameManager).getActivePlayerName()) {
					gameAction = gameData[1].split("PAZAAK:")[1].split(":")[0];
					gameThread = sessionGameThreads.get(session);
					gameManager.setGameAction(GameAction.valueOf(gameAction));
					System.out.println("ACTION: " + gameAction);
					if(gameAction.equals("PLUS") || gameAction.equals("MINUS")) {
						System.out.println("hey");
						String gameValue = gameData[1].split("PAZAAK:")[1].split(":")[1];
						((PazaakGameManager) gameManager).setGameActionValue(Integer.parseInt(gameValue));
					}
					synchronized(gameManager) {
						gameManager.notify();
						System.out.println(gameThread.getState());
					}
				} else {
					System.out.println("Not " + playerSession.get(session).getName() + "'s turn");
				}
				break;
			case QUASAR:
				gameManager = sessionGameManagers.get(session);
				gameAction = gameData[1].split(":")[1];
				System.out.println(gameAction);
				gameManager.setGameAction(GameAction.valueOf(gameAction));
				gameThread = sessionGameThreads.get(session);			
				synchronized(gameManager) {
					gameManager.notify();
					System.out.println(gameThread.getState());
				}	
		}
	}
	
	public void connectionHandler(Session session, String message,@PathParam("gamePort") int gamePort) {
		String[] userData = message.split(":");
		System.out.println(userData[1] + " " + gamePort);
		session.getUserProperties().put(userData[1], gamePort);
		this.playerSession.put(session, facade.getPlayer(userData[1]));
	}
	
	public String getMessageType(String message) {
		if(message.contains("START_GAME")) {
			return "GAME_START";
		} else if(message.contains("GAME_JOIN")) {
			return "GAME_JOIN";
		} else if(message.contains("CONNECTION_DATA")) {
			return "CONNECTION_DATA";
		} else if(message.contains("GAME_ACTION")) {
			return "GAME_DATA";
		} else 
			return "";
	}
	
	public void gameStartHandler(Session session, String message,int gamePort) throws IOException {
		switch(message) {
		case("START_GAME_PAZAAK"):
			if(this.waitingRoom.containsKey(gamePort)) {
				ArrayList<Session> sessions = new ArrayList<Session>();
				Session waitingPlayer = waitingRoom.get(gamePort);
				Player player1 = this.playerSession.get(session);
				Player player2 = this.playerSession.get(waitingPlayer);
				sessions.add(session);
				sessions.add(waitingPlayer);
				GameManager gameManager = new PazaakGameManager(this,player1,player2,sessions);
				this.sessionGameManagers.put(session,gameManager);
				this.sessionGameManagers.put(waitingPlayer,gameManager);
				session.getBasicRemote().sendText("STARTING_GAME");
				waitingPlayer.getBasicRemote().sendText("STARTING_GAME");
				this.waitingRoom.remove(gamePort);
				launchPazaakGame(gameManager,sessions);
			} else {
				System.out.println("WAITING FOR PAZAAK game");
				this.waitingRoom.put(gamePort, session);
			}
			break;
		case("START_GAME_QUASAR"):
			Player player = this.playerSession.get(session);
			QuasarGameManager gameManager = new QuasarGameManager(player,this,session);
			sessionGameManagers.put(session, gameManager);
			launchQuasarGame(gameManager,session);
			break;
		default:
			break;
		}
	}
	
	public void gameCancelHandler(Session session, int gamePort) {
		this.waitingRoom.remove(gamePort);
	}
	
	@OnClose
    public void close(Session session) {
		int portNumber = (int) session.getUserProperties().get("portNumber");
        if(this.waitingRoom.containsKey(portNumber)) {
        	waitingRoom.remove(portNumber);
        }
    }
	
	@Override
	public void update(Observable gameDataUpdate, Object arg) {
		GameData gameData = (GameData) gameDataUpdate;
		GameType gameType = gameData.getGameType();
		System.out.println("UPDATING GAMEDATA");
		switch(gameType) {
			case PAZAAK: 
				updatePazaakData((PazaakGameData) gameDataUpdate);
				break;
			case QUASAR:
				updateQuasarData((QuasarGameData) gameDataUpdate);
				break;
		}
	}
	
	public void updateQuasarData(QuasarGameData gameDataUpdate) {
		Session playerSession =  gameDataUpdate.getSessions();
		String[] gameData = ((QuasarGameData) gameDataUpdate).getData();
		try {
			if(!gameDataUpdate.gameEnded()) {
				playerSession.getBasicRemote().sendText(gameData[0] + ";" + gameData[1]);
			} else {
				HashMap<Player,Boolean> playerStatus = new HashMap<Player,Boolean>();
				ArrayList<Player> players = sessionGameManagers.get(playerSession).getPlayers();
				playerStatus.put(players.get(0),gameDataUpdate.gameWon());
				facade.createGameSession(GameType.QUASAR, playerStatus);
				playerSession.getBasicRemote().sendText("GAME_WON : "+gameDataUpdate.gameWon());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updatePazaakData(PazaakGameData gameDataUpdate) {
		System.out.println("UPDATING PAZAAK game : " + gameDataUpdate.getData()[0]);
		
		for(int k = 0; k < gameDataUpdate.getData().length;k++) {
			System.out.println(gameDataUpdate.getData()[k]);
		}
		
		ArrayList<Session> playerSessions = gameDataUpdate.getSessions();
		String[] data = gameDataUpdate.getData();
		String gameData;
		try {
		switch(data[0]) {
			case "GAME_START":
				System.out.println("GAME_START >> ");
				System.out.println(playerSession);
				String playerStart1;
				String playerStart2;
				if(data[11] == playerSession.get(playerSessions.get(0)).getName()) {
					playerStart1 = "true";
					playerStart2 = "false";
				} else {
					playerStart1 = "false";
					playerStart2 = "true";
				}
				String gameStartData1 = "GAME_START:" + data[2]+ ";" + data[3] + ";" + data[4] + ";" +data[5] + ";" + playerStart1 + ";" + data[6]; 
				String gameStartData2 = "GAME_START:" + data[7]+ ";" + data[8] + ";" + data[9] + ";" +data[10] + ";" + playerStart2 + ";" + data[1];
				playerSessions.get(0).getBasicRemote().sendText(gameStartData1);
				System.out.println(gameStartData1);
				playerSessions.get(1).getBasicRemote().sendText(gameStartData2);
				System.out.println(gameStartData2);
				break;
			case "GAME_ONGOING":	
				gameData = "GAME_ONGOING:" + data[1] + ";" + data[2] + ";" + data[3] + ";" + data[4];
				playerSessions.get(0).getBasicRemote().sendText(gameData);
				playerSessions.get(1).getBasicRemote().sendText(gameData);
				break;
			case "SWITCH_PLAYER":
				gameData = "SWITCH_PLAYER:" + data[1];
				playerSessions.get(0).getBasicRemote().sendText(gameData);
				playerSessions.get(1).getBasicRemote().sendText(gameData);
				break;
			case "GAME_END":
				gameData = "GAME_END:" + data[1];
				System.out.println(gameData);
				playerSessions.get(0).getBasicRemote().sendText(gameData);
				playerSessions.get(1).getBasicRemote().sendText(gameData);
				HashMap<Player,Boolean> playerStatus = new HashMap<Player,Boolean>();
				playerStatus.put(gameDataUpdate.getGameWinner(),true);
				facade.createGameSession(GameType.PAZAAK, playerStatus);
				break;
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public void launchQuasarGame(GameManager gameManager,Session session) {
		Thread gameThread = new Thread(gameManager);
		sessionGameThreads.put(session,gameThread);
		System.out.println("Starting QUASAR game");
		gameThread.start();
	}
	
	public void launchPazaakGame(GameManager gameManager,ArrayList<Session> sessions) {
		Thread gameThread = new Thread(gameManager);
		for(Session s : sessions) {
			sessionGameThreads.put(s,gameThread);
		}
		System.out.println("Starting PAZAAK game");
		gameThread.start();
	}

	public void setMainServer(GameSocketServer newMainServer) {
		mainServer = newMainServer;
	}
	
	public void setFacade(Facade newFacade) {
		facade = newFacade;
	}
	
	public GameSocketServer getMainWebSocket() {
		return mainServer;
	}
	
	public static class WebSocketConfigurator extends ServerEndpointConfig.Configurator {

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
			return (T) mainServer;
		}	
	}
	
}