package applicazione.eccezioni;

public class DatiPrenotazioneNonValidiException extends RuntimeException {

    public DatiPrenotazioneNonValidiException(String messaggio) {
        super(messaggio);
    }
}