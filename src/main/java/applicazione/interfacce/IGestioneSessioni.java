package applicazione.interfacce;

import dominio.modelli.UtenteAutenticato;
import java.util.Optional;

public interface IGestioneSessioni {
    String creaSessione(int idUtente, String ruolo);
    Optional<UtenteAutenticato> recuperaUtente(String token);
    boolean revocaSessione(String token);
}