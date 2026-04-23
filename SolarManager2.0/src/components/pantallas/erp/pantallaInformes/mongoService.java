package components.pantallas.erp.pantallaInformes;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import modelo.Comercial;

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
    public List<Comercial> obtenerComerciales() {

        MongoCollection<Document> col = database.getCollection("Comerciales");

        List<Comercial> lista = new ArrayList<>();

        for (Document doc : col.find()) {

            Comercial c = new Comercial();
            c.setId(doc.getObjectId("_id").toString());
            c.setNombre(doc.getString("nombre"));
            c.setApellidos(doc.getString("apellidos"));
            c.setEmail(doc.getString("email"));
            c.setTelefono(doc.getString("telefono"));
            c.setDni(doc.getString("dni"));
            c.setNumeroCuenta(doc.getString("numeroCuenta"));
            c.setCentroTrabajo(doc.getString("centroTrabajo"));
            c.setObservaciones(doc.getString("observaciones"));
            c.setActivo(doc.getBoolean("activo", true));
            c.setSupabaseId(doc.getString("supabaseId"));

            

            lista.add(c);
        }

        return lista;
    }

}

