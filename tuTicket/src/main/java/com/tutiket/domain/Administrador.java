package com.tutiket.domain;

import java.time.LocalDateTime;

public class Administrador {
    private Long id;
    private String nombre;
    private String correo;
    private String usuario;
    private String contrasena;
    private LocalDateTime fechaRegistro;
    private Long idPromotora;

    public Administrador() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public Long getIdPromotora() {return idPromotora;}
    public void setIdPromotora(Long idPromotora) {this.idPromotora = idPromotora;}
    
}   