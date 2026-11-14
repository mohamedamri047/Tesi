package applicazione.eccezioni;

public class AccessoNegatoException extends RuntimeException {

    public AccessoNegatoException() {
        super("L'utente non dispone dei permessi necessari");
    }
}