package com.tutiket.dto;

public class CompraRequestDTO {
    private Long idCliente;
    private Long idCuentaCliente;
    private Long idBoleto;

    public CompraRequestDTO() {}

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public Long getIdCuentaCliente() { return idCuentaCliente; }
    public void setIdCuentaCliente(Long idCuentaCliente) { this.idCuentaCliente = idCuentaCliente; }
    public Long getIdBoleto() { return idBoleto; }
    public void setIdBoleto(Long idBoleto) { this.idBoleto = idBoleto; }
}