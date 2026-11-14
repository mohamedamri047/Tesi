package presentazione.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class RisorseStaticheHttp implements HttpHandler {

    private static final Set<String> RISORSE_CONSENTITE = Set.of(
            "/index.html",
            "/prenota.html",
            "/prenotazioni.html",
            "/css/stile.css",
            "/js/api.js",
            "/js/login.js",
            "/js/prenota.js",
            "/js/prenotazioni.js"
    );

    @Override
    public void handle(HttpExchange scambio) throws IOException {

        String percorso = scambio.getRequestURI().getPath();

        if ("/".equals(percorso)) {
            percorso = "/index.html";
        }

        if (!RISORSE_CONSENTITE.contains(percorso)) {
            scambio.sendResponseHeaders(404, -1);
            scambio.close();
            return;
        }

        if (!"GET".equalsIgnoreCase(scambio.getRequestMethod())) {
            scambio.getResponseHeaders().set("Allow", "GET");
            scambio.sendResponseHeaders(405, -1);
            scambio.close();
            return;
        }

        try (InputStream risorsa = getClass().getResourceAsStream("/static" + percorso)) {

            if (risorsa == null) {
                scambio.sendResponseHeaders(404, -1);
                scambio.close();
                return;
            }

            byte[] contenuto = risorsa.readAllBytes();
            String tipoContenuto;

            if (percorso.endsWith(".html")) {
                tipoContenuto = "text/html; charset=UTF-8";
            } else if (percorso.endsWith(".css")) {
                tipoContenuto = "text/css; charset=UTF-8";
            } else {
                tipoContenuto = "application/javascript; charset=UTF-8";
            }

            scambio.getResponseHeaders().set("Content-Type", tipoContenuto);
            scambio.getResponseHeaders().set("Cache-Control", "no-store");
            scambio.sendResponseHeaders(200, contenuto.length);

            try (OutputStream outputStream = scambio.getResponseBody()) {
                outputStream.write(contenuto);
            }
        }
    }
}
