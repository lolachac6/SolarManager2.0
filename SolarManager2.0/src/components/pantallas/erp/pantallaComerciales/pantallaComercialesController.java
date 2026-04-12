package components.pantallas.erp.pantallaComerciales;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import components.pantallas.erp.pantallaAltaComercial.PantallaAltaComercialController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modelo.Comercial;
import modelo.Direccion;
import org.bson.Document;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

public class pantallaComercialesController implements Initializable {

    @FXML private TableView<Comercial> tablaComerciales;
    @FXML private TableColumn<Comercial, String> colId, colNombre, colActivo, colApellidos, colTelefono, colEmail, colDireccion, colTipoContrato, colDni, colNumeroCuenta, colCentroTrabajo, colObservaciones;
    @FXML private TextField txtFiltro;

    private ObservableList<Comercial> listaOriginal = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId() != null ? data.getValue().getId() : ""));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNumeroCuenta.setCellValueFactory(new PropertyValueFactory<>("numeroCuenta"));
        colCentroTrabajo.setCellValueFactory(new PropertyValueFactory<>("centroTrabajo"));
        colObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));
        
        colDireccion.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDireccion() != null ? data.getValue().getDireccion().toString() : ""));
        
        colTipoContrato.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getTipoContrato() != null ? data.getValue().getTipoContrato().toString() : ""));
        
        colActivo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getActivo() ? "Activo" : "No"));

        
        try { 
            obtenerComercialesTabla(); 
        } catch (IOException e) { 
        }

        
        txtFiltro.textProperty().addListener((obs, oldVal, newVal) -> buscarFiltro());
    }

    public void obtenerComercialesTabla() throws IOException {
        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<Document> coleccion = db.getCollection("Comerciales");
        listaOriginal.clear();

        for (Document doc : coleccion.find()) {
            Document dirDoc = doc.get("direccion", Document.class);
            Direccion direccion = (dirDoc != null) ? new Direccion(
                dirDoc.getString("calle"), dirDoc.getString("numero"),
                dirDoc.getString("codigoPostal"), dirDoc.getString("municipio"),
                dirDoc.getString("provincia")) : null;

            Comercial c = new Comercial();
            c.setId(doc.getObjectId("_id").toString());
            c.setSupabaseId(doc.getString("supabase_id")); 
            c.setNombre(doc.getString("nombre"));
            c.setApellidos(doc.getString("apellidos"));
            c.setTelefono(doc.getString("telefono"));
            c.setEmail(doc.getString("email"));
            c.setDireccion(direccion);
            c.setDni(doc.getString("dni"));
            c.setNumeroCuenta(doc.getString("numeroCuenta"));
            c.setCentroTrabajo(doc.getString("centroTrabajo"));
            c.setObservaciones(doc.getString("observaciones"));
            c.setActivo(doc.getBoolean("activo", true));

            String tipo = doc.getString("tipoContrato");
            if (tipo != null) {
                try {
                    c.setTipoContrato(Comercial.TipoContrato.valueOf(tipo));
                } catch (IllegalArgumentException e) {
                    System.err.println("Tipo de contrato no válido: " + tipo);
                }
            }
            listaOriginal.add(c);
        }
        tablaComerciales.setItems(listaOriginal);
    }

    @FXML
    public void modificarComercial(ActionEvent event) {
        Comercial seleccionado = tablaComerciales.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, selecciona un comercial de la tabla.", AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/pantallas/erp/pantallaAltaComercial/pantallaAltaComercial.fxml"));
            Parent root = loader.load();
            
            PantallaAltaComercialController controller = loader.getController();
            controller.cargarDatos(seleccionado); 

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la pantalla de edición: " + e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    public void buscarFiltro() {
        String texto = txtFiltro.getText().toLowerCase();
        if (texto == null || texto.isEmpty()) {
            tablaComerciales.setItems(listaOriginal);
            return;
        }

        ObservableList<Comercial> listaFiltrada = FXCollections.observableArrayList();
        for (Comercial c : listaOriginal) {
            if (c.getNombre().toLowerCase().contains(texto) || 
                c.getApellidos().toLowerCase().contains(texto) || 
                c.getEmail().toLowerCase().contains(texto) ||
                c.getDni().toLowerCase().contains(texto)) {
                listaFiltrada.add(c);
            }
        }
        tablaComerciales.setItems(listaFiltrada);
    }

    @FXML private void volverInicio(ActionEvent e) { 
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml"); 
    }
    
    @FXML private void anadirComercial(ActionEvent e) { 
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaAltaComercial/pantallaAltaComercial.fxml"); 
    }
    
    @FXML private void irClientes(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }

    @FXML private void irComerciales(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }

    @FXML private void irProveedores(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaProveedor/PantallaProveedor.fxml");
    }

    @FXML private void irStock(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }

    @FXML private void irPresupuestos(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }

    @FXML private void irInformes(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }

    private void cambiarPantalla(Node nodo, String ruta) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            mostrarAlerta("Error de navegación", "No se pudo cargar: " + ruta, AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Solar Manager");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}