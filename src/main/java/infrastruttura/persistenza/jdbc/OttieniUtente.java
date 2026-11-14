package infrastruttura.persistenza.jdbc;

import dominio.modelli.Utente;
import dominio.modelli.TipoUtente;
import applicazione.interfacce.IUtenteRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class OttieniUtente implements IUtenteRepository {

    @Override
    public Optional<Utente> cercaUtentePerEmail(String email) {
        String query = "SELECT u.id_utente, u.nome, u.cognome, u.email, u.hash_password, " +
                "t.id_tipo_utente, t.descrizione AS tipo_utente " +
                "FROM utenti u INNER JOIN tipi_utente t ON u.id_tipo_utente = t.id_tipo_utente " +
                "WHERE u.email = ?";

        try (
                Connection connessione = ConnessioneDatabase.getConnessione();
                PreparedStatement statement = connessione.prepareStatement(query)
        ) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                TipoUtente tipoUtente = new TipoUtente(
                        resultSet.getInt("id_tipo_utente"),
                        resultSet.getString("tipo_utente")
                );
                Utente utente = new Utente(
                        resultSet.getInt("id_utente"),
                        resultSet.getString("nome"),
                        resultSet.getString("cognome"),
                        resultSet.getString("email"),
                        resultSet.getString("hash_password"),
                        tipoUtente
                );
                return Optional.of(utente);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Errore durante la ricerca dell'utente", exception);
        }
    }
}