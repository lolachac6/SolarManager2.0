
package components.tablaInstalaciones;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import modelo.InstalacionFotovoltaica;
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



public class TablaInstalacionesController implements Initializable {

    
    @FXML private TableView<Document> tablaInstalaciones;
    @FXML private TableColumn<Document, String> colCliente;
    @FXML private TableColumn<Document, String> colPotencia;
    @FXML private TableColumn<Document, String> colPaneles;
    @FXML private TableColumn<Document, String> colProduccion;
    @FXML private TableColumn<Document, String> colAhorro;
    @FXML private TableColumn<Document, String> colInversor;
    @FXML private TableColumn<Document, String> colBateria;
    
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
        
        colPotencia.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("potenciaInstalada"))));
        
        colPaneles.setCellValueFactory(data->
                new SimpleStringProperty(String.valueOf(data.getValue().get("numeroPaneles"))));
        
        colProduccion.setCellValueFactory(data->
                new SimpleStringProperty(String.valueOf(data.getValue().get("produccionEstimada"))));
        
        colAhorro.setCellValueFactory(data->
                new SimpleStringProperty(String.valueOf(data.getValue().get("ahorroEstimado"))));
        
        colInversor.setCellValueFactory(data->
                new SimpleStringProperty(String.valueOf(data.getValue().get("inversor"))));
        
        colBateria.setCellValueFactory(data -> {Object valor = data.getValue().get("bateria");

            if (valor instanceof Boolean) {
                boolean tieneBateria = (Boolean) valor;
                return new SimpleStringProperty(tieneBateria ? "Sí" : "No");
            }

            return new SimpleStringProperty("");
        });
        
    }
    
    private String obtenerNombreCliente(String idCliente) {

    if (idCliente == null || idCliente.isEmpty()) {
        return "";
    }

    try {
        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<Document> clientes = db.getCollection("Clientes");

        Document cliente = clientes.find(new Document("_id", new org.bson.types.ObjectId(idCliente))).first();

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
            MongoCollection<Document> col = db.getCollection("Instalaciones");
            
            
            
            
            for(Document doc :col.find()){
                lista.add(doc);
                System.out.println(lista);
            }
        }catch(IOException ex){
            Logger.getLogger(TablaInstalacionesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        tablaInstalaciones.setItems(lista);
    }
    
    private String valorSeguro(String valor) {
        return valor != null ? valor : "";
    }
    
        
}
