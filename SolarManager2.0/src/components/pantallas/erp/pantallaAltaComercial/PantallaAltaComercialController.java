package components.pantallas.erp.pantallaAltaComercial;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
import javafx.scene.control.ComboBox;
import modelo.Comercial;
import modelo.Direccion;

public class PantallaAltaComercialController implements Initializable {

    // =========================
    // CAMPOS FXML
    // =========================
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtCalle;
    @FXML private TextField txtNumero;
    @FXML private TextField txtCodigoPostal;
    @FXML private TextField txtMunicipio;
    @FXML private TextField txtProvincia;
    @FXML private TextField txtDni;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtNumeroCuenta;
    @FXML private TextField txtCentroTrabajo;
    @FXML private TextField txtObservaciones;
    @FXML private CheckBox chkActivo;
    @FXML private ComboBox<Comercial.TipoContrato> cmbTipoContrato;

    // =========================
    // INIT
    // =========================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chkActivo.setSelected(true); // por defecto activo
        cmbTipoContrato.getItems().setAll(Comercial.TipoContrato.values());
    }

  
@FXML
private void guardarComercial(ActionEvent event) {

    // 1. VALIDACIONES
    if (!validarCampos()) {
        return;
    }

    try {
        // 2. CREAR OBJETO COMERCIAL
        Comercial nuevoComercial = new Comercial();
        nuevoComercial.setNombre(txtNombre.getText());
        nuevoComercial.setApellidos(txtApellidos.getText());
        nuevoComercial.setTelefono(txtTelefono.getText());
        nuevoComercial.setEmail(txtEmail.getText());
        nuevoComercial.setPassword(txtPassword.getText());
        nuevoComercial.setDireccion(new Direccion(
                txtCalle.getText(),
                txtNumero.getText(),
                txtCodigoPostal.getText(),
                txtMunicipio.getText(),
                txtProvincia.getText()
        ));
        nuevoComercial.setDni(txtDni.getText());
        nuevoComercial.setNumeroCuenta(txtNumeroCuenta.getText());
        nuevoComercial.setCentroTrabajo(txtCentroTrabajo.getText());
        nuevoComercial.setObservaciones(txtObservaciones.getText());
        nuevoComercial.setActivo(chkActivo.isSelected());
        nuevoComercial.setTipoContrato(cmbTipoContrato.getValue());

        // 3. CONVERTIR A DOCUMENT PARA MONGO
        org.bson.Document doc = new org.bson.Document()
                .append("nombre", nuevoComercial.getNombre())
                .append("apellidos", nuevoComercial.getApellidos())
                .append("telefono", nuevoComercial.getTelefono())
                .append("email", nuevoComercial.getEmail())
                .append("direccion", new org.bson.Document()
                        .append("calle", nuevoComercial.getDireccion().getCalle())
                        .append("numero", nuevoComercial.getDireccion().getNumero())
                        .append("codigoPostal", nuevoComercial.getDireccion().getCodigoPostal())
                        .append("municipio", nuevoComercial.getDireccion().getMunicipio())
                        .append("provincia", nuevoComercial.getDireccion().getProvincia()))
                .append("dni", nuevoComercial.getDni())
                .append("password", nuevoComercial.getPassword())
                .append("numeroCuenta", nuevoComercial.getNumeroCuenta())
                .append("centroTrabajo", nuevoComercial.getCentroTrabajo())
                .append("observaciones", nuevoComercial.getObservaciones())
                .append("activo", nuevoComercial.getActivo())
                .append("tipoContrato", nuevoComercial.getTipoContrato() != null
                        ? nuevoComercial.getTipoContrato().toString()
                        : "NO_ASIGNADO");

        // 4. GUARDAR EN MONGO
        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<org.bson.Document> coleccion = db.getCollection("Comerciales");
        coleccion.insertOne(doc);

        // 5. MENSAJE Y REDIRECCIÓN
        mostrarAlerta("Comercial guardado correctamente", AlertType.INFORMATION);
        cambiarPantalla(event, "/components/pantallas/erp/pantallaComerciales/pantallaComerciales.fxml");

    } catch (IOException e) {
        mostrarAlerta("Error al guardar comercial: " + e.getMessage(), AlertType.ERROR);
    }
}

    // =========================
    // VALIDACIONES
    // =========================
    private boolean validarCampos() {

    if (txtNombre.getText().isEmpty()) {
        mostrarAlerta("El nombre es obligatorio", Alert.AlertType.WARNING);
        return false;
    }

    if (txtApellidos.getText().isEmpty()) {
        mostrarAlerta("Los apellidos son obligatorios", Alert.AlertType.WARNING);
        return false;
    }

    if (txtTelefono.getText().isEmpty()) {
        mostrarAlerta("El teléfono es obligatorio", Alert.AlertType.WARNING);
        return false;
    }

    if (txtEmail.getText().isEmpty()) {
        mostrarAlerta("El email es obligatorio", Alert.AlertType.WARNING);
        return false;
    }

    if (!txtEmail.getText().contains("@")) {
        mostrarAlerta("El email no es válido", Alert.AlertType.WARNING);
        return false;
    }

    if (txtCalle.getText().isEmpty()) {
        mostrarAlerta("La calle es obligatoria", Alert.AlertType.WARNING);
        return false;
    }

    if (txtNumero.getText().isEmpty()) {
        mostrarAlerta("El número es obligatorio", Alert.AlertType.WARNING);
        return false;
    }

    if (txtCodigoPostal.getText().isEmpty()) {
        mostrarAlerta("El código postal es obligatorio", Alert.AlertType.WARNING);
        return false;
    }

    if (txtMunicipio.getText().isEmpty()) {
        mostrarAlerta("El municipio es obligatorio", Alert.AlertType.WARNING);
        return false;
    }

    if (txtProvincia.getText().isEmpty()) {
        mostrarAlerta("La provincia es obligatoria", Alert.AlertType.WARNING);
        return false;
    }

    if (txtDni.getText().isEmpty()) {
        mostrarAlerta("El DNI es obligatorio", Alert.AlertType.WARNING);
        return false;
    }

    if (txtPassword.getText().isEmpty()) {
        mostrarAlerta("La contraseña es obligatoria", Alert.AlertType.WARNING);
        return false;
    }

    if (txtNumeroCuenta.getText().isEmpty()) {
        mostrarAlerta("El número de cuenta es obligatorio", Alert.AlertType.WARNING);
        return false;
    }

    if (cmbTipoContrato.getValue() == null) {
        mostrarAlerta("Debe seleccionar un tipo de contrato", Alert.AlertType.WARNING);
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
    txtCalle.clear();
    txtNumero.clear();
    txtCodigoPostal.clear();
    txtMunicipio.clear();
    txtProvincia.clear();
    txtDni.clear();
    txtPassword.clear();
    txtNumeroCuenta.clear();
    txtCentroTrabajo.clear();
    txtObservaciones.clear();
    chkActivo.setSelected(true);
    cmbTipoContrato.getSelectionModel().clearSelection(); 
}

    // =========================
    // CANCELAR
    // =========================
    @FXML
    private void cancelar(ActionEvent event) {
        volver(event);
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
            mostrarAlerta("Error al cambiar de pantalla", AlertType.ERROR);
        }
    }
     // =========================
    // VOLVER
    // =========================
    @FXML
    private void volver(ActionEvent event) {
        cambiarPantalla(event, "/components/pantallas/erp/pantallaComerciales/pantallaComerciales.fxml");
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