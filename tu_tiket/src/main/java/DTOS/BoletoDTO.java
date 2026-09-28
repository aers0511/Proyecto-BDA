package DTOS;

import java.math.BigDecimal;

public class BoletoDTO {

    private int idBoleto;
    private String codigoBoleto;
    private BigDecimal precio;
    private String estado;
    private int idEvento;

    public BoletoDTO() {
    }

    // Constructor sin ID (útil para la generación inicial de boletos de un evento)
    public BoletoDTO(String codigoBoleto, BigDecimal precio, String estado, int idEvento) {
        this.codigoBoleto = codigoBoleto;
        this.precio = precio;
        this.estado = estado;
        this.idEvento = idEvento;
    }

    // Constructor completo con ID (para lecturas y asignación en compras)
    public BoletoDTO(int idBoleto, String codigoBoleto, BigDecimal precio, String estado, int idEvento) {
        this.idBoleto = idBoleto;
        this.codigoBoleto = codigoBoleto;
        this.precio = precio;
        this.estado = estado;
        this.idEvento = idEvento;
    }

    public int getIdBoleto() {
        return idBoleto;
    }

    public void setIdBoleto(int idBoleto) {
        this.idBoleto = idBoleto;
    }

    public String getCodigoBoleto() {
        return codigoBoleto;
    }

    public void setCodigoBoleto(String codigoBoleto) {
        this.codigoBoleto = codigoBoleto;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    @Override
    public String toString() {
        return codigoBoleto + " (" + estado + ")";
    }
}