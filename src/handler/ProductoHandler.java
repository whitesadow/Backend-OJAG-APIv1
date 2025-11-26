package handler;

import com.sun.net.httpserver.*;
import dao.ProductoDAO;
import model.Producto;
import util.JsonUtil;
import java.io.IOException;
import java.util.List;

/**
 * Handler para las operaciones de productos
 * Maneja GET /productos para obtener lista de productos
 */
public class ProductoHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Permitir solicitudes desde el frontend
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        // Manejar preflight CORS
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
            return;
        }

        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                // Obtener todos los productos
                List<Producto> productos = ProductoDAO.obtenerTodos();
                String response = JsonUtil.toJson(productos);

                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                System.out.println("✓ GET /productos - " + productos.size() + " productos retornados");

            } else {
                // Método no permitido
                String response = "{\"error\": \"Método no permitido\"}";
                exchange.sendResponseHeaders(405, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
            }
        } catch (Exception e) {
            String response = "{\"error\": \"" + e.getMessage() + "\"}";
            exchange.sendResponseHeaders(500, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            System.err.println("❌ Error en ProductoHandler: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }
}