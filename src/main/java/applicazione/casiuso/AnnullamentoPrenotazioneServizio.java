package applicazione.casiuso;

import applicazione.eccezioni.DatiPrenotazioneNonValidiException;
import applicazione.interfacce.IAnnullamentoPrenotazioneRepository;

public class AnnullamentoPrenotazioneServizio {

    private final IAnnullamentoPrenotazioneRepository annullamentoRepository;
    private final SessioniServizio sessioniServizio;

    public AnnullamentoPrenotazioneServizio(IAnnullamentoPrenotazioneRepository annullamentoRepository, SessioniServizio sessioniServizio) {
        this.annullamentoRepository = annullamentoRepository;
        this.sessioniServizio = sessioniServizio;
    }

    public void annullaPrenotazione(String token, int idPrenotazione) {
        int idPaziente = sessioniServizio.identificaPaziente(token);
        if (idPrenotazione <= 0) {
            throw new DatiPrenotazioneNonValidiException("ID prenotazione non valido");
        }
        annullamentoRepository.annullaPrenotazione(idPrenotazione, idPaziente);
    }
}