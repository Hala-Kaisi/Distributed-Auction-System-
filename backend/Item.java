import java.io.Serializable;

public class Item implements Serializable{
    int itemId;
    String itemTitle;
    String itemDescription;
    String itemCondition;

    public Item(int id, String title, String description, String condition){
        this.itemId = id;
        this.itemCondition = condition;
        this.itemDescription = description;
        this.itemTitle = title;
    }
}
