package infrastruttura.persistenza.jdbc;

import applicazione.interfacce.IDisponibilitaRepository;
import dominio.modelli.Disponibilita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OttieniDisponibilita implements IDisponibilitaRepository {

    @Override
    public List<Disponibilita> ottieniPerMedico(int idMedico) {
        List<Disponibilita> disponibilita = new ArrayList<>();
        String query = "SELECT id_disponibilita, data_disponibilita, ora_inizio, disponibile " +
                "FROM disponibilita WHERE id_medico = ? AND disponibile = TRUE " +
                "AND TIMESTAMP(data_disponibilita, ora_inizio) > NOW() " +
                "ORDER BY data_disponibilita, ora_inizio";

        try (Connection connessione = ConnessioneDatabase.getConnessione();
             PreparedStatement statement = connessione.prepareStatement(query)) {
            statement.setInt(1, idMedico);
            try (ResultSet risultato = statement.executeQuery()) {
                while (risultato.next()) {
                    Disponibilita slot = new Disponibilita(
                            risultato.getInt("id_disponibilita"),
                            idMedico,
                            risultato.getDate("data_disponibilita").toLocalDate(),
                            risultato.getTime("ora_inizio").toLocalTime(),
                            risultato.getBoolean("disponibile")
                    );
                    disponibilita.add(slot);
                }
            }
            return disponibilita;
        } catch (SQLException eccezione) {
            throw new RuntimeException("Errore durante il recupero delle disponibilità", eccezione);
        }
    }
}