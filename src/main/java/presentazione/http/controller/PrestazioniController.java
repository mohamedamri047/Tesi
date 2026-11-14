package presentazione.http.controller;

import applicazione.casiuso.PrestazioniServizio;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dominio.modelli.PrestazioneMedica;
import presentazione.http.dto.RispostaPrestazione;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PrestazioniController implements HttpHandler {
    private final PrestazioniServizio prestazioniServizio;
    private final ObjectMapper objectMapper;

    public PrestazioniController(PrestazioniServizio prestazioniServizio) {
        this.prestazioniServizio = prestazioniServizio;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange scambio) throws IOException {

        String percorso = scambio.getRequestURI().getPath();
        String metodo = scambio.getRequestMethod();
        try {
            if ("/api/prestazioni".equals(percorso) && "GET".equalsIgnoreCase(metodo)) {
                ottieniPrestazioni(scambio);
            } else if ("/api/prestazioni".equals(percorso)) {
                scambio.getResponseHeaders().set("Allow", "GET");
                inviaRisposta(scambio, 405, "{\"messaggio\":\"Metodo non consentito\"}");
            } else {
                inviaRisposta(scambio, 404, "{\"messaggio\":\"Endpoint non trovato\"}");
            }
        } catch (Exception eccezione) {
            eccezione.printStackTrace();
            inviaRisposta(scambio, 500, "{\"messaggio\":\"Errore interno del server\"}");
        }
    }

    private void ottieniPrestazioni(HttpExchange scambio) throws IOException {
        List<PrestazioneMedica> prestazioni = prestazioniServizio.RecuperaPrestazioni();
        List<RispostaPrestazione> risposta = new ArrayList<>();
        for (PrestazioneMedica prestazione : prestazioni) {
            risposta.add(new RispostaPrestazione(prestazione));
        }
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
