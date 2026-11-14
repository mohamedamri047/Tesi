package applicazione.casiuso;

import applicazione.eccezioni.CredenzialiNonValideException;
import applicazione.interfacce.IHashPassword;
import applicazione.interfacce.IUtenteRepository;
import dominio.modelli.Utente;

public class LoginServizio {

    private IUtenteRepository _utenteRepository;
    private IHashPassword _passwordHasher;

    public LoginServizio(IUtenteRepository utenteRepository, IHashPassword passwordHasher) {
        _utenteRepository = utenteRepository;
        _passwordHasher = passwordHasher;
    }

    public Utente Login(String email, String password) {
        Utente utente = _utenteRepository.cercaUtentePerEmail(email).orElseThrow(CredenzialiNonValideException::new);
        boolean corrispostaPassword = _passwordHasher.corrisponde(password,utente.getPasswordHash());
        if (!corrispostaPassword) {
            throw new CredenzialiNonValideException();
        }
        return utente;
    }
}