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
    public Item callGetSpec(int itemId, SealedObject clientReq) throws RemoteException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException;
    public String callAddItem(int itemId, String itemTitle, String itemDescription, String itemCondition) throws RemoteException;
    public int callCheckItem(int itemId) throws RemoteException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException;
    public int callCreateAuction(int itemID, int startingPrice, int reservePrice, int ownerID) throws RemoteException;
    public LinkedList<AuctionItem> callGetAuctions() throws RemoteException;
    public boolean callCloseAuction(int auctionID, int ownerID) throws RemoteException;
    public AuctionItem callGetResult(int auctionID) throws RemoteException;
    public boolean callBid(int auctionID, String name, String email, int bid) throws RemoteException;
    public boolean callCheckAuctionStatus(String name, int auctionID) throws RemoteException;
    public boolean callIsAuctionOpen(int auctionID) throws RemoteException;
    public int callRegister(String email, String password) throws RemoteException;
    public int callLogin(String email,String password) throws RemoteException;
    public int callGetHighestBid(int auctionID) throws RemoteException;
    public boolean callVerifyUser(byte[] message) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException;
    public byte[] callSign(int challenge) throws RemoteException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException;
    public int callGetNumberForVerification() throws RemoteException;
}
