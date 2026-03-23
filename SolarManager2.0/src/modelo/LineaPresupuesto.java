/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 * Representa una línea dentro de un presupuesto.
 * <p>
 * Cada línea de presupuesto corresponde a un producto concreto incluido
 * en la instalación o en el presupuesto comercial.
 * </p>
 *
 * <p>
 * Una línea de presupuesto contiene:
 * </p>
 * <ul>
 * <li>El identificador del producto</li>
 * <li>El nombre del producto</li>
 * <li>La cantidad de unidades</li>
 * <li>El precio unitario</li>
 * <li>El importe total de la línea</li>
 * </ul>
 *
 * <p>
 * Estas líneas se almacenan dentro de la clase {@link Presupuesto}
 * y permiten calcular los importes totales del presupuesto.
 * </p>
 *
 * @author ivang
 */
public class LineaPresupuesto {

    /** Identificador del producto asociado a la línea */
    private String idProducto;

    /** Nombre del producto */
    private String nombreProducto;

    /** Cantidad de unidades del producto */
    private int cantidad;

    /** Precio unitario del producto */
    private double precioUnitario;

    /** Importe total de la línea (cantidad × precioUnitario) */
    private double totalLinea;

    /**
     * Constructor vacío.
     * <p>
     * Permite crear una línea de presupuesto sin inicializar sus atributos.
     * Es útil para frameworks de persistencia o inicialización posterior.
     * </p>
     */
    public LineaPresupuesto() {
    }

    /**
     * Constructor completo que inicializa todos los atributos de la línea.
     *
     * @param idProducto identificador del producto
     * @param nombreProducto nombre del producto
     * @param cantidad cantidad de unidades
     * @param precioUnitario precio unitario
     * @param totalLinea importe total de la línea
     */
    public LineaPresupuesto(String idProducto, String nombreProducto,
                            int cantidad, double precioUnitario, double totalLinea) {

        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.totalLinea = totalLinea;
    }

    /**
     * Obtiene el identificador del producto.
     *
     * @return id del producto
     */
    public String getIdProducto() {
        return idProducto;
    }

    /**
     * Establece el identificador del producto.
     *
     * @param idProducto id del producto
     */
    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    /**
     * Obtiene el nombre del producto.
     *
     * @return nombre del producto
     */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /**
     * Establece el nombre del producto.
     *
     * @param nombreProducto nombre del producto
     */
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    /**
     * Obtiene la cantidad de unidades del producto.
     *
     * @return cantidad de unidades
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad de unidades del producto.
     *
     * @param cantidad cantidad de unidades
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el precio unitario del producto.
     *
     * @return precio unitario
     */
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Establece el precio unitario del producto.
     *
     * @param precioUnitario precio unitario
     */
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /**
     * Obtiene el importe total de la línea.
     *
     * @return total de la línea
     */
    public double getTotalLinea() {
        return totalLinea;
    }

    /**
     * Establece el importe total de la línea.
     *
     * @param totalLinea total de la línea
     */
    public void setTotalLinea(double totalLinea) {
        this.totalLinea = totalLinea;
    }

    /**
     * Devuelve una representación en texto de la línea de presupuesto.
     * <p>
     * Útil para depuración, logs o visualización rápida en listados.
     * </p>
     *
     * @return descripción textual de la línea
     */
    @Override
    public String toString() {
        return nombreProducto +
               " | Cantidad: " + cantidad +
               " | Precio: " + precioUnitario +
               " | Total: " + totalLinea;
    }
}