package dao;

import config.DatabaseConnection;
import model.CarritoItem;
import model.DetalleOrden;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarritoDAO {

    // Obtener carrito de un usuario
    public List<CarritoItem> obtenerCarrito(int idUsuario) {

        List<CarritoItem> list = new ArrayList<>();

        String sql = """
                SELECT c.id, c.cantidad, p.nombre, p.precio, p.imagen_url, p.id as id_producto
                FROM carrito c
                JOIN productos p ON c.id_producto = p.id
                WHERE c.id_usuario = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CarritoItem item = new CarritoItem();
                item.setId(rs.getInt("id"));
                item.setCantidad(rs.getInt("cantidad"));
                item.setNombre_producto(rs.getString("nombre"));
                item.setPrecio(rs.getDouble("precio"));
                item.setImagen_url(rs.getString("imagen_url"));
                item.setId_producto(rs.getInt("id_producto"));

                list.add(item);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error obtener carrito: " + e.getMessage());
        }

        return list;
    }

    // Agregar producto al carrito
    public boolean agregar(CarritoItem item) {

        String sql = "INSERT INTO carrito (id_usuario, id_producto, cantidad) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getId_usuario());
            stmt.setInt(2, item.getId_producto());
            stmt.setInt(3, item.getCantidad());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error agregar al carrito: " + e.getMessage());
            return false;
        }
    }

    // Limpiar carrito
    public boolean limpiar(int idUsuario) {
        String sql = "DELETE FROM carrito WHERE id_usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error limpiar carrito: " + e.getMessage());
            return false;
        }
    }

    // Convertir carrito en DetalleOrden antes de checkout
    public List<DetalleOrden> convertirCarritoAOrden(int idUsuario) {

        List<DetalleOrden> detalles = new ArrayList<>();

        String sql = """
                SELECT c.cantidad, p.id, p.precio
                FROM carrito c
                JOIN productos p ON c.id_producto = p.id
                WHERE c.id_usuario = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DetalleOrden d = new DetalleOrden();
                d.setId_producto(rs.getInt("id"));
                d.setCantidad(rs.getInt("cantidad"));
                d.setPrecio_unitario(rs.getDouble("precio"));
                detalles.add(d);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error convertir carrito: " + e.getMessage());
        }

        return detalles;
    }
}
