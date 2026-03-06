/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 * Representa un producto disponible dentro del sistema.
 *
 * <p>
 * Los productos se utilizan para construir las líneas de presupuesto
 * dentro de un {@link Presupuesto}. Cada producto pertenece a un
 * proveedor y tiene un tipo específico dentro del catálogo de la empresa.
 * </p>
 *
 * <p>
 * Ejemplos de productos:
 * </p>
 * <ul>
 * <li>Paneles solares</li>
 * <li>Inversores</li>
 * <li>Baterías</li>
 * <li>Estructuras de soporte</li>
 * <li>Material eléctrico</li>
 * </ul>
 *
 * <p>
 * El sistema también permite llevar un control básico de stock
 * de los productos disponibles.
 * </p>
 *
 * @author ivang
 */
public class Producto {

    /** Identificador único del producto */
    private String id;

    /** Nombre del producto */
    private String nombre;

    /** Tipo de producto dentro del catálogo */
    private TipoProducto tipoProducto;

    /** Precio unitario del producto */
    private double precio;

    /** Identificador del proveedor que suministra el producto */
    private String idProveedor;

    /** Cantidad disponible en stock */
    private int stock;

    /**
     * Constructor vacío.
     * <p>
     * Necesario para frameworks de persistencia o inicialización posterior.
     * </p>
     */
    public Producto() {
    }

    /**
     * Constructor completo para inicializar todos los atributos del producto.
     *
     * @param id identificador del producto
     * @param nombre nombre del producto
     * @param tipoProducto tipo de producto
     * @param precio precio unitario
     * @param idProveedor identificador del proveedor
     * @param stock cantidad disponible en stock
     */
    public Producto(String id, String nombre, TipoProducto tipoProducto,
                    double precio, String idProveedor, int stock) {

        this.id = id;
        this.nombre = nombre;
        this.tipoProducto = tipoProducto;
        this.precio = precio;
        this.idProveedor = idProveedor;
        this.stock = stock;
    }

    /**
     * Obtiene el identificador del producto.
     *
     * @return id del producto
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador del producto.
     *
     * @param id identificador del producto
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del producto.
     *
     * @return nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del producto.
     *
     * @param nombre nombre del producto
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el tipo de producto.
     *
     * @return tipo de producto
     */
    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    /**
     * Establece el tipo de producto.
     *
     * @param tipoProducto tipo de producto
     */
    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    /**
     * Obtiene el precio del producto.
     *
     * @return precio unitario
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * Establece el precio del producto.
     *
     * @param precio precio unitario
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     * Obtiene el identificador del proveedor.
     *
     * @return id del proveedor
     */
    public String getIdProveedor() {
        return idProveedor;
    }

    /**
     * Establece el identificador del proveedor.
     *
     * @param idProveedor id del proveedor
     */
    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    /**
     * Obtiene la cantidad disponible en stock.
     *
     * @return stock disponible
     */
    public int getStock() {
        return stock;
    }

    /**
     * Establece la cantidad disponible en stock.
     *
     * @param stock stock disponible
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Devuelve una representación textual del producto.
     *
     * @return descripción del producto
     */
    @Override
    public String toString() {
        return nombre +
               " | Tipo: " + tipoProducto +
               " | Precio: " + precio +
               " € | Stock: " + stock;
    }

    /**
     * Enumeración que define los tipos de productos disponibles
     * dentro del sistema.
     */
    public enum TipoProducto {

        /** Panel solar fotovoltaico */
        PANEL_SOLAR,

        /** Inversor solar */
        INVERSOR,

        /** Sistema de baterías */
        BATERIA,

        /** Estructura de soporte para paneles */
        ESTRUCTURA,

        /** Material eléctrico auxiliar */
        MATERIAL_ELECTRICO
    }

}