package presentazione.http.dto;

import dominio.modelli.Medico;

public class RispostaMedico {

    private int id;
    private String nome;
    private String cognome;
    private String specializzazione;

    public RispostaMedico(Medico medico) {
        this.id = medico.getId();
        this.nome = medico.getNome();
        this.cognome = medico.getCognome();
        this.specializzazione = medico.getSpecializzazione().getDescrizione();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getSpecializzazione() {
        return specializzazione;
    }
}