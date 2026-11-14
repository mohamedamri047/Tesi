package presentazione.http.dto;

import dominio.modelli.Utente;

public class RispostaLogin {

    private String token;
    private int id;
    private String nome;
    private String cognome;
    private String email;
    private String tipoUtente;

    public RispostaLogin(Utente utente) {
        this.id = utente.getId();
        this.nome = utente.getNome();
        this.cognome = utente.getCognome();
        this.email = utente.getEmail();
        this.tipoUtente = utente.getTipoUtente().getDescrizione();
    }

    public RispostaLogin(Utente utente, String token) {
        this(utente);
        this.token = token;
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

    public String getEmail() {
        return email;
    }

    public String getTipoUtente() {
        return tipoUtente;
    }

    public String getToken() {
        return token;
    }
}