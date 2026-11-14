package presentazione.http.dto;

import dominio.modelli.PrenotazioneDettaglio;
import java.time.format.DateTimeFormatter;

public class RispostaPrenotazioneDettaglio {

    private int id;
    private String prestazione;
    private String medico;
    private String data;
    private String oraInizio;
    private String stato;

    private static final DateTimeFormatter FORMATO_ORA = DateTimeFormatter.ofPattern("HH:mm");

    public RispostaPrenotazioneDettaglio(PrenotazioneDettaglio prenotazione) {
        this.id = prenotazione.getId();
        this.prestazione = prenotazione.getPrestazione();
        this.medico = prenotazione.getMedico();
        this.data = prenotazione.getData().toString();
        this.oraInizio = prenotazione.getOraInizio().format(FORMATO_ORA);
        this.stato = prenotazione.getStato();
    }

    public int getId() {
        return id;
    }

    public String getMedico() {
        return medico;
    }

    public String getData() {
        return data;
    }

    public String getOraInizio() {
        return oraInizio;
    }

    public String getStato() {
        return stato;
    }

    public String getPrestazione() {
        return prestazione;
    }
}