package DTOS;

import java.math.BigDecimal;

public class CuentaClienteDTO {

    private int idCuentaCliente;
    private String banco;
    private String numeroCuenta;
    private BigDecimal saldo;
    private int idCliente;

    public CuentaClienteDTO() {
    }

    // Constructor sin ID (útil para la asignación de cuentas durante el registro)
    public CuentaClienteDTO(String banco, String numeroCuenta, BigDecimal saldo, int idCliente) {
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.idCliente = idCliente;
    }

    // Constructor completo con ID (para lecturas y selección de medio de pago)
    public CuentaClienteDTO(int idCuentaCliente, String banco, String numeroCuenta, BigDecimal saldo, int idCliente) {
        this.idCuentaCliente = idCuentaCliente;
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.idCliente = idCliente;
    }

    public int getIdCuentaCliente() {
        return idCuentaCliente;
    }

    public void setIdCuentaCliente(int idCuentaCliente) {
        this.idCuentaCliente = idCuentaCliente;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    @Override
    public String toString() {
        return banco + " - " + numeroCuenta + " ($" + saldo + ")";
    }
}