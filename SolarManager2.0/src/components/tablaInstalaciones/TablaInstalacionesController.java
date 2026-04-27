package components.tablaInstalaciones;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import modelo.InstalacionFotovoltaica;
import modelo.Direccion;
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
        
        colBateria.setCellValueFactory(data->
                new SimpleStringProperty(String.valueOf(data.getValue().get("bateria"))));
        
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

    /**
     * Devuelve la instalación seleccionada en la tabla.
     *
     * @return instalación seleccionada o null si no hay selección
     */
    public InstalacionFotovoltaica getInstalacionSeleccionada() {
        Document doc = tablaInstalaciones.getSelectionModel().getSelectedItem();

        if (doc == null) {
            return null;
        }

        InstalacionFotovoltaica instalacion = new InstalacionFotovoltaica();

        if (doc.getObjectId("_id") != null) {
            instalacion.setId(doc.getObjectId("_id").toHexString());
        }

        instalacion.setIdCliente(doc.getString("idCliente"));
        instalacion.setPotenciaInstalada(obtenerDouble(doc, "potenciaInstalada"));
        instalacion.setNumeroPaneles(obtenerEntero(doc, "numeroPaneles"));
        instalacion.setProduccionEstimada(obtenerDouble(doc, "produccionEstimada"));
        instalacion.setAhorroEstimado(obtenerDouble(doc, "ahorroEstimado"));
        instalacion.setInversor(doc.getString("inversor"));
        instalacion.setBateria(obtenerBoolean(doc, "bateria"));

        Document dir = doc.get("direccion", Document.class);

        if (dir != null) {
            instalacion.setDireccion(new Direccion(
                    dir.getString("calle"),
                    dir.getString("numero"),
                    dir.getString("codigoPostal"),
                    dir.getString("municipio"),
                    dir.getString("provincia")
            ));
        }

        return instalacion;
    }

    /**
     * Devuelve el nombre visible del cliente asociado a la instalación seleccionada.
     *
     * @return nombre completo del cliente
     */
    public String getNombreClienteSeleccionado() {
        Document doc = tablaInstalaciones.getSelectionModel().getSelectedItem();

        if (doc == null) {
            return "";
        }

        return obtenerNombreCliente(doc.getString("idCliente"));
    }

    private double obtenerDouble(Document doc, String campo) {
        Number numero = doc.get(campo, Number.class);
        return numero != null ? numero.doubleValue() : 0.0;
    }

    private int obtenerEntero(Document doc, String campo) {
        Number numero = doc.get(campo, Number.class);
        return numero != null ? numero.intValue() : 0;
    }

    private boolean obtenerBoolean(Document doc, String campo) {
        Object valor = doc.get(campo);

        if (valor instanceof Boolean) {
            return (Boolean) valor;
        }

        if (valor != null) {
            return Boolean.parseBoolean(String.valueOf(valor));
        }

        return false;
    }
    
    private String valorSeguro(String valor) {
        return valor != null ? valor : "";
    }
}