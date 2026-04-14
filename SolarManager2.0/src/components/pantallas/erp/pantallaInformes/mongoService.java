package components.pantallas.erp.pantallaInformes;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio centralizado para operaciones relacionadas con MongoDB
 * dentro del módulo de informes.
 *
 * Totalmente integrado con MongoConnection y la base de datos SolarManager.
 */
public class mongoService {

    /**
     * Obtiene una conexión activa a la base de datos MongoDB.
     *
     * @return instancia de MongoDatabase o null si ocurre un error
     */
    public static MongoDatabase getConexion() {
        try {
            return MongoConnection.conectar();
        } catch (Exception e) {
            System.err.println("Error conectando a MongoDB: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene una lista de nombres de comerciales desde la colección "Comerciales".
     *
     * @return lista de nombres de comerciales
     */
    public static List<String> obtenerNombresComerciales() {

        List<String> nombres = new ArrayList<>();

        try {
            MongoDatabase db = getConexion();
            if (db == null) return nombres;

            MongoCollection<Document> col = db.getCollection("Comerciales");

            col.distinct("nombre", String.class).into(nombres);

            // Limpiar valores inválidos
            nombres.removeIf(s -> s == null || s.trim().isEmpty());

        } catch (Exception e) {
            System.err.println("Error obteniendo comerciales: " + e.getMessage());
        }

        return nombres;
    }
}

