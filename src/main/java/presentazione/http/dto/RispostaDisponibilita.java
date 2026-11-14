package presentazione.http.dto;

import dominio.modelli.Disponibilita;
import java.time.format.DateTimeFormatter;

public class RispostaDisponibilita {

    private int id;
    private String data;
    private String oraInizio;

    private static final DateTimeFormatter FORMATO_ORA = DateTimeFormatter.ofPattern("HH:mm");

    public RispostaDisponibilita(Disponibilita disponibilita) {
        this.id = disponibilita.getId();
        this.data = disponibilita.getData().toString();
        this.oraInizio = disponibilita.getOraInizio().format(FORMATO_ORA);
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public String getOraInizio() {
        return oraInizio;
    }
}