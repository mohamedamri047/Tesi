package dominio.modelli;

public class TipoUtente {

    private int id;
    private String descrizione;

    public TipoUtente(int id, String descrizione) {
        this.id = id;
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }
}