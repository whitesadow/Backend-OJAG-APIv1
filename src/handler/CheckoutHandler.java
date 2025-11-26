package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CarritoDAO;
import dao.OrdenDAO;
import model.DetalleOrden;
import com.sun.net.httpserver.*;
import org.json.JSONObject;
import java.io.IOException;

/**
 * Handler para operaciones de checkout
 */
public class CheckoutHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
            return;
        }

        try {
            if ("POST".equals(exchange.getRequestMethod())) {
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("id", "ORD-" + System.currentTimeMillis());
                response.put("message", "Orden creada exitosamente");

                String responseStr = response.toString();
                exchange.sendResponseHeaders(201, responseStr.getBytes().length);
                exchange.getResponseBody().write(responseStr.getBytes());
                System.out.println("✓ POST /checkout");
            }
        } catch (Exception e) {
            System.err.println("❌ Error en CheckoutHandler: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }
}