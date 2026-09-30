package com.tutiket.domain;

import com.tutiket.domain.enums.TipoOperacion;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OperacionCuenta {
    private Long id;
    private Long idCuentaCliente;
    private Long idCuentaPromotora;
    private TipoOperacion tipoOperacion;
    private BigDecimal monto;
    private LocalDateTime fechaOperacion;
    private String descripcion;

    public OperacionCuenta() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdCuentaCliente() { return idCuentaCliente; }
    public void setIdCuentaCliente(Long idCuentaCliente) { this.idCuentaCliente = idCuentaCliente; }
    public Long getIdCuentaPromotora() { return idCuentaPromotora; }
    public void setIdCuentaPromotora(Long idCuentaPromotora) { this.idCuentaPromotora = idCuentaPromotora; }
    public TipoOperacion getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(TipoOperacion tipoOperacion) { this.tipoOperacion = tipoOperacion; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDateTime getFechaOperacion() { return fechaOperacion; }
    public void setFechaOperacion(LocalDateTime fechaOperacion) { this.fechaOperacion = fechaOperacion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}