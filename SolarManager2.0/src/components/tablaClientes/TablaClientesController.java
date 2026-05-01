package components.tablaClientes;

import ConexionSupabase.SessionManager;
import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.beans.property.SimpleStringProperty;

import org.bson.Document;

import java.io.IOException;
import org.json.JSONObject;

public class TablaClientesController implements Initializable {

    @FXML
    private TableView<Document> tablaClientes;

    @FXML
    private TableColumn<Document, String> colNombre;

    @FXML
    private TableColumn<Document, String> colApellidos;

    @FXML
    private TableColumn<Document, String> colEmail;

    @FXML
    private TableColumn<Document, String> colTelefono;

    private ObservableList<Document> lista;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {

        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(valorSeguro(data.getValue().getString("nombre"))));

        colApellidos.setCellValueFactory(data ->
                new SimpleStringProperty(valorSeguro(data.getValue().getString("apellidos"))));

        colEmail.setCellValueFactory(data ->
                new SimpleStringProperty(valorSeguro(data.getValue().getString("email"))));

        colTelefono.setCellValueFactory(data ->
                new SimpleStringProperty(valorSeguro(data.getValue().getString("telefono"))));
    }

     private void cargarDatos() {

        lista = FXCollections.observableArrayList();

        try {
            JSONObject usuario = SessionManager.getUsuario();
            String idSupabase = usuario.getString("id");

            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> comerciales = db.getCollection("Comerciales");

            Document comercial = comerciales.find(eq("supabase_id", idSupabase)).first();

            if (comercial != null) {

                String idMongoComercial = comercial.getObjectId("_id").toString();

                MongoCollection<Document> clientes = db.getCollection("Clientes");

                for (Document doc : clientes.find(eq("idComercialAsignado", idMongoComercial))) {
                    lista.add(doc);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(TablaClientesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tablaClientes.setItems(lista);
    }

    private String valorSeguro(String valor) {
        return valor != null ? valor : "";
    }

    public Document getClienteSeleccionado() {
        return tablaClientes.getSelectionModel().getSelectedItem();
    }
}