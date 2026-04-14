package components.pantallas.erp.pantallaAltaProveedor;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javax.xml.soap.*;
import modelo.Direccion;
import modelo.Proveedor;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Controlador de alta, edición y eliminación de proveedores.
 */
public class PantallaAltaProveedorController {

    /** ID real de MongoDB del proveedor */
    private ObjectId proveedorId;

    /** Controla si estamos en modo edición */
    private boolean modoEdicion = false;

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmpresa;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDireccion;
    @FXML private TextArea txtObservaciones;

    /**
     * Inicializa la pantalla bloqueando el formulario.
     */
    @FXML
    public void initialize() {
        bloquearFormulario(false);
    }

    /**
     * Bloquea o desbloquea el formulario.
     */
    private void bloquearFormulario(boolean bloquear) {
        txtNombre.setDisable(bloquear);
        txtEmpresa.setDisable(bloquear);
        txtTelefono.setDisable(bloquear);
        txtEmail.setDisable(bloquear);
        txtDireccion.setDisable(bloquear);
        txtObservaciones.setDisable(bloquear);
    }

    /**
     * Limpia el formulario.
     */
    @FXML
    private void nuevoProveedor() {
        txtNombre.clear();
        txtEmpresa.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        txtObservaciones.clear();
        proveedorId = null;
        modoEdicion = false;
        bloquearFormulario(false);
    }

    /**
     * Guarda o actualiza un proveedor.
     */
    @FXML
    private void guardarProveedor(ActionEvent e) {

        if (!validarCampos()) {
            return;
        }

        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar guardado");
        confirmacion.setHeaderText("Guardar proveedor");
        confirmacion.setContentText("¿Deseas guardar los cambios del proveedor?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (!resultado.isPresent() || resultado.get() != ButtonType.OK) {
            return;
        }

        try {
            Proveedor nuevoProveedor = new Proveedor();
            nuevoProveedor.setNombre(txtNombre.getText());
            nuevoProveedor.setTelefono(txtTelefono.getText());
            nuevoProveedor.setEmail(txtEmail.getText());
            nuevoProveedor.setDireccion(new Direccion());
            nuevoProveedor.setNombreEmpresa(txtEmpresa.getText());
            nuevoProveedor.setObservaciones(txtObservaciones.getText());

            Document direccionDoc = new Document()
                    .append("calle", txtDireccion.getText())
                    .append("numero", "")
                    .append("codigoPostal", "")
                    .append("municipio", "")
                    .append("provincia", "");

            Document doc = new Document()
                    .append("nombre", nuevoProveedor.getNombre())
                    .append("telefono", nuevoProveedor.getTelefono())
                    .append("email", nuevoProveedor.getEmail())
                    .append("direccion", direccionDoc)
                    .append("nombreEmpresa", nuevoProveedor.getNombreEmpresa())
                    .append("observaciones", nuevoProveedor.getObservaciones());

            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Proveedor");

            if (proveedorId != null) {
                coleccion.updateOne(
                        new Document("_id", proveedorId),
                        new Document("$set", doc)
                );
            } else {
                Document existente = coleccion.find(
                        new Document("nombre", txtNombre.getText())
                ).first();

                if (existente != null) {
                    coleccion.updateOne(
                            new Document("nombre", txtNombre.getText()),
                            new Document("$set", doc)
                    );
                } else {
                    coleccion.insertOne(doc);
                }
            }

            mostrarAlerta("Proveedor guardado correctamente", AlertType.INFORMATION);

            cambiarPantalla(
                    (Node) e.getSource(),
                    "/components/pantallas/erp/pantallaProveedor/pantallaProveedor.fxml"
            );

        } catch (Exception ex) {
            mostrarAlerta("Error al guardar proveedor: " + ex.getMessage(), AlertType.ERROR);
        }
    }

    private boolean validarCampos() {

        if (txtNombre.getText().isEmpty()) {
            mostrarAlerta("El nombre es obligatorio", Alert.AlertType.WARNING);
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

        if (txtDireccion.getText().isEmpty()) {
            mostrarAlerta("La calle es obligatoria", Alert.AlertType.WARNING);
            return false;
        }

        if (txtEmpresa.getText().isEmpty()) {
            mostrarAlerta("La empresa es obligatoria", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    @FXML
    private void cancelar(ActionEvent e) {

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar cambios");
        confirmacion.setHeaderText("Salir sin guardar");
        confirmacion.setContentText(
                "¿Deseas cancelar y volver a la lista de proveedores?\n\n" +
                "Los cambios no guardados se perderán."
        );

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (!resultado.isPresent() || resultado.get() != ButtonType.OK) {
            return;
        }

        cambiarPantalla(
                (Node) e.getSource(),
                "/components/pantallas/erp/pantallaProveedor/pantallaProveedor.fxml"
        );
    }

    @FXML
    private void volver(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaProveedor/pantallaProveedor.fxml");
    }

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

    public void cargarProveedor(Proveedor p) {

        txtNombre.setText(p.getNombre());
        txtTelefono.setText(p.getTelefono());
        txtEmail.setText(p.getEmail());
        txtEmpresa.setText(p.getNombreEmpresa());
        txtObservaciones.setText(p.getObservaciones());

        if (p.getDireccion() != null) {
            txtDireccion.setText(p.getDireccion().toString());
        }

        bloquearFormulario(false);
        modoEdicion = false;
    }

    private void mostrarAlerta(String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Solar Manager");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtEmpresa.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        txtObservaciones.clear();
        proveedorId = null;
        modoEdicion = false;
        bloquearFormulario(true);
    }
}