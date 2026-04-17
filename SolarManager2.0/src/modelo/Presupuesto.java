/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDate;
import java.util.List;

/**
 * Representa un presupuesto generado para un cliente dentro del sistema.
 *
 * <p>
 * El presupuesto contiene la información comercial y técnica de una posible
 * instalación fotovoltaica ofrecida a un cliente.
 * </p>
 *
 * <p>
 * Cada presupuesto está asociado a:
 * </p>
 * <ul>
 * <li>Un cliente</li>
 * <li>Un comercial responsable</li>
 * <li>Una instalación fotovoltaica propuesta</li>
 * <li>Un conjunto de líneas de presupuesto con los productos incluidos</li>
 * </ul>
 *
 * <p>
 * Los importes económicos se dividen en:
 * </p>
 * <ul>
 * <li>Subtotal (suma de todas las líneas)</li>
 * <li>IVA</li>
 * <li>Total final</li>
 * </ul>
 *
 * <p>
 * Cuando un presupuesto es aceptado por el cliente puede convertirse en
 * una {@link Factura}.
 * </p>
 *
 * @author ivang
 */
public class Presupuesto {

    /** Identificador único del presupuesto */
    private String id;

    /** Identificador del cliente al que pertenece el presupuesto */
    private String idCliente;

    /** Identificador del comercial responsable */
    private String idComercial;

    /** Fecha de creación del presupuesto */
    private LocalDate fechaCreacion;

    /** Estado actual del presupuesto */
    private EstadoPresupuesto estado;

    /** Datos técnicos de la instalación fotovoltaica propuesta */
    private InstalacionFotovoltaica instalacion;

    /** Lista de productos incluidos en el presupuesto */
    private List<LineaPresupuesto> lineas;

    /** Subtotal del presupuesto (suma de todas las líneas) */
    private double subtotal;

    /** Importe del IVA */
    private double iva;

    /** Total final del presupuesto */
    private double total;

    /**
     * Constructor vacío.
     * <p>
     * Necesario para frameworks de persistencia o creación dinámica de objetos.
     * </p>
     */
    public Presupuesto() {
    }

    /**
     * Constructor completo para inicializar todos los atributos del presupuesto.
     *
     * @param id identificador del presupuesto
     * @param idCliente identificador del cliente
     * @param idComercial identificador del comercial
     * @param fechaCreacion fecha de creación
     * @param estado estado del presupuesto
     * @param instalacion instalación fotovoltaica propuesta
     * @param lineas lista de líneas de presupuesto
     * @param subtotal subtotal del presupuesto
     * @param iva importe del IVA
     * @param total total final del presupuesto
     */
    public Presupuesto(String id, String idCliente, String idComercial,
                       LocalDate fechaCreacion, EstadoPresupuesto estado,
                       InstalacionFotovoltaica instalacion,
                       List<LineaPresupuesto> lineas,
                       double subtotal, double iva, double total) {

        this.id = id;
        this.idCliente = idCliente;
        this.idComercial = idComercial;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.instalacion = instalacion;
        this.lineas = lineas;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
    }

    /**
     * Obtiene el identificador del presupuesto.
     *
     * @return id del presupuesto
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador del presupuesto.
     *
     * @param id identificador del presupuesto
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el identificador del cliente.
     *
     * @return id del cliente
     */
    public String getIdCliente() {
        return idCliente;
    }

    /**
     * Establece el identificador del cliente.
     *
     * @param idCliente id del cliente
     */
    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    /**
     * Obtiene el identificador del comercial responsable.
     *
     * @return id del comercial
     */
    public String getIdComercial() {
        return idComercial;
    }

    /**
     * Establece el identificador del comercial responsable.
     *
     * @param idComercial id del comercial
     */
    public void setIdComercial(String idComercial) {
        this.idComercial = idComercial;
    }

    /**
     * Obtiene la fecha de creación del presupuesto.
     *
     * @return fecha de creación
     */
    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Establece la fecha de creación del presupuesto.
     *
     * @param fechaCreacion fecha de creación
     */
    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Obtiene el estado del presupuesto.
     *
     * @return estado del presupuesto
     */
    public EstadoPresupuesto getEstado() {
        return estado;
    }

    /**
     * Establece el estado del presupuesto.
     *
     * @param estado nuevo estado
     */
    public void setEstado(EstadoPresupuesto estado) {
        this.estado = estado;
    }

    /**
     * Obtiene la instalación fotovoltaica asociada al presupuesto.
     *
     * @return instalación
     */
    public InstalacionFotovoltaica getInstalacion() {
        return instalacion;
    }

    /**
     * Establece la instalación fotovoltaica del presupuesto.
     *
     * @param instalacion instalación
     */
    public void setInstalacion(InstalacionFotovoltaica instalacion) {
        this.instalacion = instalacion;
    }

    /**
     * Obtiene la lista de líneas de presupuesto.
     *
     * @return lista de líneas
     */
    public List<LineaPresupuesto> getLineas() {
        return lineas;
    }

    /**
     * Establece la lista de líneas del presupuesto.
     *
     * @param lineas líneas de presupuesto
     */
    public void setLineas(List<LineaPresupuesto> lineas) {
        this.lineas = lineas;
    }

    /**
     * Obtiene el subtotal del presupuesto.
     *
     * @return subtotal
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Establece el subtotal del presupuesto.
     *
     * @param subtotal subtotal
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Obtiene el importe del IVA.
     *
     * @return IVA
     */
    public double getIva() {
        return iva;
    }

    /**
     * Establece el importe del IVA.
     *
     * @param iva IVA
     */
    public void setIva(double iva) {
        this.iva = iva;
    }

    /**
     * Obtiene el total final del presupuesto.
     *
     * @return total
     */
    public double getTotal() {
        return total;
    }

    /**
     * Establece el total final del presupuesto.
     *
     * @param total total
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * Devuelve una representación textual del presupuesto.
     *
     * @return descripción del presupuesto
     */
    @Override
    public String toString() {
        return "Presupuesto " + id +
               " | Cliente: " + idCliente +
               " | Comercial: " + idComercial +
               " | Fecha: " + fechaCreacion +
               " | Estado: " + estado +
               " | Total: " + total + " €";
    }

  
    /**
     * Enumeración que representa los posibles estados de un presupuesto.
     */
    public enum EstadoPresupuesto {

        /** Presupuesto en fase de edición */
        BORRADOR,

        /** Presupuesto enviado al cliente */
        ENVIADO,

        /** Presupuesto aceptado por el cliente */
        ACEPTADO,

        /** Presupuesto rechazado */
        RECHAZADO,

        /** Presupuesto ya convertido en factura */
        FACTURADO
    }

}