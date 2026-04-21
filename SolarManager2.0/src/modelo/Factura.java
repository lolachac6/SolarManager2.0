package modelo;

import java.time.LocalDate;

/**
 * Representa una factura generada a partir de un presupuesto aceptado.
 * <p>
 * En el sistema Solar Manager las facturas se generan siempre a partir
 * de un {@link Presupuesto}. Por ello la clase factura almacena únicamente
 * el identificador del presupuesto asociado ({@code idPresupuesto}).
 * </p>
 *
 * <p>
 * Para obtener los datos completos del cliente al generar el documento
 * de la factura se sigue la cadena:
 * </p>
 *
 * <pre>
 * Factura → idPresupuesto → Presupuesto → idCliente → Cliente
 * </pre>
 *
 * <p>
 * Esto evita duplicar información y mantiene el modelo de datos limpio.
 * </p>
 *
 * @author ivang
 */
public class Factura {

    /**
     * Identificador único de la factura.
     */
    private String id;

    /**
     * Número de factura.
     */
    private String numeroFactura;

    /**
     * Identificador del presupuesto del que se genera la factura.
     */
    private String idPresupuesto;

    /**
     * Fecha en la que se emite la factura.
     */
    private LocalDate fechaEmision;

    /**
     * Base imponible de la factura.
     */
    private double baseImponible;

    /**
     * Importe correspondiente al IVA.
     */
    private double iva;

    /**
     * Importe total de la factura.
     */
    private double total;

    /**
     * PDF binario de la factura.
     */
    private byte[] pdfFactura;

    /**
     * Nombre lógico del archivo PDF de la factura.
     */
    private String nombreArchivo;

    /**
     * Constructor vacío.
     * <p>
     * Necesario para frameworks de persistencia o creación dinámica de objetos.
     * </p>
     */
    public Factura() {
    }

    /**
     * Constructor completo para inicializar todos los atributos principales
     * de la factura.
     *
     * @param id identificador de la factura
     * @param numeroFactura número de factura
     * @param idPresupuesto identificador del presupuesto asociado
     * @param fechaEmision fecha de emisión
     * @param baseImponible base imponible
     * @param iva importe del IVA
     * @param total importe total de la factura
     */
    public Factura(String id, String numeroFactura, String idPresupuesto,
                   LocalDate fechaEmision, double baseImponible,
                   double iva, double total) {
        this.id = id;
        this.numeroFactura = numeroFactura;
        this.idPresupuesto = idPresupuesto;
        this.fechaEmision = fechaEmision;
        this.baseImponible = baseImponible;
        this.iva = iva;
        this.total = total;
    }

    /**
     * Obtiene el identificador de la factura.
     *
     * @return id de la factura
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador de la factura.
     *
     * @param id identificador de la factura
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el número de factura.
     *
     * @return número de factura
     */
    public String getNumeroFactura() {
        return numeroFactura;
    }

    /**
     * Establece el número de factura.
     *
     * @param numeroFactura número de factura
     */
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    /**
     * Obtiene el identificador del presupuesto asociado.
     *
     * @return id del presupuesto
     */
    public String getIdPresupuesto() {
        return idPresupuesto;
    }

    /**
     * Establece el identificador del presupuesto asociado.
     *
     * @param idPresupuesto id del presupuesto
     */
    public void setIdPresupuesto(String idPresupuesto) {
        this.idPresupuesto = idPresupuesto;
    }

    /**
     * Obtiene la fecha de emisión de la factura.
     *
     * @return fecha de emisión
     */
    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    /**
     * Establece la fecha de emisión de la factura.
     *
     * @param fechaEmision fecha de emisión
     */
    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    /**
     * Obtiene la base imponible de la factura.
     *
     * @return base imponible
     */
    public double getBaseImponible() {
        return baseImponible;
    }

    /**
     * Establece la base imponible de la factura.
     *
     * @param baseImponible base imponible
     */
    public void setBaseImponible(double baseImponible) {
        this.baseImponible = baseImponible;
    }

    /**
     * Obtiene el importe correspondiente al IVA.
     *
     * @return IVA
     */
    public double getIva() {
        return iva;
    }

    /**
     * Establece el importe del IVA.
     *
     * @param iva importe del IVA
     */
    public void setIva(double iva) {
        this.iva = iva;
    }

    /**
     * Obtiene el importe total de la factura.
     *
     * @return total de la factura
     */
    public double getTotal() {
        return total;
    }

    /**
     * Establece el importe total de la factura.
     *
     * @param total total de la factura
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * Obtiene el PDF binario de la factura.
     *
     * @return PDF en binario
     */
    public byte[] getPdfFactura() {
        return pdfFactura;
    }

    /**
     * Establece el PDF binario de la factura.
     *
     * @param pdfFactura PDF en binario
     */
    public void setPdfFactura(byte[] pdfFactura) {
        this.pdfFactura = pdfFactura;
    }

    /**
     * Obtiene el nombre lógico del archivo PDF.
     *
     * @return nombre del archivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * Establece el nombre lógico del archivo PDF.
     *
     * @param nombreArchivo nombre del archivo
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * Devuelve una representación en texto de la factura.
     * <p>
     * Útil para depuración, logs o mostrar facturas en listados.
     * </p>
     *
     * @return representación textual de la factura
     */
    @Override
    public String toString() {
        return "Factura " + numeroFactura +
               " | Presupuesto: " + idPresupuesto +
               " | Fecha: " + fechaEmision +
               " | Total: " + total + " €";
    }
}