package APIGoogleSolar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class ConexionAPIGS {

    public static void main(String[] args) {

        try {
            
            // Datos de entrada
            String calle = "Calle de Alcalá";
            String numero = "50";
            String ciudad = "ghfghfgh";
            String codigoPostal = "jghjhj";
            String pais = "España";
            double consumoAnual = 4500; // kWh/año (consumo de la vivienda)
            double potenciaPanel = 0.4; // kW por panel (400 W)

            String direccion = calle + " " + numero + ", " + ciudad + ", " + codigoPostal + ", " + pais;

            String apiKey = "AIzaSyCDnE9NLhGwrGfOLq2PINpi6YWR3HgirvI";

           
            // Convertir la dirección a coordenadas usando Geocoding API
            String geoUrl = "https://maps.googleapis.com/maps/api/geocode/json"
                    + "?address=" + direccion.replace(" ", "+")
                    + "&key=" + apiKey;

            JSONObject geoJson = new JSONObject(hacerPeticion(geoUrl));
            JSONArray results = geoJson.getJSONArray("results");

            if (results.length() == 0) {
                System.out.println("No se encontró la dirección");
                return;
            }

            JSONObject location = results.getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location");

            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");

           
            // Consulta a Solar API
            String solarUrl = "https://solar.googleapis.com/v1/buildingInsights:findClosest"
                    + "?location.latitude=" + lat
                    + "&location.longitude=" + lng
                    + "&requiredQuality=HIGH"
                    + "&key=" + apiKey;

            JSONObject solarJson = new JSONObject(hacerPeticion(solarUrl));

            JSONObject solarPotential = solarJson.getJSONObject("solarPotential");
            double maxPanels = solarPotential.getDouble("maxArrayPanelsCount");
            double maxArea = solarPotential.getDouble("maxArrayAreaMeters2");
            double horasSol = solarPotential.getDouble("maxSunshineHoursPerYear");

          
            // Calcular paneles solares necesario para el autoconsumo
            double factorOrientacion = 0.85; // simplificación para eficiencia según orientación y pendiente
            double energiaPorPanel = potenciaPanel * horasSol * factorOrientacion; // kWh/año
            int panelesNecesarios = (int) Math.ceil(consumoAnual / energiaPorPanel);

    
            // Resumen de los datos que devuelve Solar API
            System.out.println("==== RESUMEN SOLAR AUTOSUFICIENCIA ====");
            System.out.println("Dirección: " + direccion);
            System.out.println("Latitud: " + lat + ", Longitud: " + lng);
            System.out.println("Código postal: " + solarJson.optString("postalCode", "N/A"));
            System.out.println("Provincia: " + solarJson.optString("administrativeArea", "N/A"));
            System.out.println("Consumo anual de la casa: " + consumoAnual + " kWh");
            System.out.println("Horas de sol estimadas: " + Math.round(horasSol) + " h/año");
            System.out.println("Área disponible en tejado: " + Math.round(maxArea) + " m²");
            System.out.println("Máximo de paneles que caben: " + (int) maxPanels);
            System.out.println("Potencia por panel: " + potenciaPanel + " kW");

            System.out.println("\n--- RESULTADO CÁLCULO AUTOSUFICIENCIA ---");
            System.out.println("Energía estimada por panel al año: " + Math.round(energiaPorPanel) + " kWh");
            System.out.println("Paneles necesarios para cubrir consumo: " + panelesNecesarios);

            if (panelesNecesarios <= maxPanels) {
                System.out.println("✅ La instalación puede hacer la casa autosuficiente");
            } else {
                System.out.println("⚠️ No hay suficiente espacio en el tejado para cubrir el consumo");
                System.out.println("Se puede instalar máximo " + (int) maxPanels + " paneles");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  
    // Método para hacer peticiones HTTP
    public static String hacerPeticion(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        conn.getResponseCode() >= 200 && conn.getResponseCode() < 300 ?
                                conn.getInputStream() :
                                conn.getErrorStream()
                )
        );

        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) response.append(line);
        reader.close();

        return response.toString();
    }
}


