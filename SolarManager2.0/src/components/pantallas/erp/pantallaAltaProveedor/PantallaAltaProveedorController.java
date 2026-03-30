package components.pantallas.erp.pantallaAltaProveedor;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.scene.Node;

public class PantallaAltaProveedorController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmpresa;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDireccion;
    @FXML private TextArea txtObservaciones;

    @FXML
    private void nuevoProveedor() {
        txtNombre.clear();
        txtEmpresa.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        txtObservaciones.clear();
    }

    @FXML
    private void guardarProveedor() {
        System.out.println("Proveedor guardado");
        // aquí irá BD
    }

    @FXML
    private void editarProveedor() {
        System.out.println("Editar proveedor");
    }

    @FXML
    private void eliminarProveedor() {
        System.out.println("Eliminar proveedor");
    }

    @FXML
    private void cancelar(javafx.event.ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }
}