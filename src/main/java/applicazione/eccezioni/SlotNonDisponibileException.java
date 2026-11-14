package applicazione.eccezioni;

public class SlotNonDisponibileException extends RuntimeException {

    public SlotNonDisponibileException() {
        super("La disponibilità selezionata non è più prenotabile");
    }
}