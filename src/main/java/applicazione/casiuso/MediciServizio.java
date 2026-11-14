package applicazione.casiuso;

import applicazione.interfacce.IMedicoRepository;
import dominio.modelli.Medico;

import java.util.List;

public class MediciServizio {

    private final IMedicoRepository medicoRepository;

    public MediciServizio(IMedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    public List<Medico> ottieniMediciPerPrestazione(int idPrestazione) {
        return medicoRepository.ottieniPerPrestazione(idPrestazione);
    }
}