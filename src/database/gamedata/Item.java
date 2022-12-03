package database.gamedata;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Item {
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
    private String name;
    private int Price;
    private String Url_picture;
    
    
    public Item() {
    	
    }
   
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getPrice() {
		return Price;
	}
	
	public void setPrice(int Price) {
		this.Price = Price;
	}
	
	public String getUrl_picture() {
		return Url_picture;
	}
	
	public void setUrl_picture(String Url_picture) {
		this.Url_picture = Url_picture;
	}
    
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
    
}