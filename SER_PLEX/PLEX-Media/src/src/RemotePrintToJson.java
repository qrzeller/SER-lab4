package src;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Quentin Zeller on 9.06.2017.
 */
//Toute implementation distante on besoin de Remote
public interface RemotePrintToJson extends Remote{
    public String getJsonPrint() throws RemoteException;
}
