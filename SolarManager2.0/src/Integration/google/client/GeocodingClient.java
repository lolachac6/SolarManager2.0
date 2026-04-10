package Integration.google.client;

import Integration.google.util.HttpUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Cliente para consumir la API de Geocoding de Google.
 * Convierte direcciones en coordenadas geográficas (latitud y longitud).
 */
public class GeocodingClient {

    /**
     * Clave de API de Google.
     * ⚠️ En producción debería almacenarse en variables de entorno o fichero de configuración.
     */
    private static final String API_KEY = "AIzaSyCDnE9NLhGwrGfOLq2PINpi6YWR3HgirvI";

    /**
     * Obtiene las coordenadas geográficas de una dirección.
     *
     * @param direccion Dirección completa (calle, número, ciudad, CP, país)
     * @return Array con latitud y longitud [lat, lng]
     * @throws Exception Si ocurre un error en la llamada o la respuesta no es válida
     */
    public double[] obtenerCoordenadas(String direccion) throws Exception {

        // Codificar la dirección para soportar ñ, tildes, espacios, etc.
        String direccionCodificada = URLEncoder.encode(direccion, StandardCharsets.UTF_8.toString());

        // URL correcta de la API
        String url = "https://maps.googleapis.com/maps/api/geocode/json"
                + "?address=" + direccionCodificada
                + "&key=" + API_KEY;

        // Realizar petición HTTP
        String response = HttpUtils.get(url);
        
        System.out.println("--------" + response + "----------");

        // Validación básica: comprobar que devuelve JSON
        if (!response.trim().startsWith("{")) {
            throw new RuntimeException("Respuesta inválida de la API: " + response);
        }

        JSONObject json = new JSONObject(response);

        // Validar estado de la API
        String status = json.getString("status");
            System.out.println("---------------" + status +"-------------");
        if (!status.equals("OK")) {
            throw new RuntimeException("Error en Geocoding API: " + status);
        }

        JSONArray results = json.getJSONArray("results");

        if (results.length() == 0) {
            throw new RuntimeException("No se encontró la dirección");
        }

        // Obtener coordenadas
        JSONObject location = results.getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONObject("location");

        double lat = location.getDouble("lat");
        double lng = location.getDouble("lng");

        return new double[]{lat, lng};
    }
}