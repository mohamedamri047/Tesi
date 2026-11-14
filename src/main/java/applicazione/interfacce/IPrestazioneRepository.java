package applicazione.interfacce;

import dominio.modelli.PrestazioneMedica;
import java.util.List;

public interface IPrestazioneRepository {
    List<PrestazioneMedica> recuperaPrestazioni();
}
