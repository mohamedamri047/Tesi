
package presentazione.http.controller;

import applicazione.casiuso.SessioniServizio;
import applicazione.eccezioni.SessioneNonValidaException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import presentazione.http.AutorizzazioneHttp;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LogoutController implements HttpHandler {

    private final SessioniServizio sessioniServizio;

    public LogoutController(SessioniServizio sessioniServizio) {
        this.sessioniServizio = sessioniServizio;
    }

    @Override
    public void handle(HttpExchange scambio) throws IOException {
        String percorso = scambio.getRequestURI().getPath();
        String metodo = scambio.getRequestMethod();
        try {
            if ("/api/logout".equals(percorso) && "POST".equalsIgnoreCase(metodo)) {
                effettuaLogout(scambio);
            } else if ("/api/logout".equals(percorso)) {
                scambio.getResponseHeaders().set("Allow", "POST");
                inviaErrore(scambio, 405, "Metodo non consentito");
            } else {
                inviaErrore(scambio, 404, "Endpoint non trovato");
            }
        } catch (SessioneNonValidaException eccezione) {
            inviaErrore(scambio, 401, "Sessione assente, non valida o scaduta");
        } catch (Exception eccezione) {
            eccezione.printStackTrace();
            inviaErrore(scambio, 500, "Errore interno del server");
        }
    }

    private void effettuaLogout(HttpExchange scambio) throws IOException {
        String token = AutorizzazioneHttp.leggiToken(scambio);
        sessioniServizio.terminaSessione(token);
        scambio.sendResponseHeaders(204, -1);
        scambio.close();
    }

    private void inviaErrore(HttpExchange scambio, int codiceStato, String messaggio) throws IOException {
        String json = "{\"messaggio\":\"" + messaggio + "\"}";
        byte[] risposta = json.getBytes(StandardCharsets.UTF_8);
        scambio.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        scambio.sendResponseHeaders(codiceStato, risposta.length);
        try (OutputStream outputStream = scambio.getResponseBody()) {
            outputStream.write(risposta);
        }
    }
}
