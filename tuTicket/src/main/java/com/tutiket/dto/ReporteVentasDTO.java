package com.tutiket.dto;

import java.math.BigDecimal;

public class ReporteVentasDTO {
    private Long idEvento;
    private String nombreEvento;
    private int boletosTotales;
    private int boletosVendidos;
    private BigDecimal ingresosTotales;

    public ReporteVentasDTO() {}

    public Long getIdEvento() { return idEvento; }
    public void setIdEvento(Long idEvento) { this.idEvento = idEvento; }
    public String getNombreEvento() { return nombreEvento; }
    public void setNombreEvento(String nombreEvento) { this.nombreEvento = nombreEvento; }
    public int getBoletosTotales() { return boletosTotales; }
    public void setBoletosTotales(int boletosTotales) { this.boletosTotales = boletosTotales; }
    public int getBoletosVendidos() { return boletosVendidos; }
    public void setBoletosVendidos(int boletosVendidos) { this.boletosVendidos = boletosVendidos; }
    public BigDecimal getIngresosTotales() { return ingresosTotales; }
    public void setIngresosTotales(BigDecimal ingresosTotales) { this.ingresosTotales = ingresosTotales; }
}