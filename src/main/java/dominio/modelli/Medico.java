package dominio.modelli;

public class Medico {

    private int _id;
    private String _nome;
    private String _cognome;
    private Specializzazione _specializzazione;

    public Medico(int id, String nome, String cognome, Specializzazione specializzazione ){
        _id = id;
        _nome = nome;
        _cognome = cognome;
        _specializzazione = specializzazione;
    }

    public int getId() {
        return _id;
    }

    public String getNome() {
        return _nome;
    }

    public String getCognome() {
        return _cognome;
    }

    public Specializzazione getSpecializzazione() {
        return _specializzazione;
    }
}