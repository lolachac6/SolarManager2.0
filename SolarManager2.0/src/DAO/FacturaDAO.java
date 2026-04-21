package dao;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import modelo.Factura;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

/**
 * DAO encargado de la persistencia de facturas en MongoDB.
 *
 * @author ivang
 */
public class FacturaDAO {

    /**
     * Guarda una factura en la colección Facturas.
     *
     * @param factura factura a guardar
     * @return true si se guarda correctamente
     */
    public boolean guardarFactura(Factura factura) throws IOException {
        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<Document> coleccion = db.getCollection("Facturas");

        Document documento = new Document()
                .append("numeroFactura", factura.getNumeroFactura())
                .append("idPresupuesto", factura.getIdPresupuesto())
                .append("fechaEmision", factura.getFechaEmision() != null ? factura.getFechaEmision().toString() : null)
                .append("baseImponible", factura.getBaseImponible())
                .append("iva", factura.getIva())
                .append("total", factura.getTotal())
                .append("nombreArchivo", factura.getNombreArchivo())
                .append("pdfFactura", factura.getPdfFactura() != null ? new Binary(factura.getPdfFactura()) : null);

        coleccion.insertOne(documento);

        ObjectId idInsertado = documento.getObjectId("_id");
        if (idInsertado != null) {
            factura.setId(idInsertado.toHexString());
        }

        return true;
    }
    /**
 * Recupera una factura por idPresupuesto.
 */
public Factura obtenerFacturaPorPresupuesto(String idPresupuesto) throws IOException {

    MongoDatabase db = MongoConnection.conectar();
    MongoCollection<Document> coleccion = db.getCollection("Facturas");

    Document doc = coleccion.find(new Document("idPresupuesto", idPresupuesto)).first();

    if (doc == null) return null;

    Factura factura = new Factura();

    factura.setNumeroFactura(doc.getString("numeroFactura"));
    factura.setIdPresupuesto(doc.getString("idPresupuesto"));
    factura.setNombreArchivo(doc.getString("nombreArchivo"));

    Object pdfObj = doc.get("pdfFactura");

    if (pdfObj instanceof org.bson.types.Binary) {
        factura.setPdfFactura(((org.bson.types.Binary) pdfObj).getData());
    }

    return factura;
}
}