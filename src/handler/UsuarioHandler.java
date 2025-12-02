package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UsuarioDAO;
import model.Usuario;
import org.json.JSONObject;
import org.json.JSONArray;
import util.JsonUtil;
import java.io.IOException;
import java.util.List;

public class UsuarioHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
            return;
        }

        String path = exchange.getRequestURI().getPath();

        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                if (path.equals("/usuarios")) {
                    handleObtenerTodos(exchange);
                }
            } else if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                JSONObject json = new JSONObject(body);
                
                String accion = json.optString("accion", "registro");
                if ("registro".equals(accion)) {
                    handleRegistro(exchange, json);
                } else if ("login".equals(accion)) {
                    handleLogin(exchange, json);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error en UsuarioHandler: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleObtenerTodos(HttpExchange exchange) throws IOException {
        try {
            List<Usuario> usuarios = UsuarioDAO.obtenerTodos();
            JSONArray arr = new JSONArray();
            
            for (Usuario u : usuarios) {
                JSONObject obj = new JSONObject();
                obj.put("id", u.getId());
                obj.put("nombre", u.getNombre());
                obj.put("email", u.getEmail());
                arr.put(obj);
            }
            
            String response = arr.toString();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            System.out.println("✓ GET /usuarios");
        } catch (Exception e) {
            sendError(exchange, 500, "Error al obtener usuarios: " + e.getMessage());
        }
    }

    private void handleRegistro(HttpExchange exchange, JSONObject json) throws IOException {
        try {
            String nombre = json.getString("nombre");
            String email = json.getString("email");
            String password = json.getString("password");
            String telefono = json.optString("telefono", "");

            Usuario usuarioExistente = UsuarioDAO.obtenerPorEmail(email);
            if (usuarioExistente != null) {
                sendError(exchange, 409, "Este email ya está registrado");
                return;
            }

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setPassword(password);
            usuario.setTelefono(telefono);

            boolean registrado = UsuarioDAO.guardar(usuario);
            if (registrado) {
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("usuario", new JSONObject()
                        .put("nombre", usuario.getNombre())
                        .put("email", usuario.getEmail())
                );
                String responseStr = response.toString();
                exchange.sendResponseHeaders(201, responseStr.getBytes().length);
                exchange.getResponseBody().write(responseStr.getBytes());
                System.out.println("✓ POST /usuarios (registro)");
            } else {
                sendError(exchange, 500, "Error al guardar usuario en base de datos");
            }
        } catch (Exception e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private void handleLogin(HttpExchange exchange, JSONObject json) throws IOException {
        try {
            String email = json.getString("email");
            String password = json.getString("password");

            Usuario usuario = UsuarioDAO.obtenerPorEmail(email);
            if (usuario == null) {
                sendError(exchange, 401, "Usuario no encontrado");
                return;
            }

            if (!usuario.getPassword().equals(password)) {
                sendError(exchange, 401, "Contraseña incorrecta");
                return;
            }

            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("usuario", new JSONObject()
                    .put("id", usuario.getId())
                    .put("nombre", usuario.getNombre())
                    .put("email", usuario.getEmail())
            );
            String responseStr = response.toString();
            exchange.sendResponseHeaders(200, responseStr.getBytes().length);
            exchange.getResponseBody().write(responseStr.getBytes());
            System.out.println("✓ POST /usuarios (login)");
        } catch (Exception e) {
            sendError(exchange, 400, e.getMessage());
        }
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