package handler;

import com.sun.net.httpserver.*;
import dao.CarritoDAO;
import model.CarritoItem;
import util.ResponseUtil;
import org.json.JSONObject;
import java.io.IOException;

/**
 * Handler para operaciones del carrito
 */
public class CarritoHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS headers
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
            return;
        }

        try {
            String path = exchange.getRequestURI().getPath();

            if ("POST".equals(exchange.getRequestMethod()) && path.contains("/agregar")) {
                handleAgregarCarrito(exchange);
            } else if ("DELETE".equals(exchange.getRequestMethod()) && path.contains("/eliminar")) {
                handleEliminarCarrito(exchange);
            } else if ("GET".equals(exchange.getRequestMethod()) && path.equals("/carrito")) {
                handleObtenerCarrito(exchange);
            } else {
                sendError(exchange, 405, "Método no permitido");
            }
        } catch (Exception e) {
            System.err.println("❌ Error en CarritoHandler: " + e.getMessage());
            sendError(exchange, 500, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleObtenerCarrito(HttpExchange exchange) throws IOException {
        JSONObject response = new JSONObject();
        response.put("items", new Object[]{});
        response.put("total", 0);

        String responseStr = response.toString();
        exchange.sendResponseHeaders(200, responseStr.getBytes().length);
        exchange.getResponseBody().write(responseStr.getBytes());
        System.out.println("✓ GET /carrito");
    }

    private void handleAgregarCarrito(HttpExchange exchange) throws IOException {
        JSONObject response = new JSONObject();
        response.put("success", true);
        response.put("message", "Producto agregado al carrito");

        String responseStr = response.toString();
        exchange.sendResponseHeaders(201, responseStr.getBytes().length);
        exchange.getResponseBody().write(responseStr.getBytes());
        System.out.println("✓ POST /carrito/agregar");
    }

    private void handleEliminarCarrito(HttpExchange exchange) throws IOException {
        JSONObject response = new JSONObject();
        response.put("success", true);
        response.put("message", "Producto eliminado del carrito");

        String responseStr = response.toString();
        exchange.sendResponseHeaders(200, responseStr.getBytes().length);
        exchange.getResponseBody().write(responseStr.getBytes());
        System.out.println("✓ DELETE /carrito/eliminar");
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        JSONObject error = new JSONObject();
        error.put("success", false);
        error.put("message", message);

        String response = error.toString();
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
    }
}