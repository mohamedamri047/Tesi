package presentazione.http.dto;

import dominio.modelli.Prenotazione;

public class RispostaPrenotazione {

    private int id;
    private String stato;

    public RispostaPrenotazione(Prenotazione prenotazione) {
        this.id = prenotazione.getId();
        this.stato = prenotazione.getStato();
    }

    public int getId() {
        return id;
    }

    public String getStato() {
        return stato;
    }
}