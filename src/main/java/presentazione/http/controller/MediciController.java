package presentazione.http.controller;

import applicazione.casiuso.MediciServizio;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dominio.modelli.Medico;
import presentazione.http.dto.RispostaMedico;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MediciController implements HttpHandler {
    private final MediciServizio mediciServizio;
    private final ObjectMapper objectMapper;

    public MediciController(MediciServizio mediciServizio) {
        this.mediciServizio = mediciServizio;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange scambio) throws IOException {
        String percorso = scambio.getRequestURI().getPath();
        String metodo = scambio.getRequestMethod();
        String[] parti = percorso.split("/");
        boolean rottaMedici = parti.length == 5 && "api".equals(parti[1]) && "prestazioni".equals(parti[2]) && "medici".equals(parti[4]);

        try {
            if (rottaMedici && "GET".equalsIgnoreCase(metodo)) {
                ottieniMedici(scambio, parti[3]);
            } else if (rottaMedici) {
                scambio.getResponseHeaders().set("Allow", "GET");
                inviaRisposta(scambio, 405, "{\"messaggio\":\"Metodo non consentito\"}");
            } else {
                inviaRisposta(scambio, 404, "{\"messaggio\":\"Endpoint non trovato\"}");
            }
        } catch (NumberFormatException eccezione) {
            inviaRisposta(scambio, 400, "{\"messaggio\":\"ID prestazione non valido\"}");
        } catch (Exception eccezione) {
            eccezione.printStackTrace();
            inviaRisposta(scambio, 500, "{\"messaggio\":\"Errore interno del server\"}");
        }
    }

    private void ottieniMedici(HttpExchange scambio, String idPrestazioneTesto) throws IOException {
        int idPrestazione = Integer.parseInt(idPrestazioneTesto);
        if (idPrestazione <= 0) {
            throw new NumberFormatException();
        }
        List<Medico> medici = mediciServizio.ottieniMediciPerPrestazione(idPrestazione);
        List<RispostaMedico> risposta = new ArrayList<>();
        for (Medico medico : medici) {
            risposta.add(new RispostaMedico(medico));
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
