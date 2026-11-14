package presentazione.http.controller;

import applicazione.casiuso.LoginServizio;
import applicazione.casiuso.SessioniServizio;
import applicazione.eccezioni.CredenzialiNonValideException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dominio.modelli.Utente;
import presentazione.http.dto.RichiestaLogin;
import presentazione.http.dto.RispostaLogin;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LoginController implements HttpHandler {

    private final LoginServizio loginServizio;
    private final SessioniServizio sessioniServizio;
    private final ObjectMapper objectMapper;

    public LoginController(LoginServizio loginServizio, SessioniServizio sessioniServizio) {
        this.loginServizio = loginServizio;
        this.sessioniServizio = sessioniServizio;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange scambio) throws IOException {
        String percorso = scambio.getRequestURI().getPath();
        String metodo = scambio.getRequestMethod();
        scambio.getResponseHeaders().set("Cache-Control", "no-store");

        try {
            if ("/api/login".equals(percorso) && "POST".equalsIgnoreCase(metodo)) {
                effettuaLogin(scambio);
            } else if ("/api/login".equals(percorso)) {
                scambio.getResponseHeaders().set("Allow", "POST");
                inviaRisposta(scambio, 405, "{\"messaggio\":\"Metodo non consentito\"}");
            } else {
                inviaRisposta(scambio, 404, "{\"messaggio\":\"Endpoint non trovato\"}");
            }
        } catch (CredenzialiNonValideException eccezione) {
            inviaRisposta(scambio, 401, "{\"messaggio\":\"Email o password non corretti\"}");
        } catch (JsonProcessingException eccezione) {
            inviaRisposta(scambio, 400, "{\"messaggio\":\"JSON non valido\"}");
        } catch (Exception eccezione) {
            eccezione.printStackTrace();
            inviaRisposta(scambio, 500, "{\"messaggio\":\"Errore interno del server\"}");
        }
    }

    private void effettuaLogin(HttpExchange scambio) throws IOException {
        RichiestaLogin richiesta = objectMapper.readValue(scambio.getRequestBody(), RichiestaLogin.class);
        if (richiesta == null || richiesta.getEmail() == null || richiesta.getPassword() == null || richiesta.getEmail().trim().isEmpty() || richiesta.getPassword().isEmpty()) {
            inviaRisposta(scambio, 400, "{\"messaggio\":\"Email e password obbligatorie\"}");
            return;
        }
        Utente utente = loginServizio.Login(richiesta.getEmail(), richiesta.getPassword());
        String token = sessioniServizio.creaSessione(utente);
        RispostaLogin risposta = new RispostaLogin(utente, token);
        String json = objectMapper.writeValueAsString(risposta);
        inviaRisposta(scambio, 200, json);
    }

    private void inviaRisposta(HttpExchange scambio, int codiceStato, String corpo) throws IOException {
        byte[] risposta = corpo.getBytes(StandardCharsets.UTF_8);
        scambio.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        scambio.sendResponseHeaders(codiceStato, risposta.length);
        try (OutputStream outputStream = scambio.getResponseBody()) {
            outputStream.write(risposta);
        }
    }
}
