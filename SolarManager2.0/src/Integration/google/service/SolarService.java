package Integration.google.service;

import Integration.google.client.GeocodingClient;
import Integration.google.client.SolarAPIClient;
import Integration.google.exception.DatosEntradaInvalidosException;
import Integration.google.exception.DireccionNoCoincideException;
import Integration.google.model.ResultadoGeocoding;
import modelo.ResultadoSolar;
import org.json.JSONObject;

/**
 * Servicio encargado de la lógica de negocio del cálculo solar.
 */
public class SolarService {

    private final GeocodingClient geocodingClient = new GeocodingClient();
    private final SolarAPIClient solarClient = new SolarAPIClient();

    /**
     * Calcula la instalación solar a partir de dirección, código postal y consumo.
     *
     * @param direccion Dirección completa
     * @param codigoPostalUsuario Código postal introducido por el usuario
     * @param consumoAnual Consumo anual en kWh
     * @return ResultadoSolar con todos los datos calculados
     * @throws DatosEntradaInvalidosException Si los datos de entrada no son válidos
     * @throws DireccionNoCoincideException Si la dirección no coincide correctamente
     * @throws Exception Si ocurre un error técnico
     */
    public ResultadoSolar calcularInstalacion(String direccion, String codigoPostalUsuario, double consumoAnual)
            throws DatosEntradaInvalidosException, DireccionNoCoincideException, Exception {

        validarDatosEntrada(direccion, codigoPostalUsuario, consumoAnual);

        ResultadoGeocoding geocoding = geocodingClient.obtenerResultadoGeocoding(direccion);

        if (!codigoPostalUsuario.trim().equals(geocoding.getCodigoPostal())) {
            throw new DireccionNoCoincideException(
                    "El código postal no coincide con la dirección. Código detectado: "
                            + geocoding.getCodigoPostal()
            );
        }

        return calcularInstalacion(geocoding.getLatitud(), geocoding.getLongitud(), consumoAnual);
    }

    /**
     * Calcula la instalación solar a partir de unas coordenadas y un consumo.
     *
     * @param lat Latitud
     * @param lng Longitud
     * @param consumoAnual Consumo anual en kWh
     * @return ResultadoSolar con todos los datos calculados
     * @throws DatosEntradaInvalidosException Si el consumo no es válido
     * @throws Exception Si ocurre un error técnico
     */
    public ResultadoSolar calcularInstalacion(double lat, double lng, double consumoAnual)
            throws DatosEntradaInvalidosException, Exception {

        validarConsumo(consumoAnual);

        JSONObject solarJson = solarClient.obtenerDatosSolares(lat, lng);

        if (!solarJson.has("solarPotential")) {
            throw new RuntimeException("La API Solar no devolvió información de potencial solar");
        }

        JSONObject solarPotential = solarJson.getJSONObject("solarPotential");

        double horasSol = solarPotential.optDouble("maxSunshineHoursPerYear", -1);
        double area = solarPotential.optDouble("maxArrayAreaMeters2", -1);
        int maxPaneles = solarPotential.has("maxArrayPanelsCount")
                ? solarPotential.getInt("maxArrayPanelsCount")
                : -1;

        if (horasSol <= 0 || area < 0 || maxPaneles < 0) {
            throw new RuntimeException("Los datos devueltos por la API Solar no son válidos");
        }

        double potenciaPanel = 0.4;
        double factor = 0.85;

        double energiaPorPanel = potenciaPanel * horasSol * factor;

        if (energiaPorPanel <= 0) {
            throw new RuntimeException("No se pudo calcular la energía generada por panel");
        }

        int panelesNecesarios = (int) Math.ceil(consumoAnual / energiaPorPanel);

        if (panelesNecesarios <= 0) {
            throw new RuntimeException("El número de paneles calculado no es válido");
        }

        double precioPorPanel = 250.0;
        double presupuesto = panelesNecesarios * precioPorPanel;

        ResultadoSolar resultado = new ResultadoSolar();
        resultado.setHorasSol(horasSol);
        resultado.setArea(area);
        resultado.setMaxPaneles(maxPaneles);
        resultado.setPanelesNecesarios(panelesNecesarios);
        resultado.setEnergiaPorPanel(energiaPorPanel);
        resultado.setPresupuesto(presupuesto);
        resultado.setAutosuficiente(panelesNecesarios <= maxPaneles);

        return resultado;
    }

    /**
     * Valida los datos de entrada necesarios para el cálculo.
     *
     * @param direccion Dirección completa
     * @param codigoPostalUsuario Código postal introducido por el usuario
     * @param consumoAnual Consumo anual
     * @throws DatosEntradaInvalidosException Si algún dato no es válido
     */
    private void validarDatosEntrada(String direccion, String codigoPostalUsuario, double consumoAnual)
            throws DatosEntradaInvalidosException {

        if (direccion == null || direccion.trim().isEmpty()) {
            throw new DatosEntradaInvalidosException("La dirección es obligatoria");
        }

        if (codigoPostalUsuario == null || codigoPostalUsuario.trim().isEmpty()) {
            throw new DatosEntradaInvalidosException("El código postal es obligatorio");
        }

        if (!codigoPostalUsuario.trim().matches("\\d{5}")) {
            throw new DatosEntradaInvalidosException("El código postal debe tener 5 dígitos");
        }

        validarConsumo(consumoAnual);
    }

    /**
     * Valida el consumo anual.
     *
     * @param consumoAnual Consumo anual
     * @throws DatosEntradaInvalidosException Si el consumo no es válido
     */
    private void validarConsumo(double consumoAnual) throws DatosEntradaInvalidosException {

        if (Double.isNaN(consumoAnual) || Double.isInfinite(consumoAnual)) {
            throw new DatosEntradaInvalidosException("El consumo anual no es válido");
        }

        if (consumoAnual <= 0) {
            throw new DatosEntradaInvalidosException("El consumo anual debe ser mayor que cero");
        }
    }
}