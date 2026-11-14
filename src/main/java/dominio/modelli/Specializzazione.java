package dominio.modelli;

public class Specializzazione {

    private int id;
    private String descrizione;

    public Specializzazione(int id, String descrizione) {
        this.id = id;
        this.descrizione = descrizione;
    }

    public int getId() {
        return id;
    }

    public String getDescrizione() {
        return descrizione;
    }
}