package handler;

import com.sun.net.httpserver.*;
import dao.UsuarioDAO;
import model.Usuario;
import util.ResponseUtil;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Handler para las operaciones de usuarios (autenticación)
 * Maneja:
 * - POST /usuario/registro - Registrar nuevo usuario
 * - POST /usuario/login - Iniciar sesión
 */
public class UsuarioHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Permitir CORS
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

        String path = exchange.getRequestURI().getPath();

        try {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Leer body de la solicitud
                byte[] buffer = new byte[exchange.getRequestBody().available()];
                exchange.getRequestBody().read(buffer);
                String body = new String(buffer, StandardCharsets.UTF_8);

                System.out.println("📨 Request: " + path);
                System.out.println("   Body: " + body);

                JSONObject json = new JSONObject(body);

                if (path.contains("/usuario/registro")) {
                    handleRegistro(exchange, json);
                } else if (path.contains("/usuario/login")) {
                    handleLogin(exchange, json);
                } else {
                    sendError(exchange, 404, "Endpoint no encontrado");
                }
            } else {
                sendError(exchange, 405, "Método no permitido");
            }
        } catch (Exception e) {
            System.err.println("❌ Error en UsuarioHandler: " + e.getMessage());
            e.printStackTrace();
            sendError(exchange, 500, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    /**
     * Maneja el registro de nuevo usuario
     */
    private void handleRegistro(HttpExchange exchange, JSONObject json) throws IOException {
        try {
            String nombre = json.getString("nombre");
            String email = json.getString("email");
            String password = json.getString("password");
            String telefono = json.optString("telefono", null);

            // Validar que el email no esté registrado
            Usuario usuarioExistente = UsuarioDAO.obtenerPorEmail(email);
            if (usuarioExistente != null) {
                sendError(exchange, 409, "Este email ya está registrado");
                System.out.println("⚠️  Email duplicado: " + email);
                return;
            }

            // Crear nuevo usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setPassword(password); // El DAO debería encriptar esto
            usuario.setTelefono(telefono);

            // Guardar en base de datos
            boolean registrado = UsuarioDAO.guardar(usuario);

            if (registrado) {
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("message", "Usuario registrado exitosamente");
                response.put("usuario", new JSONObject()
                        .put("id", usuario.getId())
                        .put("nombre", usuario.getNombre())
                        .put("email", usuario.getEmail())
                );

                String responseStr = response.toString();
                exchange.sendResponseHeaders(201, responseStr.getBytes().length);
                exchange.getResponseBody().write(responseStr.getBytes());
                System.out.println("✓ Usuario registrado: " + email);
            } else {
                sendError(exchange, 500, "Error al guardar usuario en base de datos");
            }
        } catch (Exception e) {
            System.err.println("❌ Error en registro: " + e.getMessage());
            sendError(exchange, 400, e.getMessage());
        }
    }

    /**
     * Maneja el login de usuario
     */
    private void handleLogin(HttpExchange exchange, JSONObject json) throws IOException {
        try {
            String email = json.getString("email");
            String password = json.getString("password");

            // Buscar usuario en base de datos
            Usuario usuario = UsuarioDAO.obtenerPorEmail(email);

            if (usuario == null) {
                sendError(exchange, 401, "Usuario no encontrado");
                System.out.println("⚠️  Usuario no encontrado: " + email);
                return;
            }

            // Validar contraseña (debería comparar hash)
            if (!usuario.getPassword().equals(password)) {
                sendError(exchange, 401, "Contraseña incorrecta");
                System.out.println("⚠️  Contraseña incorrecta para: " + email);
                return;
            }

            // Login exitoso - generar token simulado
            String token = "jwt_token_" + System.currentTimeMillis();

            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("message", "Autenticación exitosa");
            response.put("token", token);
            response.put("usuario", new JSONObject()
                    .put("id", usuario.getId())
                    .put("nombre", usuario.getNombre())
                    .put("email", usuario.getEmail())
                    .put("telefono", usuario.getTelefono())
            );

            String responseStr = response.toString();
            exchange.sendResponseHeaders(200, responseStr.getBytes().length);
            exchange.getResponseBody().write(responseStr.getBytes());
            System.out.println("✓ Login exitoso: " + email);
        } catch (Exception e) {
            System.err.println("❌ Error en login: " + e.getMessage());
            sendError(exchange, 400, e.getMessage());
        }
    }

    /**
     * Envía respuesta de error
     */
    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        JSONObject error = new JSONObject();
        error.put("success", false);
        error.put("message", message);

        String response = error.toString();
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
    }
}