package presentazione.http.swagger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RisorseSwagger implements HttpHandler {

    private static final String PERCORSO_BASE = "META-INF/resources/webjars/swagger-ui/5.33.2/";

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String percorsoRichiesto = exchange.getRequestURI().getPath();
        String nomeFile = percorsoRichiesto.replace("/swagger-assets/", "");
        String percorsoRisorsa = PERCORSO_BASE + nomeFile;

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(percorsoRisorsa);

        if (inputStream == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        byte[] contenuto = inputStream.readAllBytes();
        String contentType = determinaContentType(nomeFile);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, contenuto.length);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(contenuto);
        }

        inputStream.close();
    }

    private String determinaContentType(String nomeFile) {

        if (nomeFile.endsWith(".css")) {
            return "text/css";
        }

        if (nomeFile.endsWith(".js")) {
            return "application/javascript";
        }

        if (nomeFile.endsWith(".png")) {
            return "image/png";
        }

        if (nomeFile.endsWith(".svg")) {
            return "image/svg+xml";
        }

        return "application/octet-stream";
    }
}