package config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/ojag_ecommerce_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * Inicializa la conexión a la base de datos (verifica driver MySQL)
     */
    public static void initialize() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ Driver MySQL cargado correctamente");
            
            // Verificar conexión
            Connection conn = getConnection();
            if (conn != null) {
                conn.close();
                System.out.println("✓ Conexión a base de datos verificada");
            } else {
                System.err.println("❌ No se pudo establecer conexión con la base de datos");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al inicializar base de datos: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("❌ Error en conexión MySQL: " + e.getMessage());
            return null;
        }
    }
}
