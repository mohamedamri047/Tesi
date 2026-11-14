package presentazione.http.dto;

import dominio.modelli.PrestazioneMedica;

public class RispostaPrestazione {

    private int id;
    private String nome;
    private String descrizione;

    public RispostaPrestazione(PrestazioneMedica prestazione) {
        this.id = prestazione.getId();
        this.nome = prestazione.getNome();
        this.descrizione = prestazione.getDescrizione();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }
}