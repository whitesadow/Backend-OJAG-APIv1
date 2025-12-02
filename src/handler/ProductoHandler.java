package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.ProductoDAO;
import model.Producto;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ProductoHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            exchange.getResponseHeaders().set("Content-Type", "application/json");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            System.out.println("📡 Método: " + method + " | Ruta: " + path);

            switch (method) {
                case "GET":
                    if (path.matches("^/productos/\\d+$")) {
                        int id = extractId(path);
                        handleObtenerPorId(exchange, id);
                    } else if (path.equals("/productos")) {
                        handleObtenerTodos(exchange);
                    } else {
                        sendError(exchange, 404, "Ruta no encontrada");
                    }
                    break;

                case "POST":
                    if (!path.equals("/productos")) {
                        sendError(exchange, 404, "Ruta no encontrada");
                        break;
                    }
                    JSONObject bodyPost = readJsonBody(exchange);
                    if (bodyPost == null) return;
                    handleCrear(exchange, bodyPost);
                    break;

                case "PUT":
                    if (!path.matches("^/productos/\\d+$")) {
                        sendError(exchange, 404, "Ruta no encontrada");
                        break;
                    }
                    int idPut = extractId(path);
                    JSONObject bodyPut = readJsonBody(exchange);
                    if (bodyPut == null) return;
                    handleActualizar(exchange, idPut, bodyPut);
                    break;

                case "DELETE":
                    if (!path.matches("^/productos/\\d+$")) {
                        sendError(exchange, 404, "Ruta no encontrada");
                        break;
                    }
                    int idDelete = extractId(path);
                    handleEliminar(exchange, idDelete);
                    break;

                default:
                    sendError(exchange, 405, "Método no permitido");
            }

        } catch (Exception e) {
            System.err.println("❌ Error global en ProductoHandler: " + e.getMessage());
            sendError(exchange, 500, "Error interno del servidor");
        }
    }

    // ==============================
    // Funciones auxiliares
    // ==============================

    private int extractId(String path) {
        return Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
    }

    private JSONObject readJsonBody(HttpExchange exchange) throws IOException {
        // Validar Content-Type
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.contains("application/json")) {
            sendError(exchange, 400, "Content-Type debe ser application/json");
            return null;
        }

        // Leer body
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).trim();
        System.out.println("📦 Body recibido: " + body);

        if (body.isEmpty()) {
            sendError(exchange, 400, "El cuerpo JSON está vacío");
            return null;
        }

        try {
            return new JSONObject(body);
        } catch (Exception e) {
            sendError(exchange, 400, "JSON inválido: " + e.getMessage());
            return null;
        }
    }

    // ==============================
    // Endpoints (CRUD)
    // ==============================

    private void handleObtenerTodos(HttpExchange exchange) throws IOException {
        List<Producto> productos = ProductoDAO.obtenerTodos();
        JSONArray arr = new JSONArray();

        for (Producto p : productos) {
            JSONObject obj = new JSONObject()
                    .put("id", p.getId())
                    .put("nombre", p.getNombre())
                    .put("descripcion", p.getDescripcion())
                    .put("precio", p.getPrecio())
                    .put("stock", p.getStock())
                    .put("imagen_url", p.getImagen_url())
                    .put("id_categoria", p.getId_categoria());
            arr.put(obj);
        }

        sendJson(exchange, 200, arr.toString());
        System.out.println("✓ GET /productos");
    }

    private void handleObtenerPorId(HttpExchange exchange, int id) throws IOException {
        Producto p = ProductoDAO.obtenerPorId(id);

        if (p == null) {
            sendError(exchange, 404, "Producto no encontrado");
            return;
        }

        JSONObject obj = new JSONObject()
                .put("id", p.getId())
                .put("nombre", p.getNombre())
                .put("descripcion", p.getDescripcion())
                .put("precio", p.getPrecio())
                .put("stock", p.getStock())
                .put("imagen_url", p.getImagen_url())
                .put("id_categoria", p.getId_categoria());

        sendJson(exchange, 200, obj.toString());
        System.out.println("✓ GET /productos/" + id);
    }

    private void handleCrear(HttpExchange exchange, JSONObject json) throws IOException {
        try {
            Producto p = new Producto();
            p.setId(0); // ID se asigna automáticamente
            p.setNombre(json.getString("nombre"));
            p.setDescripcion(json.getString("descripcion"));
            p.setPrecio(json.getDouble("precio"));
            p.setStock(json.getInt("stock"));
            p.setImagen_url(json.optString("imagen_url", ""));
            p.setId_categoria(json.getInt("id_categoria"));

            if (ProductoDAO.crear(p)) {
                sendJson(exchange, 201, "{\"success\":true,\"message\":\"Producto creado\"}");
                System.out.println("✓ POST /productos");
            } else {
                sendError(exchange, 500, "Error al crear producto");
            }

        } catch (Exception e) {
            sendError(exchange, 400, "Datos inválidos: " + e.getMessage());
        }
    }

    private void handleActualizar(HttpExchange exchange, int id, JSONObject json) throws IOException {
        Producto p = ProductoDAO.obtenerPorId(id);

        if (p == null) {
            sendError(exchange, 404, "Producto no encontrado");
            return;
        }

        if (json.has("nombre")) p.setNombre(json.getString("nombre"));
        if (json.has("descripcion")) p.setDescripcion(json.getString("descripcion"));
        if (json.has("precio")) p.setPrecio(json.getDouble("precio"));
        if (json.has("stock")) p.setStock(json.getInt("stock"));
        if (json.has("imagen_url")) p.setImagen_url(json.getString("imagen_url"));
        if (json.has("id_categoria")) p.setId_categoria(json.getInt("id_categoria"));

        if (ProductoDAO.actualizar(p)) {
            sendJson(exchange, 200, "{\"success\":true,\"message\":\"Producto actualizado\"}");
            System.out.println("✓ PUT /productos/" + id);
        } else {
            sendError(exchange, 500, "Error al actualizar producto");
        }
    }

    private void handleEliminar(HttpExchange exchange, int id) throws IOException {
        if (ProductoDAO.eliminar(id)) {
            sendJson(exchange, 200, "{\"success\":true,\"message\":\"Producto eliminado\"}");
            System.out.println("✓ DELETE /productos/" + id);
        } else {
            sendError(exchange, 500, "Error al eliminar producto");
        }
    }

    // ==============================
    // Respuesta JSON
    // ==============================

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        JSONObject error = new JSONObject()
                .put("success", false)
                .put("message", message);

        sendJson(exchange, code, error.toString());
    }
}
