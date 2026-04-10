package Integration.google.service;

import Integration.google.client.GeocodingClient;
import Integration.google.client.SolarAPIClient;
import modelo.ResultadoSolar;
import org.json.JSONObject;

/**
 * Servicio encargado de la lógica de negocio del cálculo solar.
 */
public class SolarService {

    private GeocodingClient geocodingClient = new GeocodingClient();
    private SolarAPIClient solarClient = new SolarAPIClient();

    /**
     * Calcula la instalación solar a partir de dirección y consumo.
     *
     * @param direccion Dirección completa
     * @param consumoAnual Consumo en kWh/año
     * @return ResultadoSolar con todos los datos calculados
     */
    public ResultadoSolar calcularInstalacion(String direccion, double consumoAnual) {

        ResultadoSolar resultado = new ResultadoSolar();

        try {

            double[] coords = geocodingClient.obtenerCoordenadas(direccion);
            double lat = coords[0];
            double lng = coords[1];

            JSONObject solarJson = solarClient.obtenerDatosSolares(lat, lng);
            JSONObject solarPotential = solarJson.getJSONObject("solarPotential");

            double horasSol = solarPotential.getDouble("maxSunshineHoursPerYear");
            double area = solarPotential.getDouble("maxArrayAreaMeters2");
            int maxPaneles = (int) solarPotential.getDouble("maxArrayPanelsCount");

            double potenciaPanel = 0.4;
            double factor = 0.85;

            double energiaPorPanel = potenciaPanel * horasSol * factor;
            int panelesNecesarios = (int) Math.ceil(consumoAnual / energiaPorPanel);

            double precioPorPanel = 250;
            double presupuesto = panelesNecesarios * precioPorPanel;

            resultado.setHorasSol(horasSol);
            resultado.setArea(area);
            resultado.setMaxPaneles(maxPaneles);
            resultado.setPanelesNecesarios(panelesNecesarios);
            resultado.setEnergiaPorPanel(energiaPorPanel);
            resultado.setPresupuesto(presupuesto);
            resultado.setAutosuficiente(panelesNecesarios <= maxPaneles);

        } catch (Exception e) {
            throw new RuntimeException("Error en cálculo solar: " + e.getMessage());
        }

        return resultado;
    }
}