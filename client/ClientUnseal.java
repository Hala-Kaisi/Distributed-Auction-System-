import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

public interface ClientUnseal extends Remote{
    public Item unsealObject(SealedObject sealedItem) throws RemoteException, FileNotFoundException, IOException;
}

