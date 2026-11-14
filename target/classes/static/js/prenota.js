const selezionePrestazione = document.getElementById("prestazione");
const selezioneMedico = document.getElementById("medico");
const elencoDisponibilita = document.getElementById("elenco-disponibilita");
const messaggioDisponibilita = document.getElementById("messaggio-disponibilita");
const messaggioErrore = document.getElementById("messaggio-errore");
const messaggioEsito = document.getElementById("messaggio-esito");
const riepilogo = document.getElementById("riepilogo");
const pulsanteConferma = document.getElementById("pulsante-conferma");
const pulsanteEsci = document.getElementById("pulsante-esci");

let idDisponibilitaSelezionata = null;

function nascondiMessaggi() {
    messaggioErrore.hidden = true;
    messaggioEsito.hidden = true;
}

function mostraErrore(errore) {
    if (errore.status === 401) {
        sessionStorage.removeItem("token");
        window.location.replace("/");
        return;
    }
    messaggioErrore.textContent = errore.message;
    messaggioErrore.hidden = false;
}

function azzeraDisponibilita(messaggio) {
    idDisponibilitaSelezionata = null;
    elencoDisponibilita.replaceChildren();
    riepilogo.hidden = true;
    pulsanteConferma.disabled = true;
    messaggioDisponibilita.textContent = messaggio;
}

function formattaDisponibilita(slot) {
    const parti = slot.data.split("-");
    return parti[2] + "/" + parti[1] + "/" + parti[0] + " - " + slot.oraInizio;
}

function selezionaDisponibilita(pulsante, id, descrizione) {
    for (const elemento of elencoDisponibilita.children) {
        elemento.classList.remove("selezionato");
        elemento.setAttribute("aria-pressed", "false");
    }

    pulsante.classList.add("selezionato");
    pulsante.setAttribute("aria-pressed", "true");

    idDisponibilitaSelezionata = id;
    pulsanteConferma.disabled = false;

    const prestazione = selezionePrestazione.selectedOptions[0].textContent;
    const medico = selezioneMedico.selectedOptions[0].textContent;
    riepilogo.textContent = prestazione + " con " + medico + ", " + descrizione;
    riepilogo.hidden = false;
}

async function caricaPrestazioni() {
    try {
        const prestazioni = await richiestaApi("/api/prestazioni");
        selezionePrestazione.innerHTML = '<option value="">Seleziona una prestazione</option>';

        for (const prestazione of prestazioni) {
            selezionePrestazione.add(new Option(prestazione.nome, prestazione.id));
        }
        selezionePrestazione.disabled = false;
    } catch (errore) {
        mostraErrore(errore);
        selezionePrestazione.innerHTML = '<option value="">Errore nel caricamento</option>';
    }
}

async function caricaMedici() {
    nascondiMessaggi();
    selezioneMedico.innerHTML = '<option value="">Seleziona prima una prestazione</option>';
    selezioneMedico.disabled = true;
    azzeraDisponibilita("Seleziona una prestazione e un medico.");

    const idPrestazione = selezionePrestazione.value;
    if (!idPrestazione) {
        return;
    }

    selezionePrestazione.disabled = true;

    try {
        const medici = await richiestaApi("/api/prestazioni/" + idPrestazione + "/medici");

        if (medici.length === 0) {
            messaggioDisponibilita.textContent = "Nessun medico disponibile per questa prestazione.";
            return;
        }

        selezioneMedico.innerHTML = '<option value="">Seleziona un medico</option>';

        for (const medico of medici) {
            const nome = medico.nome + " " + medico.cognome;
            selezioneMedico.add(new Option(nome, medico.id));
        }

        selezioneMedico.disabled = false;
        messaggioDisponibilita.textContent = "Seleziona un medico per visualizzare gli orari.";
    } catch (errore) {
        mostraErrore(errore);
    } finally {
        selezionePrestazione.disabled = false;
    }
}

async function caricaDisponibilita() {
    azzeraDisponibilita("Seleziona un medico per visualizzare gli orari.");

    const idMedico = selezioneMedico.value;
    if (!idMedico) {
        return;
    }

    selezionePrestazione.disabled = true;
    selezioneMedico.disabled = true;
    messaggioDisponibilita.textContent = "Caricamento disponibilità...";

    try {
        const disponibilita = await richiestaApi("/api/medici/" + idMedico + "/disponibilita");

        if (disponibilita.length === 0) {
            messaggioDisponibilita.textContent = "Questo medico non ha orari disponibili.";
            return;
        }

        messaggioDisponibilita.textContent = "Seleziona uno degli orari disponibili.";

        for (const slot of disponibilita) {
            const descrizione = formattaDisponibilita(slot);
            const pulsante = document.createElement("button");

            pulsante.type = "button";
            pulsante.className = "pulsante-slot";
            pulsante.textContent = descrizione;
            pulsante.setAttribute("aria-pressed", "false");
            pulsante.addEventListener("click", () => selezionaDisponibilita(pulsante, slot.id, descrizione));

            elencoDisponibilita.appendChild(pulsante);
        }
    } catch (errore) {
        mostraErrore(errore);
        messaggioDisponibilita.textContent = "Impossibile caricare le disponibilità.";
    } finally {
        selezionePrestazione.disabled = false;
        selezioneMedico.disabled = false;
    }
}

async function confermaPrenotazione() {
    nascondiMessaggi();

    const dati = {
        idPrestazione: Number(selezionePrestazione.value),
        idDisponibilita: idDisponibilitaSelezionata
    };

    pulsanteConferma.disabled = true;
    pulsanteConferma.textContent = "Prenotazione in corso...";
    selezionePrestazione.disabled = true;
    selezioneMedico.disabled = true;

    try {
        await richiestaApi("/api/prenotazioni", "POST", dati, true);
        messaggioEsito.textContent = "Prenotazione effettuata con successo!";
        messaggioEsito.hidden = false;
        await caricaDisponibilita();
    } catch (errore) {
        mostraErrore(errore);

        if (errore.status === 409) {
            await caricaDisponibilita();
        }
    } finally {
        pulsanteConferma.textContent = "Conferma prenotazione";
        selezionePrestazione.disabled = false;
        selezioneMedico.disabled = false;
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

selezionePrestazione.addEventListener("change", caricaMedici);
selezioneMedico.addEventListener("change", function () {
    nascondiMessaggi();
    caricaDisponibilita();
});
pulsanteConferma.addEventListener("click", confermaPrenotazione);
pulsanteEsci.addEventListener("click", effettuaLogout);

if (!sessionStorage.getItem("token")) {
    window.location.replace("/");
} else {
    caricaPrestazioni();
}
