package presentazione.http.swagger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SpecificaOpenApi implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        InputStream inputStream =
                getClass().getClassLoader().getResourceAsStream("openapi.yaml");

        if (inputStream == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        byte[] contenuto = inputStream.readAllBytes();

        exchange.getResponseHeaders().set("Content-Type", "application/yaml; charset=UTF-8");
        exchange.sendResponseHeaders(200, contenuto.length);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(contenuto);
        }

        inputStream.close();
    }
}