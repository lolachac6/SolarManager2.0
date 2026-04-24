package components.tablaPresupuestos;

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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import modelo.Presupuesto;
import modelo.Cliente;

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

        // 👇 CLIENTE (igual que instalaciones)
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

    //  MISMO MÉTODO QUE INSTALACIONES
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

        try{
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> col = db.getCollection("Presupuestos");

            for(Document doc : col.find()){
                lista.add(doc);
                System.out.println(lista);
            }

        } catch(IOException ex){
            Logger.getLogger(TablaPresupuestosController.class.getName())
                  .log(Level.SEVERE, null, ex);
        }

        tablaPresupuestos.setItems(lista);
    }

    private String valorSeguro(String valor) {
        return valor != null ? valor : "";
    }
}