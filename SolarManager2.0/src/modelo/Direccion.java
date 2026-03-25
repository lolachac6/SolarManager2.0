/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 * Representa una dirección postal dentro del sistema.
 * <p>
 * Esta clase se utiliza como componente reutilizable en distintas entidades
 * del sistema como {@link Cliente}, {@link Usuario}, {@link Proveedor} o
 * {@link InstalacionFotovoltaica}.
 * </p>
 * 
 * Una dirección está compuesta por los siguientes elementos:
 * <ul>
 * <li>Calle</li>
 * <li>Número</li>
 * <li>Código postal</li>
 * <li>Municipio</li>
 * <li>Provincia</li>
 * </ul>
 * 
 * @author ivang
 */
public class Direccion {

    /** Nombre de la calle */
    private String calle;

    /** Número del inmueble */
    private String numero;

    /** Código postal de la dirección */
    private String codigoPostal;

    /** Municipio o ciudad */
    private String municipio;

    /** Provincia */
    private String provincia;

    /**
     * Constructor vacío.
     * <p>
     * Permite crear una dirección sin inicializar sus atributos.
     * Es útil para frameworks de persistencia o creación dinámica de objetos.
     * </p>
     */
    public Direccion() {
    }

    /**
     * Constructor completo que inicializa todos los atributos de la dirección.
     *
     * @param calle nombre de la calle
     * @param numero número del inmueble
     * @param codigoPostal código postal
     * @param municipio municipio o ciudad
     * @param provincia provincia
     */
    public Direccion(String calle, String numero, String codigoPostal, String municipio, String provincia) {
        this.calle = calle;
        this.numero = numero;
        this.codigoPostal = codigoPostal;
        this.municipio = municipio;
        this.provincia = provincia;
    }

    /**
     * Obtiene el nombre de la calle.
     *
     * @return calle
     */
    public String getCalle() {
        return calle;
    }

    /**
     * Establece el nombre de la calle.
     *
     * @param calle nombre de la calle
     */
    public void setCalle(String calle) {
        this.calle = calle;
    }

    /**
     * Obtiene el número del inmueble.
     *
     * @return número
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Establece el número del inmueble.
     *
     * @param numero número del inmueble
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * Obtiene el código postal.
     *
     * @return código postal
     */
    public String getCodigoPostal() {
        return codigoPostal;
    }

    /**
     * Establece el código postal.
     *
     * @param codigoPostal código postal
     */
    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    /**
     * Obtiene el municipio o ciudad.
     *
     * @return municipio
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * Establece el municipio o ciudad.
     *
     * @param municipio municipio
     */
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    /**
     * Obtiene la provincia.
     *
     * @return provincia
     */
    public String getProvincia() {
        return provincia;
    }

    /**
     * Establece la provincia.
     *
     * @param provincia provincia
     */
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    /**
     * Devuelve la dirección formateada como texto.
     * <p>
     * Este método es útil para mostrar la dirección en documentos,
     * facturas, presupuestos o en la interfaz de usuario.
     * </p>
     *
     * @return dirección completa formateada
     */
    @Override
    public String toString() {
        return calle + " " + numero + ", " +
               codigoPostal + " " + municipio + ", " +
               provincia;
    }
}