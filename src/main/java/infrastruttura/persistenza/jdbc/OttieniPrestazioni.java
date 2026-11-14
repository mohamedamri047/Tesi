package infrastruttura.persistenza.jdbc;

import applicazione.interfacce.IPrestazioneRepository;
import dominio.modelli.PrestazioneMedica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OttieniPrestazioni implements IPrestazioneRepository {

    @Override
    public List<PrestazioneMedica> recuperaPrestazioni() {
        List<PrestazioneMedica> prestazioni = new ArrayList<>();
        String query = "SELECT id_prestazione, nome, descrizione FROM prestazioni_mediche ORDER BY nome";
        try (Connection connessione = ConnessioneDatabase.getConnessione();
             PreparedStatement statement = connessione.prepareStatement(query);
             ResultSet risultato = statement.executeQuery()) {
            while (risultato.next()) {
                PrestazioneMedica prestazione = new PrestazioneMedica(
                        risultato.getInt("id_prestazione"),
                        risultato.getString("nome"),
                        risultato.getString("descrizione")
                );
                prestazioni.add(prestazione);
            }
            return prestazioni;
        } catch (SQLException eccezione) {
            throw new RuntimeException("Errore durante il recupero delle prestazioni", eccezione);
        }
    }
}