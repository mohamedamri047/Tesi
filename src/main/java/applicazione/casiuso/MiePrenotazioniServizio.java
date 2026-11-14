package applicazione.casiuso;

import applicazione.interfacce.IPrenotazioniPazienteRepository;
import dominio.modelli.PrenotazioneDettaglio;
import java.util.List;

public class MiePrenotazioniServizio {

    private final IPrenotazioniPazienteRepository prenotazioniRepository;
    private final SessioniServizio sessioniServizio;

    public MiePrenotazioniServizio(IPrenotazioniPazienteRepository prenotazioniRepository, SessioniServizio sessioniServizio) {
        this.prenotazioniRepository = prenotazioniRepository;
        this.sessioniServizio = sessioniServizio;
    }

    public List<PrenotazioneDettaglio> ottieniMiePrenotazioni(String token) {
        int idPaziente = sessioniServizio.identificaPaziente(token);
        return prenotazioniRepository.ottieniPerPaziente(idPaziente);
    }
}