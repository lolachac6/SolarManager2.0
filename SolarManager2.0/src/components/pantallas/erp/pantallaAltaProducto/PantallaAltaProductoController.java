package components.pantallas.erp.pantallaAltaProducto;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;

public class PantallaAltaProductoController {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private ComboBox<String> cmbProveedor;
    @FXML private TextArea txtDescripcion;

    // =========================
    // INIT
    // =========================

    @FXML
    public void initialize() {
        // Datos de ejemplo (luego irán de BD)
        cmbTipo.getItems().addAll("Panel Solar", "Batería", "Inversor", "Estructura");
        cmbProveedor.getItems().addAll("Proveedor 1", "Proveedor 2", "Proveedor 3");
    }

    // =========================
    // BOTONES
    // =========================

    @FXML
    private void nuevoProducto() {
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtDescripcion.clear();

        cmbTipo.getSelectionModel().clearSelection();
        cmbProveedor.getSelectionModel().clearSelection();
    }

    @FXML
    private void guardarProducto() {
        System.out.println("Producto guardado");
        // Aquí irá DAO / Mongo / SQL
    }

    @FXML
    private void editarProducto() {
        System.out.println("Editar producto");
    }

    @FXML
    private void eliminarProducto() {
        System.out.println("Eliminar producto");
    }

    @FXML
    private void cancelar(javafx.event.ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }
}