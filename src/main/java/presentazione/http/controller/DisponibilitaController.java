package presentazione.http.controller;

import applicazione.casiuso.DisponibilitaServizio;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dominio.modelli.Disponibilita;
import presentazione.http.dto.RispostaDisponibilita;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DisponibilitaController implements HttpHandler {

    private final DisponibilitaServizio disponibilitaServizio;
    private final ObjectMapper objectMapper;

    public DisponibilitaController(DisponibilitaServizio disponibilitaServizio) {
        this.disponibilitaServizio = disponibilitaServizio;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange scambio) throws IOException {
        String percorso = scambio.getRequestURI().getPath();
        String metodo = scambio.getRequestMethod();
        String[] parti = percorso.split("/");
        boolean rottaDisponibilita = parti.length == 5 && "api".equals(parti[1]) && "medici".equals(parti[2]) && "disponibilita".equals(parti[4]);

        try {
            if (rottaDisponibilita && "GET".equalsIgnoreCase(metodo)) {
                ottieniDisponibilita(scambio, parti[3]);
            } else if (rottaDisponibilita) {
                scambio.getResponseHeaders().set("Allow", "GET");
                inviaRisposta(scambio, 405, "{\"messaggio\":\"Metodo non consentito\"}");
            } else {
                inviaRisposta(scambio, 404, "{\"messaggio\":\"Endpoint non trovato\"}");
            }
        } catch (NumberFormatException eccezione) {
            inviaRisposta(scambio, 400, "{\"messaggio\":\"ID medico non valido\"}");
        } catch (Exception eccezione) {
            eccezione.printStackTrace();
            inviaRisposta(scambio, 500, "{\"messaggio\":\"Errore interno del server\"}");
        }
    }

    private void ottieniDisponibilita(HttpExchange scambio, String idMedicoTesto) throws IOException {
        int idMedico = Integer.parseInt(idMedicoTesto);
        List<Disponibilita> disponibilita = disponibilitaServizio.ottieniDisponibilitaPerMedico(idMedico);
        List<RispostaDisponibilita> risposta = new ArrayList<>();
        for (Disponibilita slot : disponibilita) {
            risposta.add(new RispostaDisponibilita(slot));
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
