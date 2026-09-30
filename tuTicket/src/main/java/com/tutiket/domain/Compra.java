package com.tutiket.domain;

import com.tutiket.domain.enums.EstadoCompra;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Compra {
    private Long id;
    private Long idCliente;
    private Long idCuenta;
    private BigDecimal total;
    private LocalDateTime fechaCompra;
    private EstadoCompra estado;

    public Compra() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public Long getIdCuenta() { return idCuenta; }
    public void setIdCuenta(Long idCuenta) { this.idCuenta = idCuenta; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDateTime fechaCompra) { this.fechaCompra = fechaCompra; }
    public EstadoCompra getEstado() { return estado; }
    public void setEstado(EstadoCompra estado) { this.estado = estado; }
}