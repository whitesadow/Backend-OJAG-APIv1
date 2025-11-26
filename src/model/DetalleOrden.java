package model;

public class DetalleOrden {

    private int id;
    private int id_orden;
    private int id_producto;
    private int cantidad;
    private double precio_unitario;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getId_orden() { return id_orden; }
    public void setId_orden(int id_orden) { this.id_orden = id_orden; }

    public int getId_producto() { return id_producto; }
    public void setId_producto(int id_producto) { this.id_producto = id_producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecio_unitario() { return precio_unitario; }
    public void setPrecio_unitario(double precio_unitario) { this.precio_unitario = precio_unitario; }
}
