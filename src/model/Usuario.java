package model;

public class Usuario {

    private int id;
    private String nombre;
    private String correo;
    private String contraseña;
    private String telefono;
    private String fecha_registro;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getFecha_registro() { return fecha_registro; }
    public void setFecha_registro(String fecha_registro) { this.fecha_registro = fecha_registro; }

    // Aliases para compatibility con handlers
    public String getEmail() { return correo; }
    public void setEmail(String email) { this.correo = email; }

    public String getPassword() { return contraseña; }
    public void setPassword(String password) { this.contraseña = password; }
}
