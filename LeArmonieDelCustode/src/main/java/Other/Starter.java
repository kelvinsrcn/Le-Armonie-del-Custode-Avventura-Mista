package Other;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import Classifica.Server;
import Grafica.InterfacciaInizio;

public class Starter {
    public static void main(String[] args) {
        InterfacciaInizio interfacciaInizio = new InterfacciaInizio();
        interfacciaInizio.setVisible(true);
        try {
            Server server = new Server();
            boolean running = false;
            for (int i = 0; i < 5 && !running; i++) {
                try{
                    server.start();
                    running = true;
                } catch(Exception e){
                    try{
                        Thread.sleep(1000);
                    } catch(InterruptedException ex){
                        Logger.getLogger(Starter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (IOException e) {
            Logger.getLogger(Starter.class.getName()).log(Level.SEVERE, null, e);
        }

    }
}
