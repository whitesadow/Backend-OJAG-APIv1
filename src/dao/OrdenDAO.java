package dao;

import config.DatabaseConnection;
import model.DetalleOrden;
import model.Orden;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdenDAO {

    // Crear nueva orden
    public boolean crearOrden(Orden orden, List<DetalleOrden> detalles) {

        String sqlOrden =
                "INSERT INTO ordenes (id_usuario, total, estado) VALUES (?, ?, ?)";

        String sqlDetalle =
                "INSERT INTO detalles_orden (id_orden, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            // Insertar orden
            PreparedStatement stmtOrden = conn.prepareStatement(sqlOrden, Statement.RETURN_GENERATED_KEYS);
            stmtOrden.setInt(1, orden.getId_usuario());
            stmtOrden.setDouble(2, orden.getTotal());
            stmtOrden.setString(3, "pendiente");

            stmtOrden.executeUpdate();

            ResultSet keys = stmtOrden.getGeneratedKeys();
            int idOrden = 0;
            if (keys.next()) idOrden = keys.getInt(1);

            // Insertar detalles
            PreparedStatement stmtDetalle = conn.prepareStatement(sqlDetalle);

            for (DetalleOrden d : detalles) {
                stmtDetalle.setInt(1, idOrden);
                stmtDetalle.setInt(2, d.getId_producto());
                stmtDetalle.setInt(3, d.getCantidad());
                stmtDetalle.setDouble(4, d.getPrecio_unitario());
                stmtDetalle.addBatch();
            }

            stmtDetalle.executeBatch();

            conn.commit();
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error crear orden: " + e.getMessage());
            return false;
        }
    }

    // Obtener ordenes por usuario
    public List<Orden> obtenerOrdenesUsuario(int idUsuario) {

        List<Orden> list = new ArrayList<>();
        String sql = "SELECT * FROM ordenes WHERE id_usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Orden o = new Orden();
                o.setId(rs.getInt("id"));
                o.setId_usuario(rs.getInt("id_usuario"));
                o.setTotal(rs.getDouble("total"));
                o.setEstado(rs.getString("estado"));
                o.setFecha(rs.getTimestamp("fecha_orden").toString());

                list.add(o);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error obtener órdenes: " + e.getMessage());
        }

        return list;
    }
}
