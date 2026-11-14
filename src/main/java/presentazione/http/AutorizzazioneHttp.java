package presentazione.http;

import applicazione.eccezioni.SessioneNonValidaException;
import com.sun.net.httpserver.HttpExchange;

public class AutorizzazioneHttp {

    private AutorizzazioneHttp() {
    }

    public static String leggiToken(HttpExchange scambio) {
        String intestazione = scambio.getRequestHeaders().getFirst("Authorization");
        if (intestazione == null || !intestazione.regionMatches(true, 0, "Bearer ", 0, 7)) {
            throw new SessioneNonValidaException();
        }
        String token = intestazione.substring(7).trim();
        if (token.isEmpty() || token.contains(" ")) {
            throw new SessioneNonValidaException();
        }
        return token;
    }
}