/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 * Representa una instalación fotovoltaica asociada a un presupuesto.
 * <p>
 * Esta clase contiene la información técnica básica de una instalación
 * solar que se propone o se instala para un cliente.
 * </p>
 *
 * <p>
 * Los datos almacenados permiten estimar el rendimiento energético y
 * el ahorro económico generado por la instalación.
 * </p>
 *
 * La instalación se compone de:
 * <ul>
 * <li>Id Cliente</li>
 * <li>Potencia total instalada</li>
 * <li>Número de paneles solares</li>
 * <li>Producción energética estimada</li>
 * <li>Ahorro económico estimado</li>
 * <li>Dirección donde se realizará la instalación</li>
 * </ul>
 *
 * @author ivang
 */
public class InstalacionFotovoltaica {
    
    /** Id Cliente de referencia de la instalacion */
    private String IdCliente;
    /** Potencia total instalada en kW */
    private double potenciaInstalada;

    /** Número de paneles solares de la instalación */
    private int numeroPaneles;

    /** Producción energética estimada (kWh/año) */
    private double produccionEstimada;

    /** Ahorro económico estimado anual */
    private double ahorroEstimado;

    /** Dirección donde se ubica la instalación */
    private Direccion direccion;

    /**
     * Constructor vacío.
     * <p>
     * Permite crear una instalación sin inicializar sus atributos.
     * Útil para frameworks de persistencia o inicialización posterior.
     * </p>
     */
    public InstalacionFotovoltaica() {
    }

    /**
     * Constructor completo que inicializa todos los atributos de la instalación.
     *
     * @param IdCliente Cliente de referencia 
     * @param potenciaInstalada potencia total instalada
     * @param numeroPaneles número de paneles solares
     * @param produccionEstimada producción energética estimada
     * @param ahorroEstimado ahorro económico estimado
     * @param direccion dirección de la instalación
     */
    public InstalacionFotovoltaica(String IdCliente, double potenciaInstalada, int numeroPaneles,
                                   double produccionEstimada, double ahorroEstimado,
                                   Direccion direccion) {

        this.IdCliente = IdCliente;
        this.potenciaInstalada = potenciaInstalada;
        this.numeroPaneles = numeroPaneles;
        this.produccionEstimada = produccionEstimada;
        this.ahorroEstimado = ahorroEstimado;
        this.direccion = direccion;
    }

    public String getIdCliente() {
        return IdCliente;
    }

    public void setIdCliente(String IdCliente) {
        this.IdCliente = IdCliente;
    }

    /**
     * Obtiene la potencia total instalada.
     *
     * @return potencia instalada en kW
     */
    public double getPotenciaInstalada() {
        return potenciaInstalada;
    }

    /**
     * Establece la potencia total instalada.
     *
     * @param potenciaInstalada potencia instalada
     */
    public void setPotenciaInstalada(double potenciaInstalada) {
        this.potenciaInstalada = potenciaInstalada;
    }

    /**
     * Obtiene el número de paneles solares.
     *
     * @return número de paneles
     */
    public int getNumeroPaneles() {
        return numeroPaneles;
    }

    /**
     * Establece el número de paneles solares.
     *
     * @param numeroPaneles número de paneles
     */
    public void setNumeroPaneles(int numeroPaneles) {
        this.numeroPaneles = numeroPaneles;
    }

    /**
     * Obtiene la producción energética estimada.
     *
     * @return producción estimada
     */
    public double getProduccionEstimada() {
        return produccionEstimada;
    }

    /**
     * Establece la producción energética estimada.
     *
     * @param produccionEstimada producción estimada
     */
    public void setProduccionEstimada(double produccionEstimada) {
        this.produccionEstimada = produccionEstimada;
    }

    /**
     * Obtiene el ahorro económico estimado.
     *
     * @return ahorro estimado
     */
    public double getAhorroEstimado() {
        return ahorroEstimado;
    }

    /**
     * Establece el ahorro económico estimado.
     *
     * @param ahorroEstimado ahorro estimado
     */
    public void setAhorroEstimado(double ahorroEstimado) {
        this.ahorroEstimado = ahorroEstimado;
    }

    /**
     * Obtiene la dirección de la instalación.
     *
     * @return dirección de la instalación
     */
    public Direccion getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección de la instalación.
     *
     * @param direccion dirección de la instalación
     */
    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    /**
     * Devuelve una representación en texto de la instalación.
     * <p>
     * Útil para depuración, logs o visualización rápida de la información.
     * </p>
     *
     * @return descripción textual de la instalación
     */
    @Override
    public String toString() {
        return "Instalación FV | IdCliente: " + IdCliente
                + "Potencia: " + potenciaInstalada + " kW" +
               " | Paneles: " + numeroPaneles +
               " | Producción estimada: " + produccionEstimada +
               " | Ahorro estimado: " + ahorroEstimado +
               " | Dirección: " + direccion;
    }
}