package DTOS;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CompraDTO {

    private int idCompra;
    private LocalDateTime fechaHora;
    private BigDecimal precioFinal;
    private String estado;
    private int idCliente;
    private int idCuentaCliente;
    private int idBoleto;

    public CompraDTO() {
    }

    // Constructor sin ID (útil para procesar una nueva compra)
    public CompraDTO(LocalDateTime fechaHora, BigDecimal precioFinal, String estado, 
                     int idCliente, int idCuentaCliente, int idBoleto) {
        this.fechaHora = fechaHora;
        this.precioFinal = precioFinal;
        this.estado = estado;
        this.idCliente = idCliente;
        this.idCuentaCliente = idCuentaCliente;
        this.idBoleto = idBoleto;
    }

    // Constructor completo con ID (para lecturas e historial)
    public CompraDTO(int idCompra, LocalDateTime fechaHora, BigDecimal precioFinal, 
                     String estado, int idCliente, int idCuentaCliente, int idBoleto) {
        this.idCompra = idCompra;
        this.fechaHora = fechaHora;
        this.precioFinal = precioFinal;
        this.estado = estado;
        this.idCliente = idCliente;
        this.idCuentaCliente = idCuentaCliente;
        this.idBoleto = idBoleto;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdCuentaCliente() {
        return idCuentaCliente;
    }

    public void setIdCuentaCliente(int idCuentaCliente) {
        this.idCuentaCliente = idCuentaCliente;
    }

    public int getIdBoleto() {
        return idBoleto;
    }

    public void setIdBoleto(int idBoleto) {
        this.idBoleto = idBoleto;
    }

    @Override
    public String toString() {
        return "Compra #" + idCompra + " - " + estado + " ($" + precioFinal + ")";
    }
}