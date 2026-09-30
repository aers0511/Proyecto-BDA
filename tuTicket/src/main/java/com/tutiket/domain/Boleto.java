package com.tutiket.domain;

import com.tutiket.domain.enums.EstadoBoleto;
import java.math.BigDecimal;

public class Boleto {
    private Long id;
    private Long idEvento;
    private Long idCompra;
    private String folio;
    private String zona;
    private String asiento;
    private BigDecimal precio;
    private EstadoBoleto estado;

    public Boleto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdEvento() { return idEvento; }
    public void setIdEvento(Long idEvento) { this.idEvento = idEvento; }
    public Long getIdCompra() { return idCompra; }
    public void setIdCompra(Long idCompra) { this.idCompra = idCompra; }
    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public String getAsiento() { return asiento; }
    public void setAsiento(String asiento) { this.asiento = asiento; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public EstadoBoleto getEstado() { return estado; }
    public void setEstado(EstadoBoleto estado) { this.estado = estado; }
}