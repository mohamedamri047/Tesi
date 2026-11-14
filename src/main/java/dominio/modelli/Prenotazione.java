package dominio.modelli;

public class Prenotazione {

    private int id;
    private int idPaziente;
    private int idPrestazione;
    private int idDisponibilita;
    private String stato;

    public Prenotazione(int id, int idPaziente, int idPrestazione, int idDisponibilita, String stato) {
        this.id = id;
        this.idPaziente = idPaziente;
        this.idPrestazione = idPrestazione;
        this.idDisponibilita = idDisponibilita;
        this.stato = stato;
    }

    public int getId() {
        return id;
    }

    public String getStato() {
        return stato;
    }
}