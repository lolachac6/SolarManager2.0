/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 * Representa un proveedor dentro del sistema.
 *
 * <p>
 * Los proveedores suministran los productos utilizados en las instalaciones
 * fotovoltaicas. Cada proveedor puede tener asociados múltiples
 * {@link Producto}.
 * </p>
 *
 * <p>
 * La clase hereda de {@link Persona}, ya que comparte los datos básicos de
 * contacto como nombre, teléfono, email y dirección.
 * </p>
 *
 * <p>
 * Además se añaden datos específicos del proveedor como:
 * </p>
 * <ul>
 * <li>Nombre de la empresa</li>
 * <li>Página web</li>
 * <li>Observaciones o notas internas</li>
 * </ul>
 *
 * @author ivang
 */
public class Proveedor extends Persona {

    /** Identificador único del proveedor */
    private String id;

    /** Nombre de la empresa proveedora */
    private String nombreEmpresa;

    /** Página web del proveedor */
    private String web;

    /** Observaciones o notas internas sobre el proveedor */
    private String observaciones;

    /**
     * Constructor vacío.
     * <p>
     * Necesario para frameworks de persistencia o inicialización posterior.
     * </p>
     */
    public Proveedor() {
    }

    /**
     * Constructor completo para inicializar todos los atributos del proveedor.
     *
     * @param id identificador del proveedor
     * @param nombre nombre de la persona de contacto
     * @param apellidos apellidos de la persona de contacto
     * @param telefono teléfono de contacto
     * @param email correo electrónico
     * @param direccion dirección del proveedor
     * @param nombreEmpresa nombre de la empresa
     * @param web página web
     * @param observaciones notas u observaciones
     */
    public Proveedor(String id, String nombre, String apellidos, String telefono,
                     String email, Direccion direccion,
                     String nombreEmpresa, String web, String observaciones) {

        super(nombre, apellidos, telefono, email, direccion);

        this.id = id;
        this.nombreEmpresa = nombreEmpresa;
        this.web = web;
        this.observaciones = observaciones;
    }

    /**
     * Obtiene el identificador del proveedor.
     *
     * @return id del proveedor
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador del proveedor.
     *
     * @param id identificador del proveedor
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de la empresa proveedora.
     *
     * @return nombre de la empresa
     */
    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    /**
     * Establece el nombre de la empresa proveedora.
     *
     * @param nombreEmpresa nombre de la empresa
     */
    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    /**
     * Obtiene la página web del proveedor.
     *
     * @return página web
     */
    public String getWeb() {
        return web;
    }

    /**
     * Establece la página web del proveedor.
     *
     * @param web página web
     */
    public void setWeb(String web) {
        this.web = web;
    }

    /**
     * Obtiene las observaciones sobre el proveedor.
     *
     * @return observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Establece las observaciones sobre el proveedor.
     *
     * @param observaciones observaciones
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Devuelve una representación textual del proveedor.
     *
     * <p>
     * Útil para depuración, logs o visualización rápida en listados.
     * </p>
     *
     * @return descripción del proveedor
     */
    @Override
    public String toString() {
        return nombreEmpresa +
               " | Contacto: " + getNombre() + " " + getApellidos() +
               " | Tel: " + getTelefono() +
               " | Web: " + web;
    }
}