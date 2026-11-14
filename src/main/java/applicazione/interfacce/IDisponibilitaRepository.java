package applicazione.interfacce;

import dominio.modelli.Disponibilita;
import java.util.List;

public interface IDisponibilitaRepository {
    List<Disponibilita> ottieniPerMedico(int idMedico);
}