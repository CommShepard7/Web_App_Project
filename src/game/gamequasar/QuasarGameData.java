package game.gamequasar;

import java.util.Observer;
import javax.websocket.Session;

import game.gameinterfaces.GameData;
import game.gameinterfaces.GameType;

public class QuasarGameData extends GameData {
	
	private Session playerSession;
	
	public QuasarGameData(Observer gameServer,Session session) {
		super(gameServer);
		this.playerSession = session;
	}
	
	@Override
	public GameType getGameType() {
		return GameType.QUASAR;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Session getSessions() {
		return this.playerSession;
	}
	
}
