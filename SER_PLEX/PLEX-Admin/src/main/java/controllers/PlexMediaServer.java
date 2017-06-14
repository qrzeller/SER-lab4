package controllers;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Quentin Zeller on 9.06.2017.
 */
public class PlexMediaServer implements RemotePrintToJson {

    private final ControleurMedia controleurMedia;

    public PlexMediaServer(ControleurMedia controleurMedia) {

        this.controleurMedia = controleurMedia;

    }

    public void startServer() {
        try {
            System.out.println("badbubf");
            Registry registry = LocateRegistry.createRegistry(9998);
            RemotePrintToJson printJson = (RemotePrintToJson) UnicastRemoteObject.exportObject(this, 9998);
            registry.bind("PlexMediaConnection", printJson);
            System.out.println("serveur PLEX créé");
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            System.out.println("Registry for Plex medida not created"+this.getClass());
            e.printStackTrace();
        }
    }

    public String getJsonPrint() {
        return controleurMedia.getJSONProjectionsToSend();
    }

}
