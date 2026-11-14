package applicazione.eccezioni;

public class SessioneNonValidaException extends RuntimeException {

    public SessioneNonValidaException() {
        super("Sessione assente, non valida o scaduta");
    }
}