package ConexionSupabase;

import java.io.InputStream;
import java.util.Properties;

public class config {

    private static Properties properties = new Properties();

    static {
        try (InputStream input = config.class
                .getClassLoader()
                .getResourceAsStream("config/config.properties")) {

            if (input == null) {
                throw new RuntimeException("No se encontró config.properties");
            }

            properties.load(input);

        } catch (Exception e) {
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}