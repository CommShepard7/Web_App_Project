package mainserver;


import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import database.gamedata.GameSession;
import database.gamedata.Item;
import database.gamedata.Player;
import database.userdata.User;
import database.userdata.UserException;
import database.userdata.UserToken;
import game.gameinterfaces.GameType;
import database.userdata.SecurityManager;

@Singleton
public class Facade {
	
	public Facade() throws UserException {
		
	}
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public void createUser(String firstName, String lastName, String userName, String password) throws UserException {
		TypedQuery<User> query = entityManager.createQuery("SELECT user FROM User user WHERE user.userName = '".concat(userName).concat("'"), User.class);
		if(!query.getResultList().isEmpty()) {
			throw new UserException("Username already exists");
		} else {
			User newUser = new User();
			SecurityManager securityManager = new SecurityManager();
			Player newPlayer = new Player();
			newUser.setFirstName(firstName);
			newUser.setLastName(lastName);
			newUser.setUserName(userName);
			newUser.setAuth(false);
			securityManager.setId(userName);
			securityManager.setPassword(password);
			securityManager.getUsers().add(newUser);
			newPlayer.setUserPPic("css/art1.jpg");
			newPlayer.setUser(newUser);
			newPlayer.setCreditsValue(100);
			newPlayer.setName(userName);
			newPlayer.setScore(0);
			entityManager.persist(newUser);
			entityManager.persist(securityManager);
			entityManager.persist(newPlayer);
		}
	}
	
	public void createGameSession(GameType gameType, HashMap<Player,Boolean> playerStatus) {
		GameSession newGameSession = new GameSession();
		ArrayList<Player> players = new ArrayList<Player>(playerStatus.keySet());
		for(Player p : players) {
			p.setCreditsValue(p.getCreditsValue()+500);
			entityManager.merge(p);
			if(playerStatus.get(p)) {
				newGameSession.setGameWinner(p);
			}
		}
		newGameSession.setPlayers(players);
		newGameSession.setGameType(gameType);
		entityManager.persist(newGameSession);
	}
	
	public Player getPlayer(String userName) {
		TypedQuery<User> query = entityManager.createQuery("SELECT user FROM User user WHERE user.userName = '".concat(userName).concat("'"), User.class);
		return query.getSingleResult().getPlayer();
	}
	
	public boolean checkUserName(String userName) {
		TypedQuery<User> query = entityManager.createQuery("SELECT user FROM User user WHERE user.userName = '".concat(userName).concat("'"), User.class);
		return !query.getResultList().isEmpty();
	}
	
	public boolean checkUserAuth(String userToken) {
		UserToken token = entityManager.find(UserToken.class, userToken);
		if(token == null) {
			return false;
		}
 		User user = entityManager.find(User.class,token.getUserId());
		return user.isAuth();
	}
	
	public String getAuthPPic(String userName) {
		Player player = getPlayer(userName);
		return player.getUserPPic();
	}
	
	public String getAuthLastName(String userName) {
		TypedQuery<User> query = entityManager.createQuery("SELECT user FROM User user WHERE user.userName = '".concat(userName).concat("'"), User.class);
		User authUser = query.getSingleResult();
		return authUser.getLastName();
	}
	
	public String getAuthFirstName(String userName) {
		TypedQuery<User> query = entityManager.createQuery("SELECT user FROM User user WHERE user.userName = '".concat(userName).concat("'"), User.class);
		User authUser = query.getSingleResult();
		return authUser.getFirstName();
	}
	
	public String getAuthScore(String userName) {
		Player player = getPlayer(userName);
		return String.valueOf(player.getScore());
	}
	
	public String getAuthCredits(String userName) {
		Player player = getPlayer(userName);
		return String.valueOf(player.getCreditsValue());
	}
	
	public String authenticateUser(String userName, String password) throws UserException {
		TypedQuery<User> query = entityManager.createQuery("SELECT user FROM User user WHERE user.userName = '".concat(userName).concat("'"), User.class);
		User authUser = query.getSingleResult();
		UserToken userToken;
		String tokenValue;
		SecurityManager securityManager = entityManager.find(SecurityManager.class, authUser.getUserName());
		boolean authenticate = securityManager.getPassword().equals(password);
		if(authenticate && !authUser.isAuth()) {
			userToken = new UserToken();
			authUser.setAuth(true);
			userToken.setUserId(authUser.getUserId());
			tokenValue = generateToken(64);
			userToken.setTokenValue(tokenValue);
			userToken.setUser(authUser);
			entityManager.persist(userToken);
			entityManager.merge(authUser);
		} else if(authenticate && authUser.isAuth()) {
			tokenValue = authUser.getToken().getTokenValue();
		} else {
			tokenValue = "accessDenied";
		}	
		return tokenValue;
	}
	
	public void disconnectUser(String userName) {
		TypedQuery<User> query = entityManager.createQuery("SELECT user FROM User user WHERE user.userName = '".concat(userName).concat("'"), User.class);
		User authUser = query.getSingleResult();
		authUser.setAuth(false);
		UserToken userToken = authUser.getToken();
		entityManager.remove(userToken);
		entityManager.merge(authUser);
	}
	
	public void createItem() {
		Item item1 = new Item();
		item1.setName("Galaxie");
		item1.setPrice(5);
		item1.setUrl_picture("css/ppgalaxie.jpg");
		
		Item item2 = new Item();
		item2.setName("Galaxie 2");
		item2.setPrice(3);
		item2.setUrl_picture("css/illium.jpg");
		entityManager.persist(item1);
		entityManager.persist(item2);
	}
	
	public boolean userGetItem(String itemID, String userName) {
		TypedQuery<User> query = entityManager.createQuery("SELECT user FROM User user WHERE user.userName = '".concat(userName).concat("'"), User.class);
		User authUser = query.getSingleResult();
		Item item = entityManager.find(Item.class, Integer.parseInt(itemID));
		if(authUser.getPlayer().getScore() >= item.getPrice()) {
			authUser.getInventory().getItemList().add(item);
			return true;
		} else {
			return false;
		}
	}
	
	public ArrayList<Item> getItems() {
		TypedQuery<Item> query = entityManager.createQuery("SELECT item FROM Item item", Item.class);
		return (ArrayList<Item>) query.getResultList();
	}
	
	public String getNamesItem() {
		ArrayList<Item> items = getItems();
		String name_item = new String();
		for(Item item : items) {
			name_item = name_item + item.getName() + ".";
		}
		return name_item;
	}
	
	public String getPricesItem() {
		ArrayList<Item> items = getItems();
		String price_item = new String();
		for(Item item : items) {
			price_item = price_item + String.valueOf(item.getPrice()) + "+";
		}
		return price_item;
	}
	
	public String getUrlItem() {
		ArrayList<Item> items = getItems();
		String url_item = new String();
		for(Item item : items) {
			url_item = url_item + item.getUrl_picture() + "+";
		}
		return url_item;
	}

	public char[] charArrayGenerator(int length,int bound,int startingPos) {
        SecureRandom secureRandom = new SecureRandom();
        char[] randomIntVector = new char[length];
        for(int k = 0; k < length; k++) {
            randomIntVector[k] = (char) (secureRandom.nextInt(bound)+startingPos);
        }
        return randomIntVector;
    }
	
	public String generateToken(int length) {
		SecureRandom randomSwitch = new SecureRandom();
        char[][] randomCharArrays = new char[3][length];
        randomCharArrays[0] = charArrayGenerator(length, 10, 48);
        randomCharArrays[1] = charArrayGenerator(length, 26, 65);
        randomCharArrays[2] = charArrayGenerator(length, 26, 97);
        char[] randomString = new char[length];
        
        for(int k = 0; k < length; k++) {
            randomString[k] = randomCharArrays[randomSwitch.nextInt(3)][k];
        }
		
		return new String(randomString);
	}
		
}