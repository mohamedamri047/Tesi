package infrastruttura.persistenza.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnessioneDatabase {

    private static final String URL = "jdbc:mariadb://localhost:3306/TesiAmri";
    private static final String UTENTE = "root";
    private static final String PASSWORD = "";

    public static Connection getConnessione() throws SQLException {
        return DriverManager.getConnection(URL, UTENTE, PASSWORD);
    }
}