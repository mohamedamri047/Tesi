package infrastruttura.persistenza.jdbc;

import applicazione.eccezioni.PrenotazioneNonAnnullabileException;
import applicazione.eccezioni.PrenotazioneNonTrovataException;
import applicazione.interfacce.IAnnullamentoPrenotazioneRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnnullaPrenotazione implements IAnnullamentoPrenotazioneRepository {

    @Override
    public void annullaPrenotazione(int idPrenotazione, int idPaziente) {

        try (Connection connessione = ConnessioneDatabase.getConnessione()) {
            connessione.setAutoCommit(false);
            try {
                int idStatoAnnullato = ottieniIdStatoAnnullato(connessione);
                int idDisponibilita;
                int idStatoCorrente;

                String query = "SELECT p.id_disponibilita, p.id_stato_prenotazione, sp.descrizione AS stato, " +
                        "(TIMESTAMP(d.data_disponibilita, d.ora_inizio) > NOW()) AS futura " +
                        "FROM prenotazioni p " +
                        "INNER JOIN stati_prenotazione sp ON p.id_stato_prenotazione = sp.id_stato_prenotazione " +
                        "INNER JOIN disponibilita d ON p.id_disponibilita = d.id_disponibilita " +
                        "WHERE p.id_prenotazione = ? AND p.id_paziente = ? FOR UPDATE";

                try (PreparedStatement statement = connessione.prepareStatement(query)) {
                    statement.setInt(1, idPrenotazione);
                    statement.setInt(2, idPaziente);
                    try (ResultSet risultato = statement.executeQuery()) {
                        if (!risultato.next()) {
                            throw new PrenotazioneNonTrovataException();
                        }
                        if (!"PRENOTATA".equals(risultato.getString("stato"))) {
                            throw new PrenotazioneNonAnnullabileException("La prenotazione non è attiva");
                        }
                        if (!risultato.getBoolean("futura")) {
                            throw new PrenotazioneNonAnnullabileException("Non è possibile annullare una visita già iniziata");
                        }
                        idDisponibilita = risultato.getInt("id_disponibilita");
                        idStatoCorrente = risultato.getInt("id_stato_prenotazione");
                    }
                }
                aggiornaPrenotazione(connessione, idPrenotazione, idPaziente, idStatoCorrente, idStatoAnnullato);
                liberaDisponibilita(connessione, idDisponibilita);
                connessione.commit();

            } catch (SQLException | RuntimeException eccezione) {
                try {
                    connessione.rollback();
                } catch (SQLException erroreRollback) {
                    eccezione.addSuppressed(erroreRollback);
                }
                throw eccezione;
            }

        } catch (SQLException eccezione) {
            throw new RuntimeException("Errore durante l'annullamento della prenotazione", eccezione);
        }
    }

    private int ottieniIdStatoAnnullato(Connection connessione) throws SQLException {
        String query = "SELECT id_stato_prenotazione FROM stati_prenotazione WHERE descrizione = 'ANNULLATA'";
        try (PreparedStatement statement = connessione.prepareStatement(query);
             ResultSet risultato = statement.executeQuery()) {
            if (!risultato.next()) {
                throw new IllegalStateException("Stato ANNULLATA non configurato nel database");
            }
            return risultato.getInt("id_stato_prenotazione");
        }
    }

    private void aggiornaPrenotazione(Connection connessione, int idPrenotazione, int idPaziente, int idStatoCorrente, int idStatoAnnullato) throws SQLException {
        String query = "UPDATE prenotazioni SET id_stato_prenotazione = ?, data_annullamento = CURRENT_TIMESTAMP WHERE id_prenotazione = ? AND id_paziente = ? AND id_stato_prenotazione = ?";
        try (PreparedStatement statement = connessione.prepareStatement(query)) {
            statement.setInt(1, idStatoAnnullato);
            statement.setInt(2, idPrenotazione);
            statement.setInt(3, idPaziente);
            statement.setInt(4, idStatoCorrente);

            if (statement.executeUpdate() != 1) {
                throw new IllegalStateException("Impossibile aggiornare lo stato della prenotazione");
            }
        }
    }

    private void liberaDisponibilita(Connection connessione, int idDisponibilita) throws SQLException {

        String query = "UPDATE disponibilita SET disponibile = TRUE WHERE id_disponibilita = ? AND disponibile = FALSE";
        try (PreparedStatement statement = connessione.prepareStatement(query)) {
            statement.setInt(1, idDisponibilita);

            if (statement.executeUpdate() != 1) {
                throw new IllegalStateException("Impossibile liberare la disponibilità");
            }
        }
    }
}