package DTOS;

public class AdministradorDTO {

    private int idAdministrador;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String usuario;
    private String contrasena;
    private int idPromotora;

    public AdministradorDTO() {
    }

    // Constructor sin ID (para registro de administradores)
    public AdministradorDTO(String nombre, String apellidoPaterno, String apellidoMaterno, 
                            String usuario, String contrasena, int idPromotora) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.idPromotora = idPromotora;
    }

    // Constructor completo con ID (para lecturas y sesiones de usuario)
    public AdministradorDTO(int idAdministrador, String nombre, String apellidoPaterno, 
                            String apellidoMaterno, String usuario, String contrasena, int idPromotora) {
        this.idAdministrador = idAdministrador;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.idPromotora = idPromotora;
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getIdPromotora() {
        return idPromotora;
    }

    public void setIdPromotora(int idPromotora) {
        this.idPromotora = idPromotora;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellidoPaterno + " " + apellidoMaterno;
    }
}