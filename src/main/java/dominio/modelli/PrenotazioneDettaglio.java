package dominio.modelli;

import java.time.LocalDate;
import java.time.LocalTime;

public class PrenotazioneDettaglio {

    private final int id;
    private final String prestazione;
    private final String medico;
    private final LocalDate data;
    private final LocalTime oraInizio;
    private final String stato;

    public PrenotazioneDettaglio(int id, String prestazione, String medico, LocalDate data, LocalTime oraInizio, String stato) {
        this.id = id;
        this.prestazione = prestazione;
        this.medico = medico;
        this.data = data;
        this.oraInizio = oraInizio;
        this.stato = stato;
    }

    public int getId() {
        return id;
    }

    public String getPrestazione() {
        return prestazione;
    }

    public String getMedico() {
        return medico;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public String getStato() {
        return stato;
    }
}