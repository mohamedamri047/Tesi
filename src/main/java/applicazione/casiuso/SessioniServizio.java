package applicazione.casiuso;

import applicazione.eccezioni.AccessoNegatoException;
import applicazione.eccezioni.SessioneNonValidaException;
import applicazione.interfacce.IGestioneSessioni;
import dominio.modelli.Utente;
import dominio.modelli.UtenteAutenticato;

public class SessioniServizio {

    private final IGestioneSessioni gestioneSessioni;

    public SessioniServizio(IGestioneSessioni gestioneSessioni) {
        this.gestioneSessioni = gestioneSessioni;
    }

    public String creaSessione(Utente utente) {
        return gestioneSessioni.creaSessione(utente.getId(), utente.getTipoUtente().getDescrizione());
    }

    public UtenteAutenticato identificaUtente(String token) {
        return gestioneSessioni.recuperaUtente(token).orElseThrow(SessioneNonValidaException::new);
    }

    public int identificaPaziente(String token) {
        UtenteAutenticato utente = identificaUtente(token);
        if (!"PAZIENTE".equals(utente.getRuolo())) {
            throw new AccessoNegatoException();
        }
        return utente.getIdUtente();
    }

    public void terminaSessione(String token) {
        if (!gestioneSessioni.revocaSessione(token)) {
            throw new SessioneNonValidaException();
        }
    }
}