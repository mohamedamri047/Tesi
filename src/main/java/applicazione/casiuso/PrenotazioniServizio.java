package applicazione.casiuso;

import applicazione.eccezioni.DatiPrenotazioneNonValidiException;
import applicazione.interfacce.IPrenotazioneRepository;
import dominio.modelli.Prenotazione;

public class PrenotazioniServizio {

    private final IPrenotazioneRepository prenotazioneRepository;
    private final SessioniServizio sessioniServizio;

    public PrenotazioniServizio(IPrenotazioneRepository prenotazioneRepository, SessioniServizio sessioniServizio) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.sessioniServizio = sessioniServizio;
    }

    public Prenotazione prenotaVisita(String token, int idPrestazione, int idDisponibilita) {
        int idPaziente = sessioniServizio.identificaPaziente(token);
        if (idPrestazione <= 0 || idDisponibilita <= 0)
            throw new DatiPrenotazioneNonValidiException("Gli identificativi devono essere maggiori di zero");
        return prenotazioneRepository.prenotaVisita(idPaziente, idPrestazione, idDisponibilita);
    }
}