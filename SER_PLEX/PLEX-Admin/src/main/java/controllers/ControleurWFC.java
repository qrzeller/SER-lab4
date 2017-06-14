package controllers;

import ch.heigvd.iict.ser.imdb.models.Data;
import ch.heigvd.iict.ser.rmi.IClientApi;
import ch.heigvd.iict.ser.rmi.IServerApi;
import views.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ControleurWFC extends UnicastRemoteObject implements IClientApi{

	private ControleurGeneral controleurGeneral;
	private static final long serialVersionUID = -8478788162368553187L;
	private static MainGUI mainGUI;
	private IServerApi remoteService = null;

	protected ControleurWFC(ControleurGeneral controleurGeneral, MainGUI mainGUI) throws RemoteException {
		super();
		this.controleurGeneral = controleurGeneral;
		try {
			remoteService = (IServerApi) Naming.lookup("//localhost:9999/RMI_Service");
			remoteService.addObserver(this);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Connection not established "+ this.getClass());
		}
		startCheckingThread();
	}


	protected void startCheckingThread() {
		//we start a thread to periodically check if the server is available
		Thread thread = new Thread(){
			@Override
			public void run() {
				boolean isStillConnectedToServer = true;
				while(isStillConnectedToServer) {
					//every 10 seconds
					try {
						Thread.sleep(10 * 1000);
					} catch (InterruptedException e) { }
					try {
						isStillConnectedToServer = remoteService.isStillConnected();
					} catch (RemoteException e) {
						isStillConnectedToServer = false;
					}
				}
				System.err.println("Server is not avalaible anymore, we stop client");
				System.exit(1);
			};
		};
		thread.start();
	}

	/*
	 * API - RemoteObserver implementation
	 */
	//@Override
	public void update(Object observable, IClientApi.Signal signalType, String updateMsg) throws RemoteException {
		Data data = remoteService.getData();
		controleurGeneral.initBaseDeDonneesAvecNouvelleVersion(data);
		System.out.println("UPDATE");
	}

}