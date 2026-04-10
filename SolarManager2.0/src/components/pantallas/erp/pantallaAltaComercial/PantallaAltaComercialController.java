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
import javafx.scene.control.Label;
import modelo.Comercial;
import org.mindrot.jbcrypt.BCrypt;

public class PantallaAltaComercialController implements Initializable {

    // =========================
    // CAMPOS FXML
    // =========================
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtCalle;
    @FXML
    private TextField txtNumero;
    @FXML
    private TextField txtCodigoPostal;
    @FXML
    private TextField txtMunicipio;
    @FXML
    private TextField txtProvincia;
    @FXML
    private TextField txtDni;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtNumeroCuenta;
    @FXML
    private TextField txtCentroTrabajo;
    @FXML
    private TextField txtObservaciones;
    @FXML
    private CheckBox chkActivo;
    @FXML
    private ComboBox<Comercial.TipoContrato> cmbTipoContrato;
    @FXML
    private Label txtTituloAltaModificacion;

    private boolean modoEdicion = false;
    private String idComercialSeleccionado;

    // =========================
    // INIT
    // =========================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chkActivo.setSelected(true);
        cmbTipoContrato.getItems().setAll(Comercial.TipoContrato.values());
    }

    @FXML
    private void guardarComercial(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        try {
            String passwordPlana = txtPassword.getText();
            String passwordHasheada;

            if (modoEdicion && passwordPlana.isEmpty()) {
                passwordHasheada = null;
            } else {
                passwordHasheada = BCrypt.hashpw(passwordPlana, BCrypt.gensalt(12));
            }

            org.bson.Document doc = new org.bson.Document()
                    .append("nombre", txtNombre.getText())
                    .append("apellidos", txtApellidos.getText())
                    .append("telefono", txtTelefono.getText())
                    .append("email", txtEmail.getText())
                    .append("direccion", new org.bson.Document()
                            .append("calle", txtCalle.getText())
                            .append("numero", txtNumero.getText())
                            .append("codigoPostal", txtCodigoPostal.getText())
                            .append("municipio", txtMunicipio.getText())
                            .append("provincia", txtProvincia.getText()))
                    .append("dni", txtDni.getText())
                    .append("numeroCuenta", txtNumeroCuenta.getText())
                    .append("centroTrabajo", txtCentroTrabajo.getText())
                    .append("observaciones", txtObservaciones.getText())
                    .append("activo", chkActivo.isSelected())
                    .append("tipoContrato", cmbTipoContrato.getValue() != null
                            ? cmbTipoContrato.getValue().toString() : "NO_ASIGNADO");

            if (passwordHasheada != null) {
                doc.append("password", passwordHasheada);
            }

            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<org.bson.Document> coleccion = db.getCollection("Comerciales");

            if (modoEdicion) {
                coleccion.updateOne(
                        com.mongodb.client.model.Filters.eq("_id", new org.bson.types.ObjectId(idComercialSeleccionado)),
                        new org.bson.Document("$set", doc)
                );
                mostrarAlerta("Comercial actualizado correctamente", AlertType.INFORMATION);
            } else {
                coleccion.insertOne(doc);
                mostrarAlerta("Comercial guardado correctamente", AlertType.INFORMATION);
            }

            cambiarPantalla(event, "/components/pantallas/erp/pantallaComerciales/pantallaComerciales.fxml");

        } catch (IOException e) {
            mostrarAlerta("Error en la operación: " + e.getMessage(), AlertType.ERROR);
        }
    }

    public void cargarDatos(Comercial comercial) {
        this.modoEdicion = true;
        this.idComercialSeleccionado = comercial.getId();
        txtTituloAltaModificacion.setText("Modificar Comercial");
        txtNombre.setText(comercial.getNombre());
        txtApellidos.setText(comercial.getApellidos());
        txtTelefono.setText(comercial.getTelefono());
        txtEmail.setText(comercial.getEmail());
        txtDni.setText(comercial.getDni());
        txtNumeroCuenta.setText(comercial.getNumeroCuenta());
        txtCentroTrabajo.setText(comercial.getCentroTrabajo());
        txtObservaciones.setText(comercial.getObservaciones());
        chkActivo.setSelected(comercial.getActivo());
        cmbTipoContrato.setValue(comercial.getTipoContrato());

        txtPassword.setText("");

        if (comercial.getDireccion() != null) {
            txtCalle.setText(comercial.getDireccion().getCalle());
            txtNumero.setText(comercial.getDireccion().getNumero());
            txtCodigoPostal.setText(comercial.getDireccion().getCodigoPostal());
            txtMunicipio.setText(comercial.getDireccion().getMunicipio());
            txtProvincia.setText(comercial.getDireccion().getProvincia());
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

        if (!modoEdicion && txtPassword.getText().isEmpty()) {
            mostrarAlerta("La contraseña es obligatoria para nuevos registros", Alert.AlertType.WARNING);
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
