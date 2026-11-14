package applicazione.casiuso;

import applicazione.interfacce.IPrestazioneRepository;
import dominio.modelli.PrestazioneMedica;

import java.util.List;

public class PrestazioniServizio {
    private IPrestazioneRepository _prestazioneRepository;

    public PrestazioniServizio(IPrestazioneRepository prestazioneRepository){
        _prestazioneRepository = prestazioneRepository;
    }

    public List<PrestazioneMedica> RecuperaPrestazioni(){
        return _prestazioneRepository.recuperaPrestazioni();
    }
}