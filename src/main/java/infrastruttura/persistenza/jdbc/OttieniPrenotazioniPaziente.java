package infrastruttura.persistenza.jdbc;

import applicazione.interfacce.IPrenotazioniPazienteRepository;
import dominio.modelli.PrenotazioneDettaglio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OttieniPrenotazioniPaziente implements IPrenotazioniPazienteRepository {

    @Override
    public List<PrenotazioneDettaglio> ottieniPerPaziente(int idPaziente) {
        List<PrenotazioneDettaglio> prenotazioni = new ArrayList<>();
        String query = "SELECT p.id_prenotazione, pm.nome AS prestazione, " +
                "CONCAT(m.nome, ' ', m.cognome) AS medico, " +
                "d.data_disponibilita, d.ora_inizio, sp.descrizione AS stato " +
                "FROM prenotazioni p " +
                "INNER JOIN prestazioni_mediche pm ON p.id_prestazione = pm.id_prestazione " +
                "INNER JOIN disponibilita d ON p.id_disponibilita = d.id_disponibilita " +
                "INNER JOIN medici m ON d.id_medico = m.id_medico " +
                "INNER JOIN stati_prenotazione sp ON p.id_stato_prenotazione = sp.id_stato_prenotazione " +
                "WHERE p.id_paziente = ? " +
                "ORDER BY d.data_disponibilita DESC, d.ora_inizio DESC, p.id_prenotazione DESC";

        try (Connection connessione = ConnessioneDatabase.getConnessione();
             PreparedStatement statement = connessione.prepareStatement(query)) {
            statement.setInt(1, idPaziente);
            try (ResultSet risultato = statement.executeQuery()) {
                while (risultato.next()) {
                    PrenotazioneDettaglio prenotazione = new PrenotazioneDettaglio(
                            risultato.getInt("id_prenotazione"),
                            risultato.getString("prestazione"),
                            risultato.getString("medico"),
                            risultato.getDate("data_disponibilita").toLocalDate(),
                            risultato.getTime("ora_inizio").toLocalTime(),
                            risultato.getString("stato")
                    );
                    prenotazioni.add(prenotazione);
                }
            }
            return prenotazioni;
        } catch (SQLException eccezione) {
            throw new RuntimeException("Errore durante il recupero delle prenotazioni", eccezione);
        }
    }
}