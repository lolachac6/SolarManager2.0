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
import utils.AlertasSolarManager;

/**
 * Controlador de la pantalla de proveedores.
 *
 * <p>Gestiona la carga y visualización de proveedores, el filtrado
 * de la tabla, la navegación entre pantallas y las acciones de
 * detalle, actualización y eliminación.</p>
 *
 * @author Iván
 */
public class PantallaProveedorController implements Initializable {

    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, String> colId;
    @FXML private TableColumn<Proveedor, String> colNombre;
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private TableColumn<Proveedor, String> colEmail;
    @FXML private TableColumn<Proveedor, String> colDireccion;
    @FXML private TextField txtFiltro;

    private ObservableList<Proveedor> listaOriginal = FXCollections.observableArrayList();

    /**
     * Inicializa el controlador configurando la tabla, cargando los proveedores
     * y activando el filtrado por texto.
     *
     * @param url ubicación del recurso
     * @param rb recursos internacionales
     */
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
                data.getValue().getDireccion() != null ? data.getValue().getDireccion().toString() : ""
            )
        );

        try {
            obtenerProveedoresTabla();
        } catch (IOException e) {
            e.printStackTrace();
        }

        txtFiltro.textProperty().addListener((obs, oldVal, newVal) -> buscarFiltro());
    }

    /**
     * Cambia la pantalla actual por otra especificada mediante su ruta FXML.
     *
     * @param nodo nodo origen
     * @param rutaFXML ruta del archivo FXML
     */
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

    /**
     * Abre la pantalla de alta de proveedor.
     *
     * @param e evento de acción
     */
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
    private void modificar(ActionEvent e) {

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
    private void eliminarProveedor() {
        Proveedor seleccionado = tablaProveedores.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un Proveedor de la tabla para eliminar", Alert.AlertType.WARNING);
            return;
        }

        boolean confirmar = AlertasSolarManager.confirmar(
                "Eliminar Proveedor",
                "¿Seguro que deseas eliminar el proveedor: " + seleccionado.getNombre() + "?",
                "Eliminar",
                "Cancelar"
        );

        if (!confirmar) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Proveedor");

            coleccion.deleteOne(new Document("_id", new org.bson.types.ObjectId(seleccionado.getId())));

            AlertasSolarManager.info("Proveedor eliminado", "Proveedor eliminado correctamente.");

            obtenerProveedoresTabla();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al eliminar proveedor: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Cierra la ventana actual.
     *
     * @param e evento de acción
     */
    @FXML
    private void cancelar(ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Navega a la pantalla principal del ERP.
     *
     * @param e evento de acción
     */
    @FXML
    private void volverInicio(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }

    /**
     * Navega a la pantalla de clientes.
     *
     * @param e evento de acción
     */
    @FXML
    private void irClientes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }

    /**
     * Navega a la pantalla de comerciales.
     *
     * @param e evento de acción
     */
    @FXML
    private void irComerciales(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }

    /**
     * Navega a la pantalla de proveedores.
     *
     * @param e evento de acción
     */
    @FXML
    private void irProveedores(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaProveedor/pantallaProveedor.fxml");
    }

    /**
     * Navega a la pantalla de stock.
     *
     * @param e evento de acción
     */
    @FXML
    private void irStock(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }

    /**
     * Navega a la pantalla de presupuestos.
     *
     * @param e evento de acción
     */
    @FXML
    private void irPresupuestos(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }

    /**
     * Navega a la pantalla de instalaciones.
     *
     * @param e evento de acción
     */
    @FXML
    private void irInstalaciones(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml");
    }

    /**
     * Navega a la pantalla de informes.
     *
     * @param e evento de acción
     */
    @FXML
    private void irInformes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }

    /**
     * Carga los proveedores desde MongoDB y los muestra en la tabla.
     *
     * @throws IOException si ocurre un error de acceso
     */
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

    /**
     * Filtra la tabla de proveedores según el texto introducido.
     */
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

    /**
     * Muestra una alerta mediante la clase centralizada de alertas.
     *
     * @param msg mensaje principal
     * @param tipo tipo de alerta
     */
    private void mostrarAlerta(String msg, Alert.AlertType tipo) {
        AlertasSolarManager.mostrar(tipo, null, msg);
    }
}