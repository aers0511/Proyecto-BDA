package entidad;

import java.math.BigDecimal;

public class Boleto {
    private int idBoleto;
    private String codigoBoleto;
    private BigDecimal precio;
    private String estado;
    private int idEvento;

    public Boleto() {}

    public Boleto(int idBoleto, String codigoBoleto, BigDecimal precio, String estado, int idEvento) {
        this.idBoleto = idBoleto;
        this.codigoBoleto = codigoBoleto;
        this.precio = precio;
        this.estado = estado;
        this.idEvento = idEvento;
    }

    public int getIdBoleto() { return idBoleto; }
    public void setIdBoleto(int idBoleto) { this.idBoleto = idBoleto; }
    public String getCodigoBoleto() { return codigoBoleto; }
    public void setCodigoBoleto(String codigoBoleto) { this.codigoBoleto = codigoBoleto; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }
}