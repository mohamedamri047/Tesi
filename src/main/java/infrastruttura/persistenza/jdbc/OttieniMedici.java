package infrastruttura.persistenza.jdbc;

import applicazione.interfacce.IMedicoRepository;
import dominio.modelli.Medico;
import dominio.modelli.Specializzazione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OttieniMedici implements IMedicoRepository {

    @Override
    public List<Medico> ottieniPerPrestazione(int idPrestazione) {
        List<Medico> medici = new ArrayList<>();
        String query = "SELECT m.id_medico, m.nome, m.cognome, s.id_specializzazione, s.descrizione AS descrizione_specializzazione " +
                "FROM medici m " +
                "INNER JOIN medici_prestazioni mp ON m.id_medico = mp.id_medico " +
                "INNER JOIN specializzazioni s ON m.id_specializzazione = s.id_specializzazione " +
                "WHERE mp.id_prestazione = ? ORDER BY m.cognome, m.nome";

        try (Connection connessione = ConnessioneDatabase.getConnessione();
             PreparedStatement statement = connessione.prepareStatement(query)) {
            statement.setInt(1, idPrestazione);
            try (ResultSet risultato = statement.executeQuery()) {
                while (risultato.next()) {
                    Specializzazione specializzazione = new Specializzazione(
                            risultato.getInt("id_specializzazione"),
                            risultato.getString("descrizione_specializzazione")
                    );
                    Medico medico = new Medico(
                            risultato.getInt("id_medico"),
                            risultato.getString("nome"),
                            risultato.getString("cognome"),
                            specializzazione
                    );
                    medici.add(medico);
                }
            }
            return medici;
        } catch (SQLException eccezione) {
            throw new RuntimeException("Errore durante il recupero dei medici", eccezione);
        }
    }
}