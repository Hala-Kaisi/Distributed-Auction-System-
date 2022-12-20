import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedList;

public interface Spec extends Remote{
    public SealedObject getSpec(int itemId, SealedObject clientReq) throws RemoteException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException;
    public String addItem(int itemId, String itemTitle, String itemDescription, String itemCondition) throws RemoteException;
    public int checkItem(int itemId) throws RemoteException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException;
    public int createAuction(int itemID, int startingPrice, int reservePrice, int ownerID) throws RemoteException;
    public LinkedList<AuctionItem> getAuctions() throws RemoteException;
    public boolean closeAuction(int auctionID, int ownerID) throws RemoteException;
    public AuctionItem getResult(int auctionID) throws RemoteException;
    public boolean bid(int auctionID, String name, String email, int bid) throws RemoteException;
    public boolean checkAuctionStatus(String name, int auctionID) throws RemoteException;
    public boolean isAuctionOpen(int auctionID) throws RemoteException;
    public int register(String email, String password) throws RemoteException;
    public int login(String email,String password) throws RemoteException;
    public int getHighestBid(int auctionID) throws RemoteException;
    public boolean verifyUser(byte[] message) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException;
    public byte[] sign(int challenge) throws RemoteException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException;
    public int getNumberForVerification() throws RemoteException;
}
