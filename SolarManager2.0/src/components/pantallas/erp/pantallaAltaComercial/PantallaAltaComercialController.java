package components.pantallas.erp.pantallaAltaComercial;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PantallaAltaComercialController implements Initializable {

    // =========================
    // CAMPOS FXML
    // =========================
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private CheckBox chkActivo;

    // =========================
    // INIT
    // =========================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chkActivo.setSelected(true); // por defecto activo
    }

    // =========================
    // GUARDAR COMERCIAL
    // =========================
    @FXML
    private void guardarComercial() {

        // 1. VALIDACIONES
        if (!validarCampos()) {
            return;
        }

        // 2. RECOGER DATOS
        String nombre = txtNombre.getText();
        String apellidos = txtApellidos.getText();
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();
        String direccion = txtDireccion.getText();
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();
        boolean activo = chkActivo.isSelected();

        // 3. LÓGICA (AQUÍ IRÁ TU DAO / SERVICE)
        System.out.println("=== NUEVO COMERCIAL ===");
        System.out.println(nombre + " " + apellidos);
        System.out.println("Usuario: " + usuario);
        System.out.println("Activo: " + activo);

        // TODO: insertar en BD (Mongo / SQL / lo que uses)

        // 4. FEEDBACK
        mostrarAlerta("Comercial creado correctamente", AlertType.INFORMATION);

        limpiarFormulario();
    }

    // =========================
    // VALIDACIONES
    // =========================
    private boolean validarCampos() {

        if (txtNombre.getText().isEmpty()) {
            mostrarAlerta("El nombre es obligatorio", AlertType.WARNING);
            return false;
        }

        if (txtApellidos.getText().isEmpty()) {
            mostrarAlerta("Los apellidos son obligatorios", AlertType.WARNING);
            return false;
        }

        if (txtEmail.getText().isEmpty()) {
            mostrarAlerta("El email es obligatorio", AlertType.WARNING);
            return false;
        }

        if (!txtEmail.getText().contains("@")) {
            mostrarAlerta("El email no es válido", AlertType.WARNING);
            return false;
        }

        if (txtUsuario.getText().isEmpty()) {
            mostrarAlerta("El usuario es obligatorio", AlertType.WARNING);
            return false;
        }

        if (txtPassword.getText().isEmpty()) {
            mostrarAlerta("La contraseña es obligatoria", AlertType.WARNING);
            return false;
        }

        return true;
    }

    // =========================
    // LIMPIAR FORMULARIO
    // =========================
    private void limpiarFormulario() {
        txtNombre.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        txtUsuario.clear();
        txtPassword.clear();
        chkActivo.setSelected(true);
    }

    // =========================
    // CANCELAR
    // =========================
    @FXML
    private void cancelar(ActionEvent event) {
        volver(event);
    }

    // =========================
    // VOLVER
    // =========================
    @FXML
    private void volver(ActionEvent event) {
        cambiarPantalla(event, "/components/pantallas/erp/pantallaGeneral/pantallaGeneral.fxml");
    }

    // =========================
    // MÉTODO DE NAVEGACIÓN
    // =========================
    private void cambiarPantalla(ActionEvent event, String rutaFXML) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cambiar de pantalla", AlertType.ERROR);
        }
    }

    // =========================
    // ALERTAS
    // =========================
    private void mostrarAlerta(String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Solar Manager");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}