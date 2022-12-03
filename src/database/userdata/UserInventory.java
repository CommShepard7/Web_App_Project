package database.userdata;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import database.gamedata.Item;

@Entity
public class UserInventory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;
	
	@OneToOne
	private User inventoryUser;
	
	@OneToMany
	List<Item> itemList = new ArrayList<Item>();
	
	public User getInventoryUser() {
		return inventoryUser;
	}

	public void setInventoryUser(User inventoryUser) {
		this.inventoryUser = inventoryUser;
	}

	public List<Item> getItemList() {
		return itemList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}
	
}
