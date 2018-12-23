package laundry.driveraslijempol.model;

/**
 * Created by Bagus on 26/07/2018.
 */
public class OrderItems {
    public String imageUrl;
    public String itemTitle;
    public String itemDesc;
    public String itemType;
    public int itemQty;
    public boolean section;

    public OrderItems(){

    }

    public OrderItems(String itemTitle, boolean section){
        this.itemTitle = itemTitle;
        this.section = section;
    }
}
