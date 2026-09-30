package com.tutiket.domain;

import java.math.BigDecimal;

public class CuentaPromotora {
    private Long id;
    private Long idPromotora;
    private String numeroCuenta;
    private String banco;
    private BigDecimal saldo;
    private Boolean activo;

    public CuentaPromotora() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdPromotora() { return idPromotora; }
    public void setIdPromotora(Long idPromotora) { this.idPromotora = idPromotora; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }
    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}