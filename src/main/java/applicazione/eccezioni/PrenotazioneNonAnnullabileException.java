package applicazione.eccezioni;

public class PrenotazioneNonAnnullabileException extends RuntimeException {

    public PrenotazioneNonAnnullabileException(String messaggio) {
        super(messaggio);
    }
}