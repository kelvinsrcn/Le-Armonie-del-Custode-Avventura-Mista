package Eccezioni;

public class GameNotAvailableException extends Exception {

    @Override
    public String getMessage() {
        return "Errore nell'inizializzazione del gioco";
    }
    
}
