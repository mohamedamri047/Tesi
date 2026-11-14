package dominio.modelli;

import java.time.LocalDate;
import java.time.LocalTime;

public class Disponibilita {

    private int id;
    private int idMedico;
    private LocalDate data;
    private LocalTime oraInizio;
    private boolean disponibile;

    public Disponibilita(int id, int idMedico, LocalDate data, LocalTime oraInizio, boolean disponibile) {
        this.id = id;
        this.idMedico = idMedico;
        this.data = data;
        this.oraInizio = oraInizio;
        this.disponibile = disponibile;
    }

    public int getId() {
        return id;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }
}