package applicazione.interfacce;

import dominio.modelli.Prenotazione;

public interface IPrenotazioneRepository {
    Prenotazione prenotaVisita(int idPaziente, int idPrestazione, int idDisponibilita);
}