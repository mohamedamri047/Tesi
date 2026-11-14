package applicazione.interfacce;

import dominio.modelli.PrenotazioneDettaglio;
import java.util.List;

public interface IPrenotazioniPazienteRepository {
    List<PrenotazioneDettaglio> ottieniPerPaziente(int idPaziente);
}