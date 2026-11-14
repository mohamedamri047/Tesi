package infrastruttura.persistenza.jdbc;

import applicazione.eccezioni.DatiPrenotazioneNonValidiException;
import applicazione.eccezioni.SlotNonDisponibileException;
import applicazione.interfacce.IPrenotazioneRepository;
import dominio.modelli.Prenotazione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RegistraPrenotazione implements IPrenotazioneRepository {

    @Override
    public Prenotazione prenotaVisita(int idPaziente, int idPrestazione, int idDisponibilita) {
        try (Connection connessione = ConnessioneDatabase.getConnessione()) {
            connessione.setAutoCommit(false);
            try {
                if (!pazienteValido(connessione, idPaziente)) {
                    throw new DatiPrenotazioneNonValidiException("Paziente inesistente o non valido");
                }
                if (!prestazioneCompatibile(connessione, idPrestazione, idDisponibilita)) {
                    throw new DatiPrenotazioneNonValidiException("Il medico non offre la prestazione richiesta o lo slot non esiste");
                }
                int idStato = ottieniIdStato(connessione);
                if (!occupaDisponibilita(connessione, idDisponibilita)) {
                    throw new SlotNonDisponibileException();
                }
                int idPrenotazione = inserisciPrenotazione(connessione, idPaziente, idPrestazione, idDisponibilita, idStato);
                connessione.commit();
                return new Prenotazione(idPrenotazione, idPaziente, idPrestazione, idDisponibilita, "PRENOTATA");
            } catch (SQLException | RuntimeException eccezione) {
                try {
                    connessione.rollback();
                } catch (SQLException erroreRollback) {
                    eccezione.addSuppressed(erroreRollback);
                }
                throw eccezione;
            }

        } catch (SQLException eccezione) {
            throw new RuntimeException("Errore durante la registrazione della prenotazione", eccezione);
        }
    }

    private boolean pazienteValido(Connection connessione, int idPaziente) throws SQLException {
        String query = "SELECT 1 FROM utenti u INNER JOIN tipi_utente t ON u.id_tipo_utente = t.id_tipo_utente WHERE u.id_utente = ? AND t.descrizione = 'PAZIENTE'";
        try (PreparedStatement statement = connessione.prepareStatement(query)) {
            statement.setInt(1, idPaziente);
            try (ResultSet risultato = statement.executeQuery()) {
                return risultato.next();
            }
        }
    }

    private boolean prestazioneCompatibile(Connection connessione, int idPrestazione, int idDisponibilita) throws SQLException {
        String query = "SELECT 1 FROM disponibilita d INNER JOIN medici_prestazioni mp ON d.id_medico = mp.id_medico WHERE d.id_disponibilita = ? AND mp.id_prestazione = ?";
        try (PreparedStatement statement = connessione.prepareStatement(query)) {
            statement.setInt(1, idDisponibilita);
            statement.setInt(2, idPrestazione);
            try (ResultSet risultato = statement.executeQuery()) {
                return risultato.next();
            }
        }
    }

    private int ottieniIdStato(Connection connessione) throws SQLException {
        String query = "SELECT id_stato_prenotazione FROM stati_prenotazione WHERE descrizione = 'PRENOTATA'";
        try (PreparedStatement statement = connessione.prepareStatement(query);
             ResultSet risultato = statement.executeQuery()) {
            if (!risultato.next()) {
                throw new IllegalStateException("Stato PRENOTATA non configurato nel database");
            }
            return risultato.getInt("id_stato_prenotazione");
        }
    }

    private boolean occupaDisponibilita(Connection connessione, int idDisponibilita) throws SQLException {
        String query = "UPDATE disponibilita SET disponibile = FALSE WHERE id_disponibilita = ? AND disponibile = TRUE AND TIMESTAMP(data_disponibilita, ora_inizio) > NOW()";
        try (PreparedStatement statement = connessione.prepareStatement(query)) {
            statement.setInt(1, idDisponibilita);
            return statement.executeUpdate() == 1;
        }
    }

    private int inserisciPrenotazione(Connection connessione, int idPaziente, int idPrestazione, int idDisponibilita, int idStato) throws SQLException {
        String query = "INSERT INTO prenotazioni (id_paziente, id_prestazione, id_disponibilita, id_stato_prenotazione) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connessione.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, idPaziente);
            statement.setInt(2, idPrestazione);
            statement.setInt(3, idDisponibilita);
            statement.setInt(4, idStato);
            statement.executeUpdate();
            try (ResultSet chiavi = statement.getGeneratedKeys()) {
                if (!chiavi.next()) {
                    throw new SQLException("Impossibile recuperare l'ID della prenotazione");
                }
                return chiavi.getInt(1);
            }
        }
    }
}