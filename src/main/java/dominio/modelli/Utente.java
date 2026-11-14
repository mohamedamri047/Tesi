package dominio.modelli;

public class Utente {
    private int id;
    private String nome;
    private String cognome;
    private String email;
    private String passwordHash;
    private TipoUtente tipoUtente;

    public Utente(int id, String nome, String cognome, String email, String passwordHash, TipoUtente tipoUtente) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.passwordHash = passwordHash;
        this.tipoUtente = tipoUtente;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public TipoUtente getTipoUtente() {
        return tipoUtente;
    }
}