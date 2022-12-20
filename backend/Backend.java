package backend;
import org.jgroups.JChannel;
import org.jgroups.blocks.RpcDispatcher;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.Random;
import utility.GroupUtils;

public class Backend implements Spec{

  private JChannel groupChannel;
  private RpcDispatcher dispatcher;
  private int requestCount;

  public Backend() {
    this.requestCount = 0;

    // Connect to the group (channel)
    this.groupChannel = GroupUtils.connect();
    if (this.groupChannel == null) {
      System.exit(1); // error to be printed by the 'connect' function
    }

    // Make this instance of Backend a dispatcher in the channel (group)
    this.dispatcher = new RpcDispatcher(this.groupChannel, this);
  }

  LinkedList<Item> items
            = new LinkedList<Item>();
    LinkedList<AuctionItem> auctionItems
            = new LinkedList<AuctionItem>();
    LinkedList<user> users = new LinkedList<>();

    PrivateKey priv;
    PublicKey pub;
    int myVerificationNumber;

    public void setItems(){
        items.add(new Item(1, "Watch", "Rolex watch", "used"));
        items.add(new Item(2, "Bag", "Hermes bag", "new"));
        items.add(new Item(3, "Laptop", "Mac laptop", "new"));
        items.add(new Item(4, "Scarf", "Versace scarf", "used"));
        items.add(new Item(5, "Earring", "Pandora earring", "new"));
    }

    public SealedObject getSpec(int itemId, SealedObject clientReq) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
        Item item = new Item(-1, "Not available", "Not Available", "Not Available");
        for(int i=0; i < items.size(); i++){
            if (itemId == items.get(i).itemId) {
                item = items.get(i);
                break;
            }
        }

        //TODO remember to change the path of the file where the public key will be stored at
        FileReader reader = new FileReader("XXXXXXX");

        int i;
        int counter = 0;
        char keyChar[] = new char[24];
        while ((i = reader.read()) != -1) {
            keyChar[counter] = (char) i;
            counter++;
        }
        byte[] decodedKey = Base64.getDecoder().decode(String.valueOf(keyChar));
        SecretKey aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        Serializable obj = item;
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        SealedObject sealedObject = new SealedObject(obj, cipher);
        return sealedObject;
    }

    public String addItem(int itemId, String itemTitle, String itemDescription, String itemCondition){
        for(int i=0; i < items.size(); i++){
            if (itemId == items.get(i).itemId) {
                return "\nItem ID " + itemId + " is already in use. Please try another one.\n";
            }
        }
        try {
            items.add(new Item(itemId, itemTitle, itemDescription, itemCondition));
            return "\nItem has been added successfully.\n";
        }
        catch (Exception e){
            System.out.println(e);
            return "\nAn error occured while trying to add the item. Please try again later.\n";
        }
    }

    public int checkItem(int itemId){
        for(int i=0; i < items.size(); i++){
            if (itemId == items.get(i).itemId) {
                for(int a=0; a < auctionItems.size(); a++){
                    if (itemId == auctionItems.get(a).itemId){
                        return 0;
                    }
                }
                return 1;
            }
        }
        return -1;
    }

    public int createAuction(int itemID, int startingPrice, int reservePrice, int ownerID) {
        try {
            int auctionId = (int) ((Math.random() * (200 - 0)) + 0);
            auctionItems.add(new AuctionItem(auctionId, itemID, startingPrice, reservePrice, ownerID));
            return auctionId;
        }
        catch(Exception e){
                System.out.println(e);
                System.out.println("\nAn error occured while trying to add the item. Please try again later.\n");
                return -1;
        }
    }

    public LinkedList<AuctionItem> getAuctions(){
        LinkedList<AuctionItem> liveAuctions
                = new LinkedList<AuctionItem>();
        for (int i = 0; i < auctionItems.size(); i++) {
            if (auctionItems.get(i).open) {
                liveAuctions.add(auctionItems.get(i));
            }
        }
        return liveAuctions;
    }

    public boolean closeAuction(int auctionID, int ownerID){
        for (int i = 0; i < auctionItems.size(); i++) {
            if (auctionID == auctionItems.get(i).auctionId) {
                if(auctionItems.get(i).ownerId == ownerID) {
                    auctionItems.get(i).open = false;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean bid(int auctionID, String name, String email, int bid){
        for (int i = 0; i < auctionItems.size(); i++){
            if(auctionID == auctionItems.get(i).auctionId){
                if(auctionItems.get(i).highestBid == 0 && bid > 0){
                    auctionItems.get(i).highestBid = bid;
                    auctionItems.get(i).winnerName = name;
                    auctionItems.get(i).winnerEmail = email;
                    return true;
                }
                else if (bid > auctionItems.get(i).highestBid){
                    auctionItems.get(i).highestBid = bid;
                    auctionItems.get(i).winnerName = name;
                    auctionItems.get(i).winnerEmail = email;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkAuctionStatus(String name, int auctionID){
        for (int i = 0; i < auctionItems.size(); i++) {
            if (auctionID == auctionItems.get(i).auctionId) {
                if (auctionItems.get(i).winnerName.equals(name) && auctionItems.get(i).highestBid >= auctionItems.get(i).reservePrice ) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAuctionOpen(int auctionID) {
        for (int i = 0; i < auctionItems.size(); i++) {
            if (auctionID == auctionItems.get(i).auctionId) {
                if (auctionItems.get(i).open) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public AuctionItem getResult(int auctionID) {
        AuctionItem auction = new AuctionItem(-1, -1, -1, -1, -1);
        for (int i = 0; i < auctionItems.size(); i++) {
            if (auctionID == auctionItems.get(i).auctionId) {
                auction = auctionItems.get(i);
            }
        }
        return auction;
    }

    public int register(String email, String password){
        int userID = (int) ((Math.random() * (250 - 1)) + 1);
        users.add(new user(email, password, userID));
        return userID;
    }

    public int login(String email, String password){
        for (user user : users) {
            if (email.equals(user.email) && password.equals(user.password)) {
                return user.ID;
            }
        }
        return -1;
    }

    public byte[] sign(int challenge) throws RemoteException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
        keyGen.initialize(1024);
        KeyPair pair = keyGen.generateKeyPair();
        priv = pair.getPrivate();
        pub = pair.getPublic();
        System.out.println(pub);
        try {

            //TODO remember to change the path of the file where the public key will be stored at
            FileWriter myWriter = new FileWriter("XXXXXXX");

            myWriter.write(Base64.getEncoder().encodeToString(pub.getEncoded()));
            myWriter.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
        dsa.initSign(priv);
        byte b = (byte) challenge;
        dsa.update(b);
        return dsa.sign();
    }

    public int getNumberForVerification() throws RemoteException{
       myVerificationNumber= (int) ((Math.random() * (250 - 1)) + 1);
       return myVerificationNumber;
    }

    public boolean verifyUser(byte[] message) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException {

        //TODO remember to change the path of the file where the public key will be read from
        FileReader reader = new FileReader("SecretKey.txt");

        int i;
        int counter = 0;
        char keyChar[] = new char[592];
        while ((i = reader.read()) != -1) {
            keyChar[counter] = (char) i;
            counter++;
        }
        System.out.println(keyChar);
        byte[] decodedKey =  Base64.getDecoder().decode(String.valueOf(keyChar));
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
        PublicKey userPubKey =
                keyFactory.generatePublic(pubKeySpec);
        Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
        sig.initVerify(userPubKey);
        byte messageBytes = (byte) myVerificationNumber;
        sig.update(messageBytes);
        return sig.verify(message);
    }

    public int getHighestBid(int auctionID){
        for (int i = 0; i < auctionItems.size(); i++) {
            if (auctionID == auctionItems.get(i).auctionId) {
                return auctionItems.get(i).highestBid;
            }
        }
        return -1;
    }

  public static void main(String args[]) {
    new Backend();
  }

}
