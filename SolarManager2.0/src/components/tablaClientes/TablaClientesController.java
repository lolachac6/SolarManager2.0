package components.tablaClientes;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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

public class TablaClientesController implements Initializable {

    @FXML private TableView<Document> tablaClientes;

    @FXML private TableColumn<Document, String> colNombre;
    @FXML private TableColumn<Document, String> colApellidos;
    @FXML private TableColumn<Document, String> colEmail;
    @FXML private TableColumn<Document, String> colTelefono;

    private ObservableList<Document> lista;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {

        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getString("nombre")));

        colApellidos.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getString("apellidos")));

        colEmail.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getString("email")));

        colTelefono.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getString("telefono")));
    }

    private void cargarDatos() {

        lista = FXCollections.observableArrayList();

        try {

            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> col = db.getCollection("Clientes");

            for (Document doc : col.find()) {
                lista.add(doc);
            }

        } catch (IOException ex) {
            Logger.getLogger(TablaClientesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tablaClientes.setItems(lista);
    }
}