package dominio.modelli;

public class UtenteAutenticato {

    private final int idUtente;
    private final String ruolo;

    public UtenteAutenticato(int idUtente, String ruolo) {
        this.idUtente = idUtente;
        this.ruolo = ruolo;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public String getRuolo() {
        return ruolo;
    }
}