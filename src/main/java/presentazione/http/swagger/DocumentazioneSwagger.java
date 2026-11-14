package presentazione.http.swagger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DocumentazioneSwagger implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String html =
                "<!DOCTYPE html>" +
                        "<html lang=\"it\">" +
                        "<head>" +
                        "<meta charset=\"UTF-8\">" +
                        "<title>API Poliambulatorio</title>" +

                        "<link " +
                        "rel=\"stylesheet\" " +
                        "type=\"text/css\" " +
                        "href=\"/swagger-assets/swagger-ui.css\">" +

                        "</head>" +

                        "<body>" +

                        "<div id=\"swagger-ui\"></div>" +

                        "<script src=\"/swagger-assets/swagger-ui-bundle.js\"></script>" +
                        "<script src=\"/swagger-assets/swagger-ui-standalone-preset.js\"></script>" +

                        "<script>" +
                        "window.onload = function() {" +

                        "SwaggerUIBundle({" +
                        "url: '/openapi.yaml'," +
                        "dom_id: '#swagger-ui'," +
                        "deepLinking: true," +

                        "presets: [" +
                        "SwaggerUIBundle.presets.apis," +
                        "SwaggerUIStandalonePreset" +
                        "]," +

                        "layout: 'StandaloneLayout'" +

                        "});" +

                        "};" +
                        "</script>" +

                        "</body>" +
                        "</html>";

        byte[] risposta =html.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, risposta.length);

        try (OutputStream outputStream =exchange.getResponseBody()) {
            outputStream.write(risposta);
        }
    }
}