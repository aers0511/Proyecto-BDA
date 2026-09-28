package DTOS;

import java.math.BigDecimal;

public class EventoDTO {

    private int idEvento;
    private String nombreShow;
    private String tipoEvento;
    private int edadMinima;
    private String imagen;
    private int cantidadBoletos;
    private BigDecimal precioBoleto;
    private int idPromotora;

    public EventoDTO() {
    }

    // Constructor sin ID (útil para registrar un nuevo evento)
    public EventoDTO(String nombreShow, String tipoEvento, int edadMinima, String imagen, 
                     int cantidadBoletos, BigDecimal precioBoleto, int idPromotora) {
        this.nombreShow = nombreShow;
        this.tipoEvento = tipoEvento;
        this.edadMinima = edadMinima;
        this.imagen = imagen;
        this.cantidadBoletos = cantidadBoletos;
        this.precioBoleto = precioBoleto;
        this.idPromotora = idPromotora;
    }

    // Constructor completo con ID (para lecturas y catálogo)
    public EventoDTO(int idEvento, String nombreShow, String tipoEvento, int edadMinima, 
                     String imagen, int cantidadBoletos, BigDecimal precioBoleto, int idPromotora) {
        this.idEvento = idEvento;
        this.nombreShow = nombreShow;
        this.tipoEvento = tipoEvento;
        this.edadMinima = edadMinima;
        this.imagen = imagen;
        this.cantidadBoletos = cantidadBoletos;
        this.precioBoleto = precioBoleto;
        this.idPromotora = idPromotora;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public String getNombreShow() {
        return nombreShow;
    }

    public void setNombreShow(String nombreShow) {
        this.nombreShow = nombreShow;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public int getEdadMinima() {
        return edadMinima;
    }

    public void setEdadMinima(int edadMinima) {
        this.edadMinima = edadMinima;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getCantidadBoletos() {
        return cantidadBoletos;
    }

    public void setCantidadBoletos(int cantidadBoletos) {
        this.cantidadBoletos = cantidadBoletos;
    }

    public BigDecimal getPrecioBoleto() {
        return precioBoleto;
    }

    public void setPrecioBoleto(BigDecimal precioBoleto) {
        this.precioBoleto = precioBoleto;
    }

    public int getIdPromotora() {
        return idPromotora;
    }

    public void setIdPromotora(int idPromotora) {
        this.idPromotora = idPromotora;
    }

    @Override
    public String toString() {
        return nombreShow;
    }
}