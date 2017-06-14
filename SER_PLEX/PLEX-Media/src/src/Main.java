package src;

import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * Created by Quentin Zeller on 9.06.2017.
 */
public class Main {


    public static void main(String[] args) {
        PlexMedia plex = new PlexMedia("Plex-Media");

    }
}
class PlexMedia extends JFrame{

        public PlexMedia(String s) {
            super(s);
            setLayout(new FlowLayout());
            JButton onlyButton = new JButton("Print the JSON");
            onlyButton.setFont(new Font("Arial", Font.PLAIN, 40));
            add(onlyButton);
            pack();


            onlyButton.addActionListener(e -> {
                        printJSON();
                    }
            );


            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);
        }

        private void printJSON(){
            try {
                Registry registry = LocateRegistry.getRegistry(9998);
                RemotePrintToJson json = (RemotePrintToJson) Naming.lookup("//localhost:9998/PlexMediaConnection");

                System.out.println("connexion OK");

                System.out.println(json.getJsonPrint());
            } catch (Exception e) {
                System.out.println("Error connecting to server :"+ this.getClass());
                e.printStackTrace();
            }
        }
    }
