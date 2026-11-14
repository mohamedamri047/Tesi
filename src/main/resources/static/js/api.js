async function richiestaApi(percorso, metodo = "GET", dati = null, autenticato = false) {
    const intestazioni = {};
    let risposta;

    if (dati !== null) {
        intestazioni["Content-Type"] = "application/json";
    }

    if (autenticato) {
        intestazioni["Authorization"] = "Bearer " + sessionStorage.getItem("token");
    }

    try {
        risposta = await fetch(percorso, {
            method: metodo,
            headers: intestazioni,
            body: dati === null ? undefined : JSON.stringify(dati)
        });
    } catch {
        throw new Error("Impossibile contattare il server");
    }

    const contenuto = risposta.status === 204 ? null : await risposta.json();

    if (!risposta.ok) {
        const errore = new Error(contenuto.messaggio);
        errore.status = risposta.status;
        throw errore;
    }

    return contenuto;
}
