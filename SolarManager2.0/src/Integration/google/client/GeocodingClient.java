package Integration.google.client;

import Integration.google.exception.DireccionNoCoincideException;
import Integration.google.model.ResultadoGeocoding;
import Integration.google.util.HttpUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Cliente para consumir la API de Geocoding de Google.
 * Convierte direcciones en coordenadas geográficas y recupera
 * los componentes principales de la dirección.
 */
public class GeocodingClient {

    /**
     * Clave de API de Google.
     */
    private static final String API_KEY = "AIzaSyCDnE9NLhGwrGfOLq2PINpi6YWR3HgirvI";

    /**
     * Obtiene el resultado completo de geocodificación de una dirección.
     *
     * @param direccion Dirección completa
     * @return ResultadoGeocoding con coordenadas y componentes de dirección
     * @throws DireccionNoCoincideException Si la dirección no tiene coincidencia válida
     * @throws Exception Si ocurre un error técnico
     */
    public ResultadoGeocoding obtenerResultadoGeocoding(String direccion) throws Exception {

        if (direccion == null || direccion.trim().isEmpty()) {
            throw new DireccionNoCoincideException("No hay coincidencia en la dirección");
        }

        String direccionCodificada = URLEncoder.encode(direccion.trim(), StandardCharsets.UTF_8.toString());

        String url = "https://maps.googleapis.com/maps/api/geocode/json"
                + "?address=" + direccionCodificada
                + "&key=" + API_KEY;

        System.out.println("URL Geocoding API: " + url);

        String response = HttpUtils.get(url);

        System.out.println("Respuesta Geocoding API:");
        System.out.println(response);

        if (response == null || response.trim().isEmpty() || !response.trim().startsWith("{")) {
            throw new RuntimeException("Respuesta inválida de la API de Geocoding");
        }

        JSONObject json = new JSONObject(response);
        String status = json.optString("status", "");

        System.out.println("Status Geocoding API: " + status);

        if (!"OK".equals(status)) {
            if ("ZERO_RESULTS".equals(status)) {
                throw new DireccionNoCoincideException("No hay coincidencia en la dirección");
            }
            throw new RuntimeException("Error en Geocoding API: " + status);
        }

        JSONArray results = json.optJSONArray("results");

        if (results == null || results.length() == 0) {
            throw new DireccionNoCoincideException("No hay coincidencia en la dirección");
        }

        JSONObject primerResultado = results.optJSONObject(0);

        if (primerResultado == null) {
            throw new DireccionNoCoincideException("No hay coincidencia en la dirección");
        }

        boolean partialMatch = primerResultado.optBoolean("partial_match", false);

        if (partialMatch) {
            throw new DireccionNoCoincideException("No hay coincidencia en la dirección");
        }

        JSONObject geometry = primerResultado.optJSONObject("geometry");
        if (geometry == null) {
            throw new RuntimeException("La respuesta de Geocoding no contiene geometry");
        }

        JSONObject location = geometry.optJSONObject("location");
        if (location == null) {
            throw new RuntimeException("La respuesta de Geocoding no contiene location");
        }

        if (!location.has("lat") || !location.has("lng")) {
            throw new RuntimeException("La respuesta de Geocoding no contiene coordenadas válidas");
        }

        double lat = location.getDouble("lat");
        double lng = location.getDouble("lng");

        String calle = obtenerComponente(primerResultado, "route");
        String numero = obtenerComponente(primerResultado, "street_number");
        String ciudad = obtenerComponente(primerResultado, "locality");

        if (ciudad.isEmpty()) {
            ciudad = obtenerComponente(primerResultado, "postal_town");
        }

        String provincia = obtenerComponente(primerResultado, "administrative_area_level_2");
        if (provincia.isEmpty()) {
            provincia = obtenerComponente(primerResultado, "administrative_area_level_1");
        }

        String codigoPostal = obtenerComponente(primerResultado, "postal_code");

        return new ResultadoGeocoding(lat, lng, calle, numero, ciudad, provincia, codigoPostal);
    }

    /**
     * Obtiene un componente de dirección del resultado de Geocoding.
     *
     * @param resultado Resultado principal
     * @param tipoBuscado Tipo de componente a buscar
     * @return Valor del componente o cadena vacía
     */
    private String obtenerComponente(JSONObject resultado, String tipoBuscado) {

        JSONArray componentes = resultado.optJSONArray("address_components");

        if (componentes == null) {
            return "";
        }

        for (int i = 0; i < componentes.length(); i++) {
            JSONObject componente = componentes.optJSONObject(i);

            if (componente == null) {
                continue;
            }

            JSONArray tipos = componente.optJSONArray("types");

            if (tipos == null) {
                continue;
            }

            for (int j = 0; j < tipos.length(); j++) {
                if (tipoBuscado.equals(tipos.optString(j))) {
                    return componente.optString("long_name", "").trim();
                }
            }
        }

        return "";
    }
}