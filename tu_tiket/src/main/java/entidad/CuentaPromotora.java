package entidad;

import java.math.BigDecimal;

public class CuentaPromotora {
    private int idCuenta;
    private String banco;
    private String numeroCuenta;
    private BigDecimal saldo;
    private int idPromotora;

    public CuentaPromotora() {}

    public CuentaPromotora(int idCuenta, String banco, String numeroCuenta, BigDecimal saldo, int idPromotora) {
        this.idCuenta = idCuenta;
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.idPromotora = idPromotora;
    }

    public int getIdCuenta() { return idCuenta; }
    public void setIdCuenta(int idCuenta) { this.idCuenta = idCuenta; }
    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public int getIdPromotora() { return idPromotora; }
    public void setIdPromotora(int idPromotora) { this.idPromotora = idPromotora; }
}