package applicazione.interfacce;

import dominio.modelli.Medico;
import java.util.List;

public interface IMedicoRepository {
    List<Medico> ottieniPerPrestazione(int idPrestazione);
}