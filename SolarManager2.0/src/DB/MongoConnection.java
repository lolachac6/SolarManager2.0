package DB;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Properties;
import javax.net.ssl.*;

public class MongoConnection {

    private static MongoDatabase database;

    public static MongoDatabase conectar() throws FileNotFoundException, IOException {

        if (database == null) {
            try {
                 
                Properties props = new Properties();
                props.load(MongoConnection.class.getResourceAsStream("config.properties"));
                String user = props.getProperty("MONGO_USER");
                String pass = props.getProperty("MONGO_PASS");
                String dbName = props.getProperty("MONGO_DB");
                
                String uri = "mongodb+srv://" + user + ":" + pass + "@cluster0.wnrz0ak.mongodb.net/" + dbName + "?retryWrites=true&w=majority";
                 
                TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    }
                };

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new SecureRandom());

                MongoClientSettings settings = MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(uri))
                        .applyToSslSettings(builder -> builder.enabled(true).context(sslContext))
                        .build();

                MongoClient client = MongoClients.create(settings);
                database = client.getDatabase(dbName);

                System.out.println("Conectado a MongoDB con SSL");

            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                System.out.println("Error conexión: " + e.getMessage());
            }
        }

        return database;
    }
}