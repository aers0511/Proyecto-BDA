package entidad;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OperacionCuenta {
    private int idOperacion;
    private String tipoOperacion;
    private BigDecimal monto;
    private LocalDateTime fechaHora;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoNuevo;
    private int idCuentaCliente;

    public OperacionCuenta() {}

    public OperacionCuenta(int idOperacion, String tipoOperacion, BigDecimal monto, LocalDateTime fechaHora, BigDecimal saldoAnterior, BigDecimal saldoNuevo, int idCuentaCliente) {
        this.idOperacion = idOperacion;
        this.tipoOperacion = tipoOperacion;
        this.monto = monto;
        this.fechaHora = fechaHora;
        this.saldoAnterior = saldoAnterior;
        this.saldoNuevo = saldoNuevo;
        this.idCuentaCliente = idCuentaCliente;
    }

    public int getIdOperacion() { return idOperacion; }
    public void setIdOperacion(int idOperacion) { this.idOperacion = idOperacion; }
    public String getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(String tipoOperacion) { this.tipoOperacion = tipoOperacion; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public BigDecimal getSaldoAnterior() { return saldoAnterior; }
    public void setSaldoAnterior(BigDecimal saldoAnterior) { this.saldoAnterior = saldoAnterior; }
    public BigDecimal getSaldoNuevo() { return saldoNuevo; }
    public void setSaldoNuevo(BigDecimal saldoNuevo) { this.saldoNuevo = saldoNuevo; }
    public int getIdCuentaCliente() { return idCuentaCliente; }
    public void setIdCuentaCliente(int idCuentaCliente) { this.idCuentaCliente = idCuentaCliente; }
}