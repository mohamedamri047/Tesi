
package presentazione.http.controller;

import applicazione.casiuso.AnnullamentoPrenotazioneServizio;
import applicazione.casiuso.MiePrenotazioniServizio;
import applicazione.casiuso.PrenotazioniServizio;
import applicazione.eccezioni.AccessoNegatoException;
import applicazione.eccezioni.DatiPrenotazioneNonValidiException;
import applicazione.eccezioni.PrenotazioneNonAnnullabileException;
import applicazione.eccezioni.PrenotazioneNonTrovataException;
import applicazione.eccezioni.SessioneNonValidaException;
import applicazione.eccezioni.SlotNonDisponibileException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dominio.modelli.Prenotazione;
import dominio.modelli.PrenotazioneDettaglio;
import presentazione.http.AutorizzazioneHttp;
import presentazione.http.dto.RichiestaPrenotazione;
import presentazione.http.dto.RispostaPrenotazione;
import presentazione.http.dto.RispostaPrenotazioneDettaglio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrenotazioniController implements HttpHandler {

    private final PrenotazioniServizio prenotazioniServizio;
    private final MiePrenotazioniServizio miePrenotazioniServizio;
    private final AnnullamentoPrenotazioneServizio annullamentoServizio;
    private final ObjectMapper objectMapper;

    public PrenotazioniController(PrenotazioniServizio prenotazioniServizio, MiePrenotazioniServizio miePrenotazioniServizio, AnnullamentoPrenotazioneServizio annullamentoServizio) {
        this.prenotazioniServizio = prenotazioniServizio;
        this.miePrenotazioniServizio = miePrenotazioniServizio;
        this.annullamentoServizio = annullamentoServizio;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    @Override
    public void handle(HttpExchange scambio) throws IOException {
        String percorso = scambio.getRequestURI().getPath();
        String metodo = scambio.getRequestMethod();
        String[] parti = percorso.split("/");
        boolean rottaPrenotazioni = "/api/prenotazioni".equals(percorso);
        boolean rottaAnnullamento = parti.length == 5 && "api".equals(parti[1]) && "prenotazioni".equals(parti[2]) && "annulla".equals(parti[4]);

        scambio.getResponseHeaders().set("Cache-Control", "no-store");

        try {
            if (rottaPrenotazioni && "GET".equalsIgnoreCase(metodo)) {
                ottieniPrenotazioni(scambio);
            } else if (rottaPrenotazioni && "POST".equalsIgnoreCase(metodo)) {
                creaPrenotazione(scambio);
            } else if (rottaAnnullamento && "PATCH".equalsIgnoreCase(metodo)) {
                annullaPrenotazione(scambio, parti[3]);
            } else if (rottaPrenotazioni || rottaAnnullamento) {
                scambio.getResponseHeaders().set("Allow", rottaPrenotazioni ? "GET, POST" : "PATCH");
                inviaRisposta(scambio, 405, "{\"messaggio\":\"Metodo non consentito\"}");
            } else {
                inviaRisposta(scambio, 404, "{\"messaggio\":\"Endpoint non trovato\"}");
            }
        } catch (SessioneNonValidaException eccezione) {
            inviaRisposta(scambio, 401, "{\"messaggio\":\"Sessione assente, non valida o scaduta\"}");
        } catch (AccessoNegatoException eccezione) {
            inviaRisposta(scambio, 403, "{\"messaggio\":\"Accesso non consentito\"}");
        } catch (JsonProcessingException eccezione) {
            inviaRisposta(scambio, 400, "{\"messaggio\":\"JSON non valido\"}");
        } catch (DatiPrenotazioneNonValidiException eccezione) {
            inviaRisposta(scambio, 400, objectMapper.writeValueAsString(Collections.singletonMap("messaggio", eccezione.getMessage())));
        } catch (PrenotazioneNonTrovataException eccezione) {
            inviaRisposta(scambio, 404, "{\"messaggio\":\"Prenotazione non trovata\"}");
        } catch (PrenotazioneNonAnnullabileException eccezione) {
            inviaRisposta(scambio, 409, objectMapper.writeValueAsString(Collections.singletonMap("messaggio", eccezione.getMessage())));
        } catch (SlotNonDisponibileException eccezione) {
            inviaRisposta(scambio, 409, "{\"messaggio\":\"Disponibilità non prenotabile\"}");
        } catch (Exception eccezione) {
            eccezione.printStackTrace();
            inviaRisposta(scambio, 500, "{\"messaggio\":\"Errore interno del server\"}");
        }
    }

    private void ottieniPrenotazioni(HttpExchange scambio) throws IOException {
        String token = AutorizzazioneHttp.leggiToken(scambio);
        List<PrenotazioneDettaglio> prenotazioni = miePrenotazioniServizio.ottieniMiePrenotazioni(token);
        List<RispostaPrenotazioneDettaglio> risposta = new ArrayList<>();
        for (PrenotazioneDettaglio prenotazione : prenotazioni) {
            risposta.add(new RispostaPrenotazioneDettaglio(prenotazione));
        }
        String json = objectMapper.writeValueAsString(risposta);
        inviaRisposta(scambio, 200, json);
    }

    private void creaPrenotazione(HttpExchange scambio) throws IOException {
        String token = AutorizzazioneHttp.leggiToken(scambio);
        RichiestaPrenotazione richiesta = objectMapper.readValue(scambio.getRequestBody(), RichiestaPrenotazione.class);
        if (richiesta == null) {
            throw new DatiPrenotazioneNonValidiException("Richiesta mancante");
        }
        Prenotazione prenotazione = prenotazioniServizio.prenotaVisita(token, richiesta.getIdPrestazione(), richiesta.getIdDisponibilita());
        RispostaPrenotazione risposta = new RispostaPrenotazione(prenotazione);
        String json = objectMapper.writeValueAsString(risposta);
        inviaRisposta(scambio, 201, json);
    }

    private void annullaPrenotazione(HttpExchange scambio, String idPrenotazioneTesto) throws IOException {
        String token = AutorizzazioneHttp.leggiToken(scambio);
        int idPrenotazione;
        try {
            idPrenotazione = Integer.parseInt(idPrenotazioneTesto);
        } catch (NumberFormatException eccezione) {
            throw new DatiPrenotazioneNonValidiException("ID prenotazione non valido");
        }
        annullamentoServizio.annullaPrenotazione(token, idPrenotazione);
        scambio.sendResponseHeaders(204, -1);
        scambio.close();
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
