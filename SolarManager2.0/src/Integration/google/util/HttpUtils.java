package Integration.google.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Clase utilitaria para realizar peticiones HTTP.
 * Permite reutilizar la lógica de conexión para diferentes APIs.
 */
public class HttpUtils {

    /**
     * Realiza una petición HTTP GET a la URL especificada.
     *
     * @param urlString URL completa de la petición
     * @return Respuesta en formato String
     * @throws Exception Si ocurre un error de conexión
     */
    public static String get(String urlString) throws Exception {

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);

        int responseCode = conn.getResponseCode();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        responseCode >= 200 && responseCode < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream()
                )
        );

        String line;
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        if (responseCode < 200 || responseCode >= 300) {
            throw new RuntimeException("Error HTTP " + responseCode + ": " + response.toString());
        }

        return response.toString();
    }
}