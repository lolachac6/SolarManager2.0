package Integration.google.model;

/**
 * Modelo que representa el resultado devuelto por Geocoding API.
 * Contiene coordenadas y componentes principales de la dirección.
 */
public class ResultadoGeocoding {

    private final double latitud;
    private final double longitud;
    private final String calle;
    private final String numero;
    private final String ciudad;
    private final String provincia;
    private final String codigoPostal;

    /**
     * Crea un resultado de geocodificación.
     *
     * @param latitud Latitud obtenida
     * @param longitud Longitud obtenida
     * @param calle Calle obtenida
     * @param numero Número obtenido
     * @param ciudad Ciudad obtenida
     * @param provincia Provincia obtenida
     * @param codigoPostal Código postal obtenido
     */
    public ResultadoGeocoding(double latitud, double longitud, String calle, String numero,
                              String ciudad, String provincia, String codigoPostal) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.calle = calle;
        this.numero = numero;
        this.ciudad = ciudad;
        this.provincia = provincia;
        this.codigoPostal = codigoPostal;
    }

    /**
     * Devuelve la latitud.
     *
     * @return Latitud
     */
    public double getLatitud() {
        return latitud;
    }

    /**
     * Devuelve la longitud.
     *
     * @return Longitud
     */
    public double getLongitud() {
        return longitud;
    }

    /**
     * Devuelve la calle.
     *
     * @return Calle
     */
    public String getCalle() {
        return calle;
    }

    /**
     * Devuelve el número.
     *
     * @return Número
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Devuelve la ciudad.
     *
     * @return Ciudad
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * Devuelve la provincia.
     *
     * @return Provincia
     */
    public String getProvincia() {
        return provincia;
    }

    /**
     * Devuelve el código postal.
     *
     * @return Código postal
     */
    public String getCodigoPostal() {
        return codigoPostal;
    }
}