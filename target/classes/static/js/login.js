const moduloLogin = document.getElementById("modulo-login");
const pulsanteLogin = document.getElementById("pulsante-login");
const messaggioErrore = document.getElementById("messaggio-errore");

moduloLogin.addEventListener("submit", async function (evento) {
    evento.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    messaggioErrore.hidden = true;
    pulsanteLogin.disabled = true;
    pulsanteLogin.textContent = "Accesso in corso...";

    try {
        const risposta = await richiestaApi("/api/login", "POST", {
            email: email,
            password: password
        });

        sessionStorage.setItem("token", risposta.token);
        window.location.href = "/prenota.html";

    } catch (errore) {
        messaggioErrore.textContent = errore.message;
        messaggioErrore.hidden = false;

    } finally {
        pulsanteLogin.disabled = false;
        pulsanteLogin.textContent = "Accedi";
    }
});
