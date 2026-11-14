package infrastruttura.sicurezza;

import applicazione.interfacce.IGestioneSessioni;
import dominio.modelli.UtenteAutenticato;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class GestioneSessioniMemoria implements IGestioneSessioni {

    private static final Duration DURATA_SESSIONE = Duration.ofMinutes(30);
    private final Map<String, Sessione> sessioni = new ConcurrentHashMap<>();
    private final SecureRandom generatoreCasuale = new SecureRandom();

    private static class Sessione {
        private final UtenteAutenticato utente;
        private final Instant scadenza;
        private Sessione(UtenteAutenticato utente, Instant scadenza) {
            this.utente = utente;
            this.scadenza = scadenza;
        }
    }

    @Override
    public String creaSessione(int idUtente, String ruolo) {
        eliminaSessioniScadute();
        UtenteAutenticato utente = new UtenteAutenticato(idUtente, ruolo);
        Sessione sessione = new Sessione(utente, Instant.now().plus(DURATA_SESSIONE));
        while (true) {
            byte[] bytesCasuali = new byte[32];
            generatoreCasuale.nextBytes(bytesCasuali);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytesCasuali);
            if (sessioni.putIfAbsent(token, sessione) == null) {
                return token;
            }
        }
    }

    @Override
    public Optional<UtenteAutenticato> recuperaUtente(String token) {
        if (token == null || token.isEmpty()) {
            return Optional.empty();
        }
        Sessione sessione = sessioni.get(token);
        if (sessione == null) {
            return Optional.empty();
        }
        if (!Instant.now().isBefore(sessione.scadenza)) {
            sessioni.remove(token, sessione);
            return Optional.empty();
        }
        return Optional.of(sessione.utente);
    }

    @Override
    public boolean revocaSessione(String token) {
        return token != null && sessioni.remove(token) != null;
    }

    private void eliminaSessioniScadute() {
        Instant adesso = Instant.now();
        sessioni.entrySet().removeIf(voce -> !adesso.isBefore(voce.getValue().scadenza));
    }
}