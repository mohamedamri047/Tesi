const elencoPrenotazioni = document.getElementById("elenco-prenotazioni");
const messaggioCaricamento = document.getElementById("messaggio-caricamento");
const messaggioErrore = document.getElementById("messaggio-errore");
const messaggioEsito = document.getElementById("messaggio-esito");
const pulsanteEsci = document.getElementById("pulsante-esci");

function mostraErrore(errore) {
    if (errore.status === 401) {
        sessionStorage.removeItem("token");
        window.location.replace("/");
        return;
    }
    messaggioErrore.textContent = errore.message;
    messaggioErrore.hidden = false;
}

function formattaData(data) {
    const parti = data.split("-");
    return parti[2] + "/" + parti[1] + "/" + parti[0];
}

function prenotazioneAnnullabile(prenotazione) {
    const dataVisita = new Date(prenotazione.data + "T" + prenotazione.oraInizio);
    return prenotazione.stato === "PRENOTATA" && dataVisita > new Date();
}

function creaSchedaPrenotazione(prenotazione) {
    const scheda = document.createElement("article");
    scheda.className = "scheda-prenotazione";

    const intestazione = document.createElement("div");
    intestazione.className = "intestazione-prenotazione";

    const titolo = document.createElement("h3");
    titolo.textContent = prenotazione.prestazione;

    const etichettaStato = document.createElement("span");
    etichettaStato.className = "stato-prenotazione";
    etichettaStato.textContent = prenotazione.stato;

    if (prenotazione.stato === "PRENOTATA") {
        etichettaStato.classList.add("stato-prenotata");
    } else {
        etichettaStato.classList.add("stato-annullata");
    }

    intestazione.append(titolo, etichettaStato);

    const dettagli = document.createElement("div");
    dettagli.className = "dettagli-prenotazione";

    const testoMedico = document.createElement("p");
    testoMedico.textContent = "Medico: " + prenotazione.medico;

    const testoData = document.createElement("p");
    testoData.textContent = "Data: " + formattaData(prenotazione.data);

    const testoOra = document.createElement("p");
    testoOra.textContent = "Ora: " + prenotazione.oraInizio;

    dettagli.append(testoMedico, testoData, testoOra);
    scheda.append(intestazione, dettagli);

    if (prenotazioneAnnullabile(prenotazione)) {
        const pulsanteAnnulla = document.createElement("button");
        pulsanteAnnulla.type = "button";
        pulsanteAnnulla.className = "pulsante-annulla";
        pulsanteAnnulla.textContent = "Annulla prenotazione";

        pulsanteAnnulla.addEventListener("click", function () {
            annullaPrenotazione(prenotazione.id, pulsanteAnnulla);
        });

        scheda.appendChild(pulsanteAnnulla);
    }

    return scheda;
}

async function caricaPrenotazioni() {
    messaggioErrore.hidden = true;
    messaggioCaricamento.hidden = false;
    messaggioCaricamento.textContent = "Caricamento prenotazioni...";
    elencoPrenotazioni.replaceChildren();

    try {
        const prenotazioni = await richiestaApi("/api/prenotazioni", "GET", null, true);
        messaggioCaricamento.hidden = true;

        if (prenotazioni.length === 0) {
            const messaggioVuoto = document.createElement("p");
            messaggioVuoto.className = "messaggio-vuoto";
            messaggioVuoto.textContent = "Non hai ancora effettuato nessuna prenotazione.";
            elencoPrenotazioni.appendChild(messaggioVuoto);
            return;
        }

        for (const prenotazione of prenotazioni) {
            elencoPrenotazioni.appendChild(creaSchedaPrenotazione(prenotazione));
        }
    } catch (errore) {
        messaggioCaricamento.hidden = true;
        mostraErrore(errore);
    }
}

async function annullaPrenotazione(idPrenotazione, pulsante) {
    const conferma = window.confirm("Vuoi davvero annullare questa prenotazione?");
    if (!conferma) {
        return;
    }

    messaggioErrore.hidden = true;
    messaggioEsito.hidden = true;
    pulsante.disabled = true;
    pulsante.textContent = "Annullamento in corso...";

    try {
        await richiestaApi("/api/prenotazioni/" + idPrenotazione + "/annulla", "PATCH", null, true);
        await caricaPrenotazioni();

        messaggioEsito.textContent = "Prenotazione annullata con successo.";
        messaggioEsito.hidden = false;
    } catch (errore) {
        mostraErrore(errore);
        pulsante.disabled = false;
        pulsante.textContent = "Annulla prenotazione";
    }
}

async function effettuaLogout() {
    pulsanteEsci.disabled = true;

    try {
        await richiestaApi("/api/logout", "POST", null, true);
        sessionStorage.removeItem("token");
        window.location.replace("/");
    } catch (errore) {
        mostraErrore(errore);
    } finally {
        pulsanteEsci.disabled = false;
    }
}

pulsanteEsci.addEventListener("click", effettuaLogout);

if (!sessionStorage.getItem("token")) {
    window.location.replace("/");
} else {
    caricaPrenotazioni();
}
