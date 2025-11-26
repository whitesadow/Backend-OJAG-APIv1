package dao;

import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Producto;

public class ProductoDAO {

    // Crear producto
    public boolean crear(Producto p) {
        String sql = "INSERT INTO productos (nombre, descripcion, precio, stock, imagen_url, id_categoria) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getNombre());
            stmt.setString(2, p.getDescripcion());
            stmt.setDouble(3, p.getPrecio());
            stmt.setInt(4, p.getStock());
            stmt.setString(5, p.getImagen_url());
            stmt.setInt(6, p.getId_categoria());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error crear producto: " + e.getMessage());
            return false;
        }
    }

    // Obtener todos
    public List<Producto> listar() {
        List<Producto> list = new ArrayList<>();
        String sql = "SELECT * FROM productos";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPrecio(rs.getDouble("precio"));
                p.setStock(rs.getInt("stock"));
                p.setImagen_url(rs.getString("imagen_url"));
                p.setId_categoria(rs.getInt("id_categoria"));

                list.add(p);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error listar productos: " + e.getMessage());
        }

        return list;
    }

    // Alias para compatibility con handlers
    public static List<Producto> obtenerTodos() {
        return new ProductoDAO().listar();
    }
}
