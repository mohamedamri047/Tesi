package applicazione.casiuso;

import applicazione.interfacce.IDisponibilitaRepository;
import dominio.modelli.Disponibilita;
import java.util.List;

public class DisponibilitaServizio {

    private final IDisponibilitaRepository _disponibilitaRepository;

    public DisponibilitaServizio(IDisponibilitaRepository disponibilitaRepository) {
        _disponibilitaRepository = disponibilitaRepository;
    }

    public List<Disponibilita> ottieniDisponibilitaPerMedico(int idMedico) {
        return _disponibilitaRepository.ottieniPerMedico(idMedico);
    }
}