import java.io.Serializable;

public class AuctionItem implements Serializable{
    int itemId;
    int auctionId;
    int startingPrice;
    int reservePrice;
    int highestBid = 0;
    String winnerEmail = "";
    String winnerName = "";
    boolean open = true;
    int ownerId = -1;

    public AuctionItem(int auctionID, int id, int startingPrice, int reservePrice, int ownerID){
        this.auctionId = auctionID;
        this.itemId = id;
        this.startingPrice = startingPrice;
        this.reservePrice = reservePrice;
        this.ownerId = ownerID;
    }
}
