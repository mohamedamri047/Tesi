package applicazione.interfacce;

import dominio.modelli.Utente;
import java.util.Optional;

public interface IUtenteRepository {
    Optional<Utente> cercaUtentePerEmail(String email);
}