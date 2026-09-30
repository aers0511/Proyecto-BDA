package com.tutiket.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventoCreacionDTO {
    private Long idPromotora;
    private String nombre;
    private String categoria;
    private String clasificacion;
    private LocalDateTime fechaHora;
    private String lugar;
    private BigDecimal precioBase;
    private String imagenPath;
    private int cantidadBoletos;

    public EventoCreacionDTO() {}

    public Long getIdPromotora() { return idPromotora; }
    public void setIdPromotora(Long idPromotora) { this.idPromotora = idPromotora; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getClasificacion() { return clasificacion; }
    public void setClasificacion(String clasificacion) { this.clasificacion = clasificacion; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }
    public String getImagenPath() { return imagenPath; }
    public void setImagenPath(String imagenPath) { this.imagenPath = imagenPath; }
    public int getCantidadBoletos() { return cantidadBoletos; }
    public void setCantidadBoletos(int cantidadBoletos) { this.cantidadBoletos = cantidadBoletos; }
}