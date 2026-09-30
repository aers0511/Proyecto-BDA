package com.tutiket.domain;

import java.math.BigDecimal;

public class CuentaCliente {

    private Long id;
    private Long idCliente;
    private String numeroCuenta;
    private String banco;
    private BigDecimal saldo;
    private Boolean activo;

    public CuentaCliente() {
    }

    public CuentaCliente(Long id, Long idCliente, String numeroCuenta, String banco, BigDecimal saldo, Boolean activo) {
        this.id = id;
        this.idCliente = idCliente;
        this.numeroCuenta = numeroCuenta;
        this.banco = banco;
        this.saldo = saldo;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
