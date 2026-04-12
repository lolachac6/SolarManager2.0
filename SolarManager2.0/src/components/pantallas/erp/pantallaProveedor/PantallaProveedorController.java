package components.pantallas.erp.pantallaProveedor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.bson.Document;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Direccion;
import modelo.Proveedor;
import components.pantallas.erp.pantallaAltaProveedor.PantallaAltaProveedorController;
import javafx.scene.control.Alert;

public class PantallaProveedorController implements Initializable {

    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, String> colId;
    @FXML private TableColumn<Proveedor, String> colNombre;
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private TableColumn<Proveedor, String> colEmail;
    @FXML private TableColumn<Proveedor, String> colDireccion;

    @FXML private TextField txtFiltro;

    private ObservableList<Proveedor> listaOriginal = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colId.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getId() != null ? data.getValue().getId() : ""
            )
        );

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        colDireccion.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getDireccion() != null
                        ? data.getValue().getDireccion().toString()
                        : ""
            )
        );


        try {
            obtenerProveedoresTabla();
        } catch (IOException e) {
            e.printStackTrace();
        }

        txtFiltro.textProperty().addListener((obs, oldVal, newVal) -> buscarFiltro());
    }

    // =========================
    // MÉTODO GENERAL DE NAVEGACIÓN
    // =========================
    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   
    @FXML
    private void abrirAltaProveedor(javafx.event.ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/components/pantallas/erp/pantallaAltaProveedor/pantallaAltaProveedor.fxml")
            );

            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
        @FXML
    private void detalle(ActionEvent e) {

        Proveedor seleccionado = tablaProveedores.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            System.out.println("Selecciona un proveedor");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/components/pantallas/erp/pantallaAltaProveedor/pantallaAltaProveedor.fxml")
            );

            Parent root = loader.load();

            PantallaAltaProveedorController controller = loader.getController();
            controller.cargarProveedor(seleccionado);

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
@FXML
private void actualizar(ActionEvent e) {
    try {
        // Limpiamos el filtro
        txtFiltro.clear();

        // Recargamos los datos desde Mongo
        obtenerProveedoresTabla();

        // Refrescamos la tabla
        tablaProveedores.refresh();

        // Alerta de confirmación
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Actualización");
        alerta.setHeaderText(null);
        alerta.setContentText("La lista de proveedores se ha actualizado correctamente.");
        alerta.showAndWait();

    } catch (IOException ex) {
        ex.printStackTrace();

        // Alerta de error (opcional pero recomendable)
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error");
        error.setHeaderText("Error al actualizar");
        error.setContentText("No se han podido cargar los proveedores desde la base de datos.");
        error.showAndWait();
    }
}




   
@FXML
    private void eliminarProveedor() {
        Proveedor seleccionado = tablaProveedores.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un Proveedor de la tabla para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar Proveedor");
        confirmacion.setContentText("¿Seguro que deseas eliminar el proveedor: " + seleccionado.getNombre() + "?");

        ButtonType aceptar = new ButtonType("Eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmacion.getButtonTypes().setAll(aceptar, cancelar);

        if (confirmacion.showAndWait().orElse(cancelar) != aceptar) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Proveedor");

            coleccion.deleteOne(new Document("_id", new org.bson.types.ObjectId(seleccionado.getId())));

            mostrarAlerta("Proveedor eliminado correctamente", Alert.AlertType.INFORMATION);

            obtenerProveedoresTabla();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al eliminar proveedor: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }     


        
   

    @FXML
    private void cancelar(ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }

    // =========================
    // BOTONES
    // =========================
    @FXML
    private void volverInicio(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }

    @FXML
    private void irClientes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }

    @FXML
    private void irComerciales(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }

    @FXML
    private void irProveedores(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaProveedor/pantallaProveedor.fxml");
    }
    

    @FXML
    private void irStock(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }

    @FXML
    private void irPresupuestos(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }

        @FXML
    private void irInstalaciones(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml");
    }
    
    @FXML
    private void irInformes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }
    
    

    // =========================
    // CARGAR DATOS
    // =========================
    public void obtenerProveedoresTabla() throws IOException {

        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<Document> coleccion = db.getCollection("Proveedor");

        listaOriginal.clear();

        for (Document doc : coleccion.find()) {

            Object dirObj = doc.get("direccion");

            Document dirDoc = null;
            if (dirObj instanceof Document) {
                dirDoc = (Document) dirObj;
            }

            Direccion direccion = null;
            if (dirDoc != null) {
                direccion = new Direccion(
                        dirDoc.getString("calle"),
                        dirDoc.getString("numero"),
                        dirDoc.getString("codigoPostal"),
                        dirDoc.getString("municipio"),
                        dirDoc.getString("provincia")
                );
            }

            Proveedor c = new Proveedor();

            c.setId(doc.getObjectId("_id").toString());
            c.setNombre(doc.getString("nombre"));
            c.setApellidos(doc.getString("apellidos"));
            c.setTelefono(doc.getString("telefono"));
            c.setEmail(doc.getString("email"));
            c.setDireccion(direccion);
            c.setNombreEmpresa(doc.getString("nombreEmpresa"));
            c.setWeb(doc.getString("web"));
            c.setObservaciones(doc.getString("observaciones"));


            listaOriginal.add(c);
        }

        tablaProveedores.setItems(listaOriginal);
    }

    // =========================
    // FILTRO
    // =========================
    @FXML

    public void buscarFiltro() {

    String filtro = txtFiltro.getText().toLowerCase();

    if (filtro.isEmpty()) {
        tablaProveedores.setItems(listaOriginal);
        return;
    }

    ObservableList<Proveedor> filtrada = FXCollections.observableArrayList();

    for (Proveedor c : listaOriginal) {

        if ((c.getNombre() != null && c.getNombre().toLowerCase().contains(filtro))
                || (c.getApellidos() != null && c.getApellidos().toLowerCase().contains(filtro))
                || (c.getEmail() != null && c.getEmail().toLowerCase().contains(filtro))
                || (c.getNombreEmpresa() != null && c.getNombreEmpresa().toLowerCase().contains(filtro))
                || (c.getTelefono() != null && c.getTelefono().toLowerCase().contains(filtro))) {

            filtrada.add(c);
        }
    }

    tablaProveedores.setItems(filtrada);
}

     private void mostrarAlerta(String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

    
    
