package applicazione.eccezioni;

public class PrenotazioneNonTrovataException extends RuntimeException {

    public PrenotazioneNonTrovataException() {
        super("Prenotazione non trovata");
    }
}