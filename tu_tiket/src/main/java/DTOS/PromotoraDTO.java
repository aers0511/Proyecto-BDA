package DTOS;

public class PromotoraDTO {

    private int idPromotora;
    private String nombreComercial;
    private String calle;
    private String numero;
    private String colonia;
    private String ciudad;
    private String estado;

    public PromotoraDTO() {
    }

    // Constructor sin ID (útil para inserciones/registro)
    public PromotoraDTO(String nombreComercial, String calle, String numero, String colonia, String ciudad, String estado) {
        this.nombreComercial = nombreComercial;
        this.calle = calle;
        this.numero = numero;
        this.colonia = colonia;
        this.ciudad = ciudad;
        this.estado = estado;
    }

    // Constructor completo con ID (para lecturas/consultas)
    public PromotoraDTO(int idPromotora, String nombreComercial, String calle, String numero, String colonia, String ciudad, String estado) {
        this.idPromotora = idPromotora;
        this.nombreComercial = nombreComercial;
        this.calle = calle;
        this.numero = numero;
        this.colonia = colonia;
        this.ciudad = ciudad;
        this.estado = estado;
    }

    public int getIdPromotora() {
        return idPromotora;
    }

    public void setIdPromotora(int idPromotora) {
        this.idPromotora = idPromotora;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombreComercial; // Facilita mostrar el nombre en ComboBoxes de la UI
    }
}