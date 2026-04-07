package components.tablaPresupuestos;

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

public class TablaPresupuestosController implements Initializable {

    @FXML private TableView<Document> tablaPresupuestos;

    @FXML private TableColumn<Document, String> colId;
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

    private void configurarColumnas() {

        colId.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getObjectId("_id").toString()));

        colCliente.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getString("idCliente")));

        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getString("fecha")));

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getString("estado")));

        colTotal.setCellValueFactory(data -> {
            Object total = data.getValue().get("total");
            return new SimpleStringProperty(total != null ? total.toString() + " €" : "");
        });
    }

    private void cargarDatos() {

        lista = FXCollections.observableArrayList();

        try {

            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> col = db.getCollection("Presupuestos");

            for (Document doc : col.find()) {
                lista.add(doc);
            }

        } catch (IOException ex) {
            Logger.getLogger(TablaPresupuestosController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tablaPresupuestos.setItems(lista);
    }
}