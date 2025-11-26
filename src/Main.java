import com.sun.net.httpserver.*;
import config.DatabaseConnection;
import handler.*;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Servidor HTTP principal para la API de ecommerce
 * Configura rutas para productos, usuarios, carrito y órdenes
 * Puerto: 8095
 */
public class Main {
    private static final int PORT = 8095;
    private static HttpServer server;

    public static void main(String[] args) {
        try {
            // Inicializar base de datos
            DatabaseConnection.initialize();
            System.out.println("✓ Base de datos conectada");

            // Crear servidor HTTP
            server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
            System.out.println("✓ Servidor creado en puerto " + PORT);

            // Configurar rutas
            setupRoutes();

            // Iniciar servidor
            server.setExecutor(null);
            server.start();
            System.out.println("╔═══════════════════════════════════════════════════════╗");
            System.out.println("║     🚀 API Ecommerce iniciada correctamente          ║");
            System.out.println("║     Puerto: " + PORT + "                                  ║");
            System.out.println("║     URL: http://localhost:" + PORT + "                  ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");

        } catch (IOException e) {
            System.err.println("❌ Error al iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configura todas las rutas HTTP de la API
     */
    private static void setupRoutes() {
        // Rutas de Productos
        server.createContext("/productos", new ProductoHandler());
        System.out.println("  ✓ GET /productos");

        // Rutas de Usuario (Autenticación)
        server.createContext("/usuario/registro", new UsuarioHandler());
        System.out.println("  ✓ POST /usuario/registro");

        server.createContext("/usuario/login", new UsuarioHandler());
        System.out.println("  ✓ POST /usuario/login");

        // Rutas de Carrito
        server.createContext("/carrito", new CarritoHandler());
        System.out.println("  ✓ GET /carrito");

        server.createContext("/carrito/agregar", new CarritoHandler());
        System.out.println("  ✓ POST /carrito/agregar");

        server.createContext("/carrito/eliminar", new CarritoHandler());
        System.out.println("  ✓ DELETE /carrito/eliminar/{id}");

        // Rutas de Checkout y Órdenes
        server.createContext("/checkout", new CheckoutHandler());
        System.out.println("  ✓ POST /checkout");

        server.createContext("/ordenes", new OrdenesHandler());
        System.out.println("  ✓ GET /ordenes");

        server.createContext("/orden", new OrdenesHandler());
        System.out.println("  ✓ GET /orden/{id}");

        // Ruta raíz para health check
        server.createContext("/", exchange -> {
            String response = "{\"status\": \"API activa\", \"version\": \"1.0\", \"puerto\": " + PORT + "}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });
        System.out.println("  ✓ GET / (health check)");
    }

    /**
     * Detiene el servidor
     */
    public static void stopServer() {
        if (server != null) {
            server.stop(0);
            System.out.println("✓ Servidor detenido");
        }
    }
}