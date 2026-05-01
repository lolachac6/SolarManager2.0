package components.tablaPresupuestos;

import ConexionSupabase.SessionManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.bson.Document;
import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONObject;


public class TablaPresupuestosController implements Initializable {

    @FXML private TableView<Document> tablaPresupuestos;

    @FXML private TableColumn<Document, String> colCliente;
    @FXML private TableColumn<Document, String> colFecha;
    @FXML private TableColumn<Document, String> colEstado;
    @FXML private TableColumn<Document, String> colTotal;

    private ObservableList<Document> lista;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
    } 

    private void configurarColumnas(){

        colCliente.setCellValueFactory(data -> {
            String idCliente = data.getValue().getString("idCliente");
            String nombreCompleto = obtenerNombreCliente(idCliente);
            return new SimpleStringProperty(nombreCompleto);
        });

        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("fechaCreacion"))));

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(valorSeguro(data.getValue().getString("estado"))));

        colTotal.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(data.getValue().get("total")) + " €"
                ));
    }

    private String obtenerNombreCliente(String idCliente) {

        if (idCliente == null || idCliente.isEmpty()) {
            return "";
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> clientes = db.getCollection("Clientes");

            Document cliente = clientes.find(
                    new Document("_id", new org.bson.types.ObjectId(idCliente))
            ).first();

            if (cliente != null) {
                String nombre = valorSeguro(cliente.getString("nombre"));
                String apellidos = valorSeguro(cliente.getString("apellidos"));
                return nombre + " " + apellidos;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

private void cargarDatos(){

    lista = FXCollections.observableArrayList();

    try {
        MongoDatabase db = MongoConnection.conectar();

        MongoCollection<Document> colPresupuestos = db.getCollection("Presupuestos");
        MongoCollection<Document> colClientes = db.getCollection("Clientes");
        MongoCollection<Document> colComerciales = db.getCollection("Comerciales");

        System.out.println("🔵 Cargando presupuestos filtrados");

        // 🔹 Usuario Supabase
        JSONObject usuario = SessionManager.getUsuario();
        String supabaseId = usuario.getString("id");

        System.out.println("🟡 Supabase ID: " + supabaseId);

        // 🔹 Buscar comercial en Mongo
        Document comercial = colComerciales.find(eq("supabase_id", supabaseId)).first();

        if (comercial == null) {
            System.out.println("🔴 Comercial no encontrado en Mongo");
            tablaPresupuestos.setItems(lista);
            return;
        }

        String idComercial = comercial.getObjectId("_id").toHexString();

        System.out.println("🟡 ID Comercial Mongo: " + idComercial);

        // 🔹 Clientes del comercial
        List<String> idsClientes = new ArrayList<>();

        for (Document cliente : colClientes.find(eq("idComercialAsignado", idComercial))) {

            String idCliente = cliente.getObjectId("_id").toHexString();
            idsClientes.add(idCliente);

            System.out.println("🟢 Cliente: " + idCliente);
        }

        System.out.println("🟡 Total clientes: " + idsClientes.size());

        if (idsClientes.isEmpty()) {
            System.out.println("🔴 Sin clientes → no hay presupuestos");
            tablaPresupuestos.setItems(lista);
            return;
        }

        // 🔹 Presupuestos de esos clientes
        for (Document doc : colPresupuestos.find(in("idCliente", idsClientes))) {

            System.out.println("🟣 Presupuesto: " + doc);

            lista.add(doc);
        }

        System.out.println("🟢 Total presupuestos: " + lista.size());

    } catch (IOException ex) {
        Logger.getLogger(TablaPresupuestosController.class.getName())
              .log(Level.SEVERE, null, ex);
    }

    tablaPresupuestos.setItems(lista);
}

    /**
     * Devuelve el presupuesto seleccionado en la tabla.
     *
     * @return documento del presupuesto seleccionado o null si no hay selección
     */
    public Document getPresupuestoSeleccionado() {
        return tablaPresupuestos.getSelectionModel().getSelectedItem();
    }

    private String valorSeguro(String valor) {
        return valor != null ? valor : "";
    }
}