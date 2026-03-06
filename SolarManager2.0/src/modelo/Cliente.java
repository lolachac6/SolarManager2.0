/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 * Representa un cliente del sistema Solar Manager.
 * <p>
 * Un cliente puede ser de dos tipos:
 * <ul>
 * <li>PARTICULAR (identificado mediante DNI)</li>
 * <li>EMPRESA (identificada mediante CIF)</li>
 * </ul>
 * 
 * La clase hereda de {@link Persona}, por lo que dispone de los atributos
 * comunes como nombre, apellidos, teléfono, email y dirección.
 * 
 * Cada cliente puede tener un comercial asignado mediante su identificador.
 * 
 * @author ivang
 */
public class Cliente extends Persona {

    /** Identificador único del cliente */
    private String id;

    /** Tipo de cliente (PARTICULAR o EMPRESA) */
    private TipoCliente tipoCliente;

    /** Documento de identidad para clientes particulares */
    private String dni;

    /** Documento fiscal para clientes empresa */
    private String cif;

    /** Número de cuenta bancaria del cliente */
    private String numeroCuenta;

    /** Observaciones o notas adicionales sobre el cliente */
    private String observaciones;

    /** Identificador del comercial asignado al cliente */
    private String idComercialAsignado;

    /**
     * Constructor vacío.
     * <p>
     * Necesario para frameworks de persistencia y para crear el objeto sin
     * inicializar sus atributos.
     */
    public Cliente() {
    }

    /**
     * Constructor completo para inicializar todos los atributos del cliente.
     *
     * @param id Identificador del cliente
     * @param nombre Nombre del cliente
     * @param apellidos Apellidos del cliente
     * @param telefono Teléfono de contacto
     * @param email Correo electrónico
     * @param direccion Dirección del cliente
     * @param tipoCliente Tipo de cliente (PARTICULAR o EMPRESA)
     * @param dni DNI del cliente si es particular
     * @param cif CIF del cliente si es empresa
     * @param numeroCuenta Número de cuenta bancaria
     * @param observaciones Comentarios adicionales
     * @param idComercialAsignado Identificador del comercial responsable
     */
    public Cliente(String id, String nombre, String apellidos, String telefono, String email,
                   Direccion direccion, TipoCliente tipoCliente, String dni, String cif,
                   String numeroCuenta, String observaciones, String idComercialAsignado) {

        super(nombre, apellidos, telefono, email, direccion);

        this.id = id;
        this.tipoCliente = tipoCliente;
        this.dni = dni;
        this.cif = cif;
        this.numeroCuenta = numeroCuenta;
        this.observaciones = observaciones;
        this.idComercialAsignado = idComercialAsignado;
    }

    /**
     * Obtiene el identificador del cliente.
     *
     * @return id del cliente
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador del cliente.
     *
     * @param id nuevo identificador
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el tipo de cliente.
     *
     * @return tipo de cliente
     */
    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    /**
     * Define el tipo de cliente.
     *
     * @param tipoCliente tipo de cliente
     */
    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    /**
     * Obtiene el DNI del cliente.
     *
     * @return DNI del cliente
     */
    public String getDni() {
        return dni;
    }

    /**
     * Establece el DNI del cliente.
     *
     * @param dni documento nacional de identidad
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Obtiene el CIF del cliente.
     *
     * @return CIF del cliente
     */
    public String getCif() {
        return cif;
    }

    /**
     * Establece el CIF del cliente.
     *
     * @param cif código de identificación fiscal
     */
    public void setCif(String cif) {
        this.cif = cif;
    }

    /**
     * Obtiene el número de cuenta bancaria del cliente.
     *
     * @return número de cuenta
     */
    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    /**
     * Define el número de cuenta bancaria del cliente.
     *
     * @param numeroCuenta número de cuenta
     */
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    /**
     * Obtiene las observaciones asociadas al cliente.
     *
     * @return observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Establece las observaciones del cliente.
     *
     * @param observaciones comentarios adicionales
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Obtiene el identificador del comercial asignado al cliente.
     *
     * @return id del comercial
     */
    public String getIdComercialAsignado() {
        return idComercialAsignado;
    }

    /**
     * Establece el comercial asignado al cliente.
     *
     * @param idComercialAsignado identificador del comercial
     */
    public void setIdComercialAsignado(String idComercialAsignado) {
        this.idComercialAsignado = idComercialAsignado;
    }

    /**
     * Enumeración que define los tipos de cliente posibles.
     */
    public enum TipoCliente {

        /** Cliente particular identificado mediante DNI */
        PARTICULAR,

        /** Cliente empresa identificado mediante CIF */
        EMPRESA

    }
}