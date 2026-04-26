package components.pantallas.erp.pantallaProveedor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import org.bson.Document;
import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Platform;
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
import javafx.stage.Modality;
import utils.AlertasSolarManager;
import utils.CifradoDatos;

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
    @FXML private TableColumn<Proveedor, String> colNombreEmpresa;
    @FXML private TableColumn<Proveedor, String> colRazonSocial;
    @FXML private TableColumn<Proveedor, String> colCif;
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private TableColumn<Proveedor, String> colEmail;
    @FXML private TableColumn<Proveedor, String> colDireccion;
    @FXML private TextField txtFiltro;

    private ObservableList<Proveedor> listaOriginal = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        colId.setVisible(false); 
        
        colId.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getId() != null ? data.getValue().getId() : ""
            )
        );
        colNombreEmpresa.setCellValueFactory(new PropertyValueFactory<>("nombreEmpresa"));
        colRazonSocial.setCellValueFactory(new PropertyValueFactory<>("razonSocial"));
        colCif.setCellValueFactory(new PropertyValueFactory<>("cif"));
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
     * Cambia la pantalla actual por otra indicada mediante su ruta FXML.
     *
     * @param nodo nodo origen desde el que se obtiene el Stage
     * @param rutaFXML ruta del fichero FXML a cargar
     */
    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Stage stageActual = (Stage) nodo.getScene().getWindow();
            stageActual.close();

            Stage nuevoStage = new Stage();
            nuevoStage.setScene(new Scene(root));
            nuevoStage.setResizable(true);

            Platform.runLater(() -> {
                nuevoStage.setMaximized(true);
                nuevoStage.centerOnScreen();
            });

            nuevoStage.show();

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

            Stage stageActual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageActual.close();

            Stage nuevoStage = new Stage();
            nuevoStage.setScene(new Scene(root));
            nuevoStage.setResizable(true);

            Platform.runLater(() -> {
                nuevoStage.setMaximized(true);
                nuevoStage.centerOnScreen();
            });

            nuevoStage.show();

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

            Stage stageActual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageActual.close();

            Stage nuevoStage = new Stage();
            nuevoStage.setScene(new Scene(root));
            nuevoStage.setResizable(true);

            Platform.runLater(() -> {
                nuevoStage.setMaximized(true);
                nuevoStage.centerOnScreen();
            });

            nuevoStage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    

    @FXML
private void detalle(ActionEvent event) {
    Proveedor seleccionado = tablaProveedores.getSelectionModel().getSelectedItem();

    if (seleccionado == null) {
        AlertasSolarManager.mostrar(
            Alert.AlertType.WARNING,
            null,
            "Selecciona un proveedor para ver el detalle"
        );
        return;
    }

    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/components/pantallas/pantallaDetalleProveedor/pantalladetalleproveedor.fxml")
        );

        Parent root = loader.load();

        components.pantallas.pantallaDetalleProveedor.PantallaDetalleProveedorController controller =
                loader.getController();

        controller.cargarProveedor(seleccionado);

        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Stage modal = new Stage();
        modal.initOwner(stageActual);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setResizable(false);
        modal.setScene(new Scene(root));
        modal.showAndWait();

    } catch (IOException e) {
        e.printStackTrace();
        AlertasSolarManager.mostrar(
            Alert.AlertType.ERROR,
            null,
            "No se pudo abrir el detalle del proveedor."
        );
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

    @FXML
    private void cancelar(ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void volverInicio(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }

    @FXML
    private void irClientes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }

    @FXML
    private void irComerciales(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }

    @FXML
    private void irProveedores(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaProveedor/pantallaProveedor.fxml");
    }

    @FXML
    private void irStock(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }

    @FXML
    private void irPresupuestos(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }

    @FXML
    private void irInstalaciones(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml");
    }

    @FXML
    private void irInformes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }

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
            c.setRazonSocial(doc.getString("razonSocial"));
            c.setCif(CifradoDatos.descifrarSiEsPosible(doc.getString("cif")));
            c.setTelefono(CifradoDatos.descifrarSiEsPosible(doc.getString("telefono")));
            c.setEmail(doc.getString("email"));
            c.setDireccion(direccion);
            c.setNombreEmpresa(doc.getString("nombreEmpresa"));
            c.setWeb(doc.getString("web"));
            c.setObservaciones(doc.getString("observaciones"));

            listaOriginal.add(c);
        }

        tablaProveedores.setItems(listaOriginal);
    }

    @FXML
    public void buscarFiltro() {
        String filtro = txtFiltro.getText().toLowerCase();

        if (filtro.isEmpty()) {
            tablaProveedores.setItems(listaOriginal);
            return;
        }

        ObservableList<Proveedor> filtrada = FXCollections.observableArrayList();

        for (Proveedor c : listaOriginal) {
            if ((c.getNombreEmpresa() != null && c.getNombreEmpresa().toLowerCase().contains(filtro))
                    || (c.getRazonSocial() != null && c.getRazonSocial().toLowerCase().contains(filtro))
                    || (c.getCif() != null && c.getCif().toLowerCase().contains(filtro))
                    || (c.getEmail() != null && c.getEmail().toLowerCase().contains(filtro))
                    || (c.getTelefono() != null && c.getTelefono().toLowerCase().contains(filtro))) {

                filtrada.add(c);
            }
        }

        tablaProveedores.setItems(filtrada);
    }

    private void mostrarAlerta(String msg, Alert.AlertType tipo) {
        AlertasSolarManager.mostrar(tipo, null, msg);
    }
}