package ch.heigvd.iict.ser.imdb;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import ch.heigvd.iict.ser.imdb.db.MySQLAccess;
import ch.heigvd.iict.ser.imdb.models.Data;
import ch.heigvd.iict.ser.rmi.IClientApi;
import ch.heigvd.iict.ser.rmi.IServerApi;


public class Main {

    //Ajout de l'objet serveur RMI --> inner class
    private customRMIServer serverRMI = new customRMIServer(this);

    static {
        // this will load the MySQL driver, each DB has its own driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL drivers not found !");
            System.exit(1);
        }

        //database configuration
        MySQLAccess.MYSQL_URL 		= "docr.iict.ch";
        MySQLAccess.MYSQL_DBNAME 	= "imdb";
        MySQLAccess.MYSQL_USER 		= "imdb";
        MySQLAccess.MYSQL_PASSWORD 	= "imdb";
    }

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    private Data lastData = null;

    private void run() {

        boolean continuer = true;
        while(continuer) {
            System.out.print("Select the data version to download [1/2/3/0=quit]: ");
            int choice = -1;
            try {
                choice = scanner.nextInt();
            } catch(Exception e) {
                e.printStackTrace();
            }

            if(choice == 0) continuer = false;
            else if(choice >= 1 && choice <= 3) {
                Worker worker = new Worker(choice);
                this.lastData = worker.run();

                //TODO notify client
                //we notify them as is :
                serverRMI.setChanged();
                serverRMI.notifyObservers(new Date());

            }
        }
    }

    private class customRMIServer extends Observable implements IServerApi{


        private Main main;

        public customRMIServer(Main main) {

            this.main = main;
            try {
                Registry rmiRegistry = LocateRegistry.createRegistry(9999);
                IServerApi rmiService = (IServerApi) UnicastRemoteObject.exportObject(this, 9999);
                rmiRegistry.bind("RMI_Service", rmiService);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        public void setChanged(){
            super.setChanged();
        }


    /*
     * API - RmiService implementation
     */

        //@Override
        public void addObserver(IClientApi client) throws RemoteException {
            WrappedObserver wo = new WrappedObserver(client);
            addObserver(wo);
        }

        //@Override
        public boolean isStillConnected() throws RemoteException {
            return true;
        }

        //@Override
        public Data getData() throws RemoteException {

            //TODO remove main and recover lastdata from super
            return main.lastData;
        }

        /*
         *  Observer
         */
        private class WrappedObserver implements Observer, Serializable {

            private static final long serialVersionUID = -2067345842536415833L;

            private IClientApi ro = null;

            public WrappedObserver(IClientApi ro) {
                this.ro = ro;
            }

            //@Override
            public void update(Observable o, Object arg) {
                try {
                    ro.update(o.toString(), IClientApi.Signal.UPDATE_REQUESTED, arg.toString());
                } catch (RemoteException e) {
                    System.out.println("Remote exception removing observer: " + this);
                    o.deleteObserver(this);
                }
            }
        }
    }
}

