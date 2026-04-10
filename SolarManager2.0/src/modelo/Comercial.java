package modelo;

/**
 * Representa un comercial del sistema Solar Manager.
 * <p>
 * Un comercial es una persona que trabaja para la empresa y gestiona clientes
 * o presupuestos dentro del sistema.
 * 
 * La clase hereda de {@link Persona}, por lo que dispone de los atributos
 * comunes como nombre, apellidos, teléfono, email y dirección.
 * 
 * Cada comercial tiene un tipo de contrato y un centro de trabajo asignado.
 * 
 * @author ivang
 */
public class Comercial extends Persona {

    /** Identificador único del comercial */
    private String id;

    /** Tipo de contrato del comercial */
    private TipoContrato tipoContrato;

    /** Documento de identidad del comercial */
    private String dni;
    
    /** Password */
    private String password;

    /** Número de cuenta bancaria */
    private String numeroCuenta;

    /** Centro de trabajo asignado */
    private String centroTrabajo;

    /** Observaciones o notas adicionales */
    private String observaciones;
    
    private boolean activo;

    /**
     * Constructor vacío.
     * <p>
     * Necesario para frameworks de persistencia y para crear el objeto sin
     * inicializar sus atributos.
     */
    public Comercial() {
    }

    /**
     * Constructor completo para inicializar todos los atributos del comercial.
     *
     * @param id identificador del comercial
     * @param nombre nombre
     * @param apellidos apellidos
     * @param telefono teléfono
     * @param email correo electrónico
     * @param password password
     * @param direccion dirección
     * @param tipoContrato tipo de contrato
     * @param dni documento de identidad
     * @param numeroCuenta número de cuenta bancaria
     * @param centroTrabajo centro de trabajo
     * @param observaciones comentarios adicionales
     * @param activo si continua trabajando
     */
    public Comercial(String id,
                     String nombre,
                     String apellidos,
                     String telefono,
                     String email,
                     String password, 
                     Direccion direccion,
                     TipoContrato tipoContrato,
                     String dni,
                     String numeroCuenta,
                     String centroTrabajo,
                     String observaciones,
                     Boolean activo) {

        super(nombre, apellidos, telefono, email, direccion);

        this.id = id;
        this.tipoContrato = tipoContrato;
        this.dni = dni;
        this.numeroCuenta = numeroCuenta;
        this.centroTrabajo = centroTrabajo;
        this.observaciones = observaciones;
        this.activo = activo;
        this.password = password;
    }

    /**
     * Obtiene el identificador del comercial.
     *
     * @return id del comercial
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador del comercial.
     *
     * @param id nuevo identificador
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el tipo de contrato.
     *
     * @return tipo de contrato
     */
    public TipoContrato getTipoContrato() {
        return tipoContrato;
    }

    /**
     * Define el tipo de contrato.
     *
     * @param tipoContrato tipo de contrato
     */
    public void setTipoContrato(TipoContrato tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    /**
     * Obtiene el DNI del comercial.
     *
     * @return DNI
     */
    public String getDni() {
        return dni;
    }

    /**
     * Establece el DNI del comercial.
     *
     * @param dni documento nacional de identidad
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Obtiene el número de cuenta bancaria.
     *
     * @return número de cuenta
     */
    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    /**
     * Define el número de cuenta bancaria.
     *
     * @param numeroCuenta número de cuenta
     */
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    /**
     * Obtiene el centro de trabajo.
     *
     * @return centro de trabajo
     */
    public String getCentroTrabajo() {
        return centroTrabajo;
    }

    /**
     * Establece el centro de trabajo.
     *
     * @param centroTrabajo centro de trabajo
     */
    public void setCentroTrabajo(String centroTrabajo) {
        this.centroTrabajo = centroTrabajo;
    }

    /**
     * Obtiene las observaciones asociadas al comercial.
     *
     * @return observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Establece las observaciones del comercial.
     *
     * @param observaciones comentarios adicionales
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    /**
     *
     * @return
     */
    public boolean getActivo(){
       return this.activo;
    }

    /**
     *
     * @param activo
     */
    public void setActivo ( Boolean activo){
        this.activo = activo;
        
    }
    
    public void setPassword (String password){
        this.password = password;   
    }
    
    public String getPassword() {
    return password;
}
    
    /**
     * Enumeración que define los tipos de contrato posibles.
     */
    public enum TipoContrato {

        /** Jornada completa */
        COMPLETA,

        /** Jornada media */
        MEDIA

    }
}