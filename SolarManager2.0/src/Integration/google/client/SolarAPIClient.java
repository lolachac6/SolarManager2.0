package Integration.google.client;

import Integration.google.util.HttpUtils;
import org.json.JSONObject;

/**
 * Cliente para consumir la API Solar de Google.
 * Obtiene información sobre el potencial solar de una ubicación.
 */
public class SolarAPIClient {

    private static final String API_KEY = "AIzaSyCDnE9NLhGwrGfOLq2PINpi6YWR3HgirvI";

    /**
     * Obtiene los datos solares de una ubicación.
     *
     * @param lat Latitud
     * @param lng Longitud
     * @return JSONObject con datos solares
     * @throws Exception Si ocurre un error en la petición
     */
    public JSONObject obtenerDatosSolares(double lat, double lng) throws Exception {

        String url = "https://solar.googleapis.com/v1/buildingInsights:findClosest"
                + "?location.latitude=" + lat
                + "&location.longitude=" + lng
                + "&requiredQuality=HIGH"
                + "&key=" + API_KEY;

        String response = HttpUtils.get(url);

        return new JSONObject(response);
    }
}