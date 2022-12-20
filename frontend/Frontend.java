package frontend;
import org.jgroups.JChannel;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.RspList;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedList;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

import utility.GroupUtils;

public class Frontend extends UnicastRemoteObject implements Spec{

  public static final long serialVersionUID = 42069;
  public final String SERVER_NAME = "auction";
  public final int REGISTRY_PORT = 1099;

  private JChannel groupChannel;
  private RpcDispatcher dispatcher;

  private final int DISPATCHER_TIMEOUT = 1000;

  public Frontend() throws RemoteException {
    // Connect to the group (channel)
    this.groupChannel = GroupUtils.connect();
    if (this.groupChannel == null) {
      System.exit(1); // error to be printed by the 'connect' function
    }

    // Bind this server instance to the RMI Registry
    this.bind(this.SERVER_NAME);

    // Make this instance of Frontend a dispatcher in the channel (group)
    this.dispatcher = new RpcDispatcher(this.groupChannel, this);
    this.dispatcher.setMembershipListener(new MembershipListener());

  }

  private void bind(String serverName) {
    try {
      Registry registry = LocateRegistry.createRegistry(this.REGISTRY_PORT);
      registry.rebind(serverName, this);
      System.out.println("✅    rmi server running...");
    } catch (Exception e) {
      System.err.println("🆘    exception:");
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void main(String args[]) {
    try {
      new Frontend();
    } catch (RemoteException e) {
      System.err.println("🆘    remote exception:");
      e.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  public Item callGetSpec(int itemId, SealedObject clientReq) throws RemoteException, IOException,
      NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
    // TODO Auto-generated method stub
    Item item = new Item(-1, "Not available", "Not Available", "Not Available");
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "getSpec",
          new Object[] { itemId, clientReq}, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return item;
      }

      // Iterate through the responses to build a total of all the responses
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

  @Override
  public String callAddItem(int itemId, String itemTitle, String itemDescription, String itemCondition)
      throws RemoteException {
    // TODO Auto-generated method stub
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "addItem",
          new Object[] { itemId, itemTitle, itemDescription, itemCondition}, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return "No responses have been received";
      }

      // Iterate through the responses to build a total of all the responses
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

  @Override
  public int callCheckItem(int itemId) throws RemoteException, IOException, NoSuchAlgorithmException,
      NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
    // TODO Auto-generated method stub
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "checkItem",
          new Object[] { itemId }, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return -1;
      }

      // Iterate through the responses to build a total of all the responses
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

  @Override
  public int callCreateAuction(int itemID, int startingPrice, int reservePrice, int ownerID) throws RemoteException {
    // TODO Auto-generated method stub
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "createAuction",
          new Object[] {itemID, startingPrice, reservePrice, ownerID}, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return -1;
      }
      // Iterate through the responses to build a total of all the responses
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

  @Override
  public LinkedList<AuctionItem> callGetAuctions() throws RemoteException {
    // TODO Auto-generated method stub
    LinkedList<AuctionItem> liveAuctions
                = new LinkedList<AuctionItem>();
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "getAuctions",
          new Object[] {}, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return liveAuctions;
      }

      // Iterate through the responses to build a total of all the responses
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

  @Override
  public boolean callCloseAuction(int auctionID, int ownerID) throws RemoteException {
    // TODO Auto-generated method stub
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "closeAuction",
          new Object[] {auctionID, ownerID }, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return false;
      }

      // Iterate through the responses to build a total of all the responses
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

  @Override
  public AuctionItem callGetResult(int auctionID) throws RemoteException {
    // TODO Auto-generated method stub
    AuctionItem auction = new AuctionItem(-1, -1, -1, -1, -1);
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "getResult",
          new Object[] { auctionID }, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return auction;
      }
      // Iterate through the responses to build a total of all the responses
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

  @Override
  public boolean callBid(int auctionID, String name, String email, int bid) throws RemoteException {
    // TODO Auto-generated method stub
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "bid",
          new Object[] { auctionID, name, email, bid }, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return false;
      }
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

  @Override
  public boolean callCheckAuctionStatus(String name, int auctionID) throws RemoteException {
    // TODO Auto-generated method stub
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "checkAuctionStatus",
          new Object[] { name, auctionID }, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return false;
      }
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

  @Override
  public boolean callIsAuctionOpen(int auctionID) throws RemoteException {
    // TODO Auto-generated method stub
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "isAuctionOpen",
          new Object[] { auctionID }, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return false;
      }
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

  @Override
  public int callRegister(String email, String password) throws RemoteException {
    // TODO Auto-generated method stub
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "callRegister",
          new Object[] { email, password }, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return -1;
      }
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

  @Override
  public int callLogin(String email, String password) throws RemoteException {
    // TODO Auto-generated method stub
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "callLogin",
          new Object[] { email, password }, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return -1;
      }
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

  @Override
  public int callGetHighestBid(int auctionID) throws RemoteException {
    // TODO Auto-generated method stub
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "callGetHighestBid",
          new Object[] { auctionID }, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return -1;
      }
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

  @Override
  public boolean callVerifyUser(byte[] message) throws IOException, InvalidKeyException, NoSuchAlgorithmException,
      InvalidKeySpecException, SignatureException, NoSuchProviderException {
    // TODO Auto-generated method stub
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "verifyUser",
          new Object[] { message }, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return false;
      }
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

  @Override
  public int callGetNumberForVerification() throws RemoteException {
    // TODO Auto-generated method stub
    try {

      // Call the "generateRandomNumber" function on all the group members, passing 2
      // params of object class integer
      RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "getNumberForVerification",
          new Object[] {}, new Class[] { int.class, int.class },
          new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

      System.out.printf("#️⃣    received %d responses from the group\n", responses.size());
      if (responses.isEmpty()) {
        return -1;
      }
      return responses.getResults().get(0);
    } catch (Exception e) {
      System.err.println("🆘    dispatcher exception:");
      e.printStackTrace();
    }
  }

}
