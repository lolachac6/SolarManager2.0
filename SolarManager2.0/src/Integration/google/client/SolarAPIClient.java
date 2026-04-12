package Integration.google.client;

import Integration.google.util.HttpUtils;
import org.json.JSONObject;

/**
 * Cliente para consumir la API Solar de Google.
 * Obtiene información sobre el potencial solar de una ubicación.
 */
public class SolarAPIClient {

    /**
     * Clave de API de Google.
     */
    private static final String API_KEY = "AIzaSyCDnE9NLhGwrGfOLq2PINpi6YWR3HgirvI";

    /**
     * Obtiene los datos solares de una ubicación.
     * Intenta consultar la API con distintos niveles de calidad hasta encontrar
     * uno disponible para la localización indicada.
     *
     * @param lat Latitud
     * @param lng Longitud
     * @return JSONObject con datos solares
     * @throws Exception Si ocurre un error y no se puede obtener información solar
     */
    public JSONObject obtenerDatosSolares(double lat, double lng) throws Exception {

        String[] calidades = {"HIGH", "MEDIUM", "BASE"};

        for (String calidad : calidades) {
            try {
                return obtenerDatosSolares(lat, lng, calidad);
            } catch (RuntimeException e) {
                String mensaje = e.getMessage();
                if (mensaje == null || !mensaje.contains("Error HTTP 404")) {
                    throw e;
                }
            }
        }

        throw new RuntimeException("No se encontraron datos solares para la ubicación indicada");
    }

    /**
     * Obtiene los datos solares de una ubicación con una calidad concreta.
     *
     * @param lat Latitud
     * @param lng Longitud
     * @param calidad Calidad mínima requerida
     * @return JSONObject con datos solares
     * @throws Exception Si ocurre un error en la petición
     */
    private JSONObject obtenerDatosSolares(double lat, double lng, String calidad) throws Exception {

        String url = "https://solar.googleapis.com/v1/buildingInsights:findClosest"
                + "?location.latitude=" + lat
                + "&location.longitude=" + lng
                + "&requiredQuality=" + calidad
                + "&key=" + API_KEY;

        System.out.println("Llamando a Solar API con calidad: " + calidad);
        System.out.println("URL Solar API: " + url);

        String response = HttpUtils.get(url);

        System.out.println("Respuesta Solar API:");
        System.out.println(response);

        if (response == null || response.trim().isEmpty() || !response.trim().startsWith("{")) {
            throw new RuntimeException("Respuesta inválida de la API Solar");
        }

        JSONObject json = new JSONObject(response);

        if (json.has("error")) {
            JSONObject error = json.getJSONObject("error");
            String message = error.optString("message", "Error desconocido en Solar API");
            throw new RuntimeException(message);
        }

        return json;
    }
}