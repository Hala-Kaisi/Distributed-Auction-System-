package client;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.security.*;
import java.io.IOException;
import java.lang.Object;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;
import java.util.LinkedList;
import java.rmi.server.ServerNotActiveException;

public class Client implements ClientUnseal{

  private final String SERVER_NAME = "auction";
  public final int REGISTRY_PORT = 1099;


  private Client() {
  };

  PrivateKey priv;
  PublicKey pub;

  public Item unsealObject(SealedObject sealedItem) throws RemoteException, FileNotFoundException, IOException {
      Item returnedItem = new Item(-1, "Not available", "Not Available", "Not Available");

      try (//TODO remember to change the path of the file where the public key will be read from
      FileReader reader = new FileReader("SecretKey.txt")) {
        int i;
        int counter = 0;
        char keyChar[] = new char[24];
        while ((i = reader.read()) != -1) {
            keyChar[counter] = (char) i;
            counter++;
        }
        byte[] decodedKey = Base64.getDecoder().decode(String.valueOf(keyChar));
        SecretKey aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        try {
            returnedItem = (Item) sealedItem.getObject(aesKey);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
        }
      }

      ;
      return returnedItem;
  }

  public SealedObject sealClientID(int ID) throws RemoteException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {

      Serializable obj = ID;
      KeyGenerator key = KeyGenerator.getInstance("AES");
      key.init(128);
      SecretKey aesKey = key.generateKey();
      try {

          //TODO remember to change the path of the file where the public key will be stored at
          FileWriter myWriter = new FileWriter("XXXXXXX");

          myWriter.write(Base64.getEncoder().encodeToString(aesKey.getEncoded()));
          myWriter.close();
      } catch (Exception e) {
          System.err.println("Error: " + e.getMessage());
      }
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, aesKey);
      SealedObject sealedObject = new SealedObject(obj, cipher);
      return sealedObject;
  }

  public SealedObject createSealedID(int i) throws RemoteException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
      Client client = new Client();
      SealedObject s = client.sealClientID(i);
      return s;
  }

  public void checkItem(Spec server) throws ServerNotActiveException, RemoteException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException{
      System.out.print("\nEnter the item ID: ");
      Scanner in2 = new Scanner(System.in);
      int itemID = -1;
      try {
          itemID = in2.nextInt();
      } catch (Exception e) {
          System.out.println("\nERROR:\nItem ID is expected to be an integer. Please try again with a valid number.\n");
      }
      Item response = server.callGetSpec(itemID, createSealedID((int) ((Math.random() * (250 - 1)) + 1)));
      System.out.println("\n\nresponse:\n---------------\n" +
              "Item ID: " + response.itemId + "\nItem Title: "
              + response.itemTitle + "\nItem Description: " + response.itemDescription + "\nItem Condition: "
              + response.itemCondition + "\n");
  }

  public void addItem(Spec server) throws RemoteException{
      System.out.print("Enter the item ID: ");
      Scanner in1 = new Scanner(System.in);
      int itemID = -1;
      try {
          itemID = in1.nextInt();
      } catch (Exception e) {
          System.out.println("\nERROR:\nItem ID is expected to be an integer. Please try again with a valid number.\n");
          System.exit(0);
      }
      System.out.print("Enter the item title: ");
      Scanner in2 = new Scanner(System.in);
      String itemTitle = "";
      try {
          itemTitle = in2.nextLine();
      } catch (Exception e) {
          System.out.println("\nERROR:\n" + e + "\n");
          System.exit(0);
      }
      System.out.print("Enter the item description: ");
      Scanner in3 = new Scanner(System.in);
      String itemDescription = "";
      try {
          itemDescription = in3.nextLine();
      } catch (Exception e) {
          System.out.println("\nERROR:\n" + e + "\n");
          System.exit(0);
      }
      System.out.print("Enter the item condition (used/new): ");
      Scanner in4 = new Scanner(System.in);
      String itemCondition = "";
      try {
          itemCondition = in4.nextLine();
      } catch (Exception e) {
          System.out.println("\nERROR:\n" + e + "\n");
          System.exit(0);
      }
      String answer = server.callAddItem(itemID, itemTitle, itemDescription, itemCondition);
      System.out.println("\n----------------\n" + answer + "\n");
  }

  public void seller(Spec server, int clientID) throws RemoteException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
      System.out.print("\n!!You can exit using CONTROL+C!!\n\nEnter the item ID to be placed for auction: ");
      Scanner in1 = new Scanner(System.in);
      int itemID = -1;
      try {
          itemID = in1.nextInt();
      } catch (Exception e) {
          System.out.println("\nERROR:\nItem ID is expected to be an integer. Please try again with a valid number.\n");
          seller(server, clientID);
      }
      int checkItem = server.callCheckItem(itemID);
      if (checkItem == -1) {
          System.out.println("Entered item ID " + itemID + " is not an item on the list. Use method 2 to add the item.\n");
          seller(server, clientID);
      } else if (checkItem == 0) {
          System.out.println("Entered item ID " + itemID + " is/was already placed for auction.\n");
          seller(server,clientID);
      } else {
          System.out.print("\nEnter the auction starting price: ");
          Scanner in2 = new Scanner(System.in);
          int startingPrice = -1;
          try {
              startingPrice = in2.nextInt();
          } catch (Exception e) {
              System.out.println("\nERROR:\nItem ID is expected to be an integer. Please try again with a valid number.\n");
              seller(server, clientID);
          }
          System.out.print("\nEnter the reserve price: ");
          Scanner in3 = new Scanner(System.in);
          int reservePrice = -1;
          try {
              reservePrice = in3.nextInt();
          } catch (Exception e) {
              System.out.println("\nERROR:\nItem ID is expected to be an integer. Please try again with a valid number.\n");
              seller(server, clientID);
          }
          int createAuction = server.callCreateAuction(itemID, startingPrice, reservePrice, clientID);
          if (createAuction != -1) {
              System.out.println("\n-------------\nAuction has been created. The auction ID is " + createAuction + "\n\n");

          } else {
              System.out.println("An error has occured, the auction has not been created");
              seller(server, clientID);
          }
      }
  }

  public void buyer(Spec server) throws RemoteException, InterruptedException{
      System.out.println("\n!!You can exit using CONTROL+C!!\n\nCurrent live auctions:\n--------------------\n");
      LinkedList<AuctionItem> currentAuctions = server.callGetAuctions();
      if (currentAuctions.size() == 0) {
          System.out.println("No current active auctions.\n");
      } else {
          for (int i = 0; i < currentAuctions.size(); i++) {
              System.out.println("Auction ID: " + currentAuctions.get(i).auctionId);
              System.out.println("Item ID: " + currentAuctions.get(i).itemId);
              if(currentAuctions.get(i).highestBid != 0) {
                  System.out.println("Current highest bid: " + currentAuctions.get(i).highestBid + "\n");
              }
              else{
                  System.out.println("Starting price: " + currentAuctions.get(i).startingPrice + "\n");
              }
          }
          System.out.println("Enter auction ID");
          Scanner in1 = new Scanner(System.in);
          int auctionID = -1;
          try {
              auctionID = in1.nextInt();
          } catch (Exception e) {
              System.out.println("\nERROR:\nAuction ID is expected to be an integer. Please try again with a valid number.\n");
              buyer(server);
          }
          currentAuctions = server.callGetAuctions();
          String name = "";
          int bid = -1;
          for (int i = 0; i < currentAuctions.size(); i++) {
              if (currentAuctions.get(i).auctionId == auctionID) {
                  while(true) {
                      if(server.callGetHighestBid(auctionID) != 0){
                          System.out.println("\nCurrent highest bid: " + server.callGetHighestBid(auctionID));
                      }
                      if (server.callIsAuctionOpen(auctionID)) {
                          System.out.println("\nEnter your full name: ");
                          Scanner in2 = new Scanner(System.in);
                          try {
                              name = in2.nextLine();
                          } catch (Exception e) {
                              System.out.println("\nERROR:\n" + e + "\n");
                              buyer(server);
                          }
                          System.out.println("Enter your email: ");
                          Scanner in3 = new Scanner(System.in);
                          String email = "";
                          try {
                              email = in3.nextLine();
                          } catch (Exception e) {
                              System.out.println("\nERROR:\n" + e + "\n");
                              buyer(server);
                          }
                          System.out.println("Enter your bid for auction number " + auctionID + ":");
                          Scanner in4 = new Scanner(System.in);
                          try {
                              bid = in4.nextInt();
                          } catch (Exception e) {
                              System.out.println("\nERROR:\nBid is expected to be an integer. Please try again with a valid number.\n");
                              buyer(server);
                          }
                          boolean bidResult = server.callBid(auctionID, name, email, bid);
                          if (bidResult) {
                              System.out.println("You placed now the highest bid!\n\nBid again? (y/n)");
                              Scanner in5 = new Scanner(System.in);
                              char answer = 'z';
                              try {
                                  answer = in5.next().charAt(0);
                              } catch (Exception e) {
                                  System.out.println("\nERROR:\n" + e + "\n");
                              }
                              if (answer == 'n' || answer == 'N') {
                                  break;
                              } else if (answer != 'y' && answer != 'Y') {
                                  System.out.println("\nExpected n or y\n");
                                  break;
                              }

                          } else {
                              System.out.println("An error has occured. The highest bid could have been changed or the auction has been closed.");
                              buyer(server);
                          }
                      }
                      else{
                          System.out.println("\nThe auction has been closed.\n");
                          break;
                      }
                  }
                  System.out.println("\nListening to auction results...\n");
                  boolean isOpened = server.callIsAuctionOpen(auctionID);
                  while (isOpened) {
                      isOpened = server.callIsAuctionOpen(auctionID);
                  }
                  boolean didIWin = server.callCheckAuctionStatus(name, auctionID);
                  if (didIWin) {
                      System.out.println("\n\nCongratulation! You won the auction at a price of " + bid + "\n\n");
                      return;
                  }
                  System.out.println("\n\nThe auction was closed and you were not the highest bid.\n\n");
                  return;
              }
          }
      }
  }

  public void close (Spec server, int clientID){
      System.out.println("Enter auction ID: ");
      Scanner in1 = new Scanner(System.in);
      int check = -1;
      try {
          check = in1.nextInt();
          boolean closeAuction = server.callCloseAuction(check, clientID);
          if (closeAuction) {
              System.out.println("\nResult:\n-------------");
              AuctionItem result = server.callGetResult(check);
              if (result.highestBid < result.reservePrice) {
                  System.out.println("\nThe reserve price has not been reached.\n");
                  return;
              } else {
                  System.out.println("\nName: " + result.winnerName + "\nEmail: " + result.winnerEmail +
                          "\nHighest bid: " + result.highestBid);
                  return;
              }
          }
          else {
                  System.out.println("\nThe auction has not been closed. Auction ID could be wrong or you do not have access to the auction.\n");
                  return;
              }
      } catch (Exception e) {
          System.out.println(e);
          return;
      }
  }

  public int enterSystem(int methodID, Spec server) throws RemoteException{
      if(methodID == 1){
          System.out.println("Enter your email: ");
          Scanner in2 = new Scanner(System.in);
          String email = "";
          try {
              email = in2.nextLine();
          } catch (Exception e) {
              System.out.println("\nERROR:\n" + e + "\n");
          }
          System.out.println("Enter your password: ");
          Scanner in3 = new Scanner(System.in);
          String password = "";
          try {
              password = in3.nextLine();
          } catch (Exception e) {
              System.out.println("\nERROR:\n" + e + "\n");
          }
          int clientID = server.callRegister(email, password);
          return clientID;
      }
      else if(methodID == 2){
          System.out.println("Enter your email: ");
          Scanner in2 = new Scanner(System.in);
          String email = "";
          try {
              email = in2.nextLine();
          } catch (Exception e) {
              System.out.println("\nERROR:\n" + e + "\n");
          }
          System.out.println("Enter your password: ");
          Scanner in3 = new Scanner(System.in);
          String password = "";
          try {
              password = in3.nextLine();
          } catch (Exception e) {
              System.out.println("\nERROR:\n" + e + "\n");
          }
          int clientID = server.callLogin(email, password);
          if(clientID != -1) return clientID;
      }
      return -1;
  }

  public boolean verifyServer(byte[] message, int myNumber) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException {

      //TODO remember to change the path of the file where the public key will be read from
      FileReader reader = new FileReader("XXXXXXX");

      int i;
      int counter = 0;
      char keyChar[] = new char[592];
      while ((i = reader.read()) != -1) {
          keyChar[counter] = (char) i;
          counter++;
      }
      byte[] decodedKey =  Base64.getDecoder().decode(String.valueOf(keyChar));
      X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(decodedKey);
      KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
      PublicKey serverPubKey =
              keyFactory.generatePublic(pubKeySpec);
      Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
      sig.initVerify(serverPubKey);
      byte messageBytes = (byte) myNumber;
      sig.update(messageBytes);
      return sig.verify(message);
  }

  public byte[] signMessage(int challenge) throws RemoteException, NoSuchAlgorithmException, InvalidKeyException, SignatureException,NoSuchProviderException {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
      keyGen.initialize(1024);
      KeyPair pair = keyGen.generateKeyPair();
      priv = pair.getPrivate();
      pub = pair.getPublic();
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

  public static void main(String[] args) {
    Client c = new Client();
    try {
        Registry registry = LocateRegistry.getRegistry();
        Spec server = (Spec) registry.lookup(c.SERVER_NAME);
        System.out.println("\nVerifying server...\n");
        int serverVerification = server.callGetNumberForVerification();
        byte[] message = c.signMessage(serverVerification);
        boolean isServer = server.callVerifyUser(message);
        if(isServer) {
            System.out.println("Server verified successfully!\n\n\nVerifying identity at server...\n");
            int mySignNumber = (int) ((Math.random() * (250 - 1)) + 1);
            byte[] messageToServer = server.callSign(mySignNumber);
            boolean verifyMe = c.verifyServer(messageToServer, mySignNumber);
            if(verifyMe) {
                System.out.println("Identity verified successfully!\n\n\nConnection is secured. redirecting you to the log-in menu...\n\n");
                while (true) {
                    System.out.println("Enter 1 to register, or 2 to log-in: ");
                    Scanner in = new Scanner(System.in);
                    int enter = -1;
                    try {
                        enter = in.nextInt();
                    } catch (Exception e) {
                        System.out.println("\nERROR:\nMethod ID is expected to be an integer. Please try again with a valid number.\n");
                    }
                    if (enter == 1 || enter == 2) {
                        int returnedValue = c.enterSystem(enter, server);
                        if (returnedValue == -1) {
                            System.out.println("\nLog-in failed. email or password are either incorrect or account not registered\n");
                        } else {
                            while (true) {
                                System.out.print("\n------------------------------------\n\nAvailable methods:\n-----------------\n" +
                                        "Method 1: Fetch an item with item ID.\n" +
                                        "Method 2: Add an item.\nMethod 3: Create an auction giving an item ID.\nMethod 4: Bid on an auction." +
                                        "\nMethod 5: Close an auction.\nMethod 3: Exit.\n\nEnter the method number: ");
                                Scanner in1 = new Scanner(System.in);
                                int methodID = -1;
                                try {
                                    methodID = in1.nextInt();
                                } catch (Exception e) {
                                    System.out.println("\nERROR:\nMethod ID is expected to be an integer. Please try again with a valid number.\n");
                                }
                                if (methodID == 1) {
                                    c.checkItem(server);
                                } else if (methodID == 2) {
                                    c.addItem(server);
                                } else if (methodID == 3) {
                                    c.seller(server, returnedValue);
                                } else if (methodID == 4) {
                                    c.buyer(server);
                                } else if (methodID == 5) {
                                    c.close(server, returnedValue);
                                } else if (methodID == 6) {
                                    System.exit(0);
                                } else {
                                    System.out.println("\n----------------\nMethod ID does not exist.\n");
                                }
                            }
                        }
                    } else {
                        System.out.println("\nServer is not verified.\n");
                    }
                }
            }
            else{
                System.out.println("\nMethod ID could either be 1 or 2. Please try again with a valid number.\n");
            }
        }
        else{
            System.out.println("\nIdentity verification failed at server.\n");
        }
    } catch (Exception e) {
        System.err.println("Client exception: " + e.toString());
        e.printStackTrace();
    }

  }
}