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
 * <li>Id de la instalación</li>
 * <li>Id Cliente</li>
 * <li>Potencia total instalada</li>
 * <li>Número de paneles solares</li>
 * <li>Producción energética estimada</li>
 * <li>Ahorro económico estimado</li>
 * <li>Dirección donde se realizará la instalación</li>
 * <li>Inversor asociado a la instalación</li>
 * <li>Indica si la instalación lleva batería</li>
 * </ul>
 *
 * @author ivang
 */
public class InstalacionFotovoltaica {

    /** Id de la instalación */
    private String id;

    /** Id Cliente de referencia de la instalación */
    private String idCliente;

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

    /** Inversor asociado a la instalación */
    private String inversor;

    /** Indica si la instalación lleva batería */
    private boolean bateria;

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
     * @param id id de la instalación
     * @param idCliente cliente de referencia
     * @param potenciaInstalada potencia total instalada
     * @param numeroPaneles número de paneles solares
     * @param produccionEstimada producción energética estimada
     * @param ahorroEstimado ahorro económico estimado
     * @param direccion dirección de la instalación
     * @param inversor inversor asociado a la instalación
     * @param bateria true si la instalación lleva batería
     */
    public InstalacionFotovoltaica(String id, String idCliente, double potenciaInstalada, int numeroPaneles,
                                   double produccionEstimada, double ahorroEstimado,
                                   Direccion direccion, String inversor, boolean bateria) {
        this.id = id;
        this.idCliente = idCliente;
        this.potenciaInstalada = potenciaInstalada;
        this.numeroPaneles = numeroPaneles;
        this.produccionEstimada = produccionEstimada;
        this.ahorroEstimado = ahorroEstimado;
        this.direccion = direccion;
        this.inversor = inversor;
        this.bateria = bateria;
    }

    /**
     * Obtiene el id de la instalación.
     *
     * @return id de la instalación
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el id de la instalación.
     *
     * @param id id de la instalación
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el id del cliente asociado.
     *
     * @return id del cliente
     */
    public String getIdCliente() {
        return idCliente;
    }

    /**
     * Establece el id del cliente asociado.
     *
     * @param idCliente id del cliente
     */
    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
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
     * Obtiene el inversor asociado a la instalación.
     *
     * @return inversor de la instalación
     */
    public String getInversor() {
        return inversor;
    }

    /**
     * Establece el inversor asociado a la instalación.
     *
     * @param inversor inversor de la instalación
     */
    public void setInversor(String inversor) {
        this.inversor = inversor;
    }

    /**
     * Indica si la instalación lleva batería.
     *
     * @return true si la instalación lleva batería
     */
    public boolean getBateria() {
        return bateria;
    }

    /**
     * Establece si la instalación lleva batería.
     *
     * @param bateria true si la instalación lleva batería
     */
    public void setBateria(boolean bateria) {
        this.bateria = bateria;
    }

    /**
     * Devuelve una representación en texto de la instalación.
     *
     * @return descripción textual de la instalación
     */
    @Override
    public String toString() {
        return "Instalación FV | Id: " + id
                + " | IdCliente: " + idCliente
                + " | Potencia: " + potenciaInstalada + " kW"
                + " | Paneles: " + numeroPaneles
                + " | Producción estimada: " + produccionEstimada
                + " | Ahorro estimado: " + ahorroEstimado
                + " | Dirección: " + direccion
                + " | Inversor: " + inversor
                + " | Batería: " + bateria;
    }
}