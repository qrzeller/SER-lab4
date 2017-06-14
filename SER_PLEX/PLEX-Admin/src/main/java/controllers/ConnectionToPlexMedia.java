package controllers;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Quentin Zeller on 9.06.2017.
 */
public class ConnectionToPlexMedia implements RemotePrintToJson {

    private final ControleurMedia controleurMedia;

    public ConnectionToPlexMedia(ControleurMedia controleurMedia) {

        this.controleurMedia = controleurMedia;

    }

    public void startServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(9998);
            RemotePrintToJson printJson = (RemotePrintToJson) UnicastRemoteObject.exportObject(this, 9998);
            registry.bind("PlexMediaConnection", printJson);
            System.out.println("serveur PLEX créé");
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public String getJsonPrint() {
        return controleurMedia.getJSONProjections();
    }

}
