package handler;

import com.sun.net.httpserver.*;
import dao.OrdenDAO;
import model.Orden;
import util.ResponseUtil;
import org.json.JSONObject;
import java.io.IOException;

/**
 * Handler para operaciones de órdenes
 */
public class OrdenesHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
            return;
        }

        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                JSONObject response = new JSONObject();
                response.put("ordenes", new Object[]{});

                String responseStr = response.toString();
                exchange.sendResponseHeaders(200, responseStr.getBytes().length);
                exchange.getResponseBody().write(responseStr.getBytes());
                System.out.println("✓ GET /ordenes");
            }
        } catch (Exception e) {
            System.err.println("❌ Error en OrdenesHandler: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }
}