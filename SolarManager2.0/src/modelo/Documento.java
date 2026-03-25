package modelo;

import java.time.LocalDate;

/**
 * Representa un documento almacenado en el sistema.
 * <p>
 * Un documento puede estar asociado a diferentes entidades del sistema
 * como clientes, presupuestos o facturas. Los documentos pueden ser
 * archivos PDF, imágenes u otros tipos de archivo relevantes para
 * la gestión de instalaciones fotovoltaicas.
 * </p>
 *
 * <p>
 * En la aplicación Solar Manager, los documentos se almacenan en la base
 * de datos y se relacionan con la entidad correspondiente mediante
 * el identificador {@code idReferencia}.
 * </p>
 *
 * @author ivang
 */
public class Documento {

    /**
     * Identificador único del documento.
     */
    private String id;

    /**
     * Nombre del documento o del archivo almacenado.
     */
    private String nombre;

    /**
     * Tipo de documento almacenado.
     */
    private TipoDocumento tipoDocumento;

    /**
     * Identificador de la entidad a la que pertenece el documento.
     * Por ejemplo, puede ser el id de un cliente, presupuesto o factura.
     */
    private String idReferencia;

    /**
     * Tipo de entidad a la que está asociado el documento.
     */
    private TipoEntidad tipoEntidad;

    /**
     * Fecha en la que el documento fue subido al sistema.
     */
    private LocalDate fechaSubida;

    /**
     * Constructor vacío.
     * <p>
     * Se utiliza principalmente para frameworks, serialización
     * o creación de objetos antes de establecer sus propiedades.
     * </p>
     */
    public Documento() {
    }

    /**
     * Constructor completo de la clase Documento.
     *
     * @param id Identificador único del documento
     * @param nombre Nombre del archivo o documento
     * @param tipoDocumento Tipo de documento
     * @param idReferencia Identificador de la entidad relacionada
     * @param tipoEntidad Tipo de entidad a la que pertenece el documento
     * @param fechaSubida Fecha en la que se subió el documento
     */
    public Documento(String id, String nombre, TipoDocumento tipoDocumento,
                     String idReferencia, TipoEntidad tipoEntidad, LocalDate fechaSubida) {

        this.id = id;
        this.nombre = nombre;
        this.tipoDocumento = tipoDocumento;
        this.idReferencia = idReferencia;
        this.tipoEntidad = tipoEntidad;
        this.fechaSubida = fechaSubida;
    }

    /**
     * Obtiene el identificador del documento.
     *
     * @return id del documento
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador del documento.
     *
     * @param id identificador único del documento
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del documento.
     *
     * @return nombre del documento
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del documento.
     *
     * @param nombre nombre del documento o archivo
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el tipo de documento.
     *
     * @return tipo de documento
     */
    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * Establece el tipo de documento.
     *
     * @param tipoDocumento tipo de documento
     */
    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    /**
     * Obtiene el identificador de la entidad relacionada.
     *
     * @return id de la entidad relacionada
     */
    public String getIdReferencia() {
        return idReferencia;
    }

    /**
     * Establece el identificador de la entidad relacionada.
     *
     * @param idReferencia id de la entidad relacionada
     */
    public void setIdReferencia(String idReferencia) {
        this.idReferencia = idReferencia;
    }

    /**
     * Obtiene el tipo de entidad asociada al documento.
     *
     * @return tipo de entidad
     */
    public TipoEntidad getTipoEntidad() {
        return tipoEntidad;
    }

    /**
     * Establece el tipo de entidad asociada al documento.
     *
     * @param tipoEntidad tipo de entidad
     */
    public void setTipoEntidad(TipoEntidad tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    /**
     * Obtiene la fecha en la que el documento fue subido.
     *
     * @return fecha de subida del documento
     */
    public LocalDate getFechaSubida() {
        return fechaSubida;
    }

    /**
     * Establece la fecha de subida del documento.
     *
     * @param fechaSubida fecha de subida
     */
    public void setFechaSubida(LocalDate fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    /**
     * Enumeración que define los tipos de documentos soportados
     * por el sistema.
     */
    public enum TipoDocumento {

        /**
         * Documento PDF correspondiente a un presupuesto.
         */
        PRESUPUESTO_PDF,

        /**
         * Documento PDF correspondiente a una factura.
         */
        FACTURA_PDF,

        /**
         * Contrato firmado con el cliente.
         */
        CONTRATO,

        /**
         * Fotografía de la instalación fotovoltaica.
         */
        FOTO_INSTALACION,

        /**
         * Otros documentos no clasificados.
         */
        OTRO
    }

    /**
     * Enumeración que define las entidades del sistema
     * a las que puede asociarse un documento.
     */
    public enum TipoEntidad {

        /**
         * Documento asociado a un cliente.
         */
        CLIENTE,

        /**
         * Documento asociado a un presupuesto.
         */
        PRESUPUESTO,

        /**
         * Documento asociado a una factura.
         */
        FACTURA
    }

}