package components.pantallas.erp.pantallaAltaComercial;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Comercial;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.AlertasSolarManager;

/**
 * Controlador de la pantalla de alta y modificación de comerciales.
 *
 * Gestiona la carga de datos del comercial, el guardado en MongoDB
 * y la navegación de vuelta a la pantalla de comerciales.
 *
 * @author Iván
 */
public class PantallaAltaComercialController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private CheckBox chkActivo;

    @FXML private TextField txtDni;
    @FXML private TextField txtNumeroCuenta;
    @FXML private TextField txtCentroTrabajo;
    @FXML private TextArea txtObservaciones;

    @FXML private TextField txtCalle;
    @FXML private TextField txtNumero;
    @FXML private TextField txtCodigoPostal;
    @FXML private TextField txtMunicipio;
    @FXML private TextField txtProvincia;

    @FXML private ComboBox<Comercial.TipoContrato> cbTipoContrato;

    private Comercial comercialEditar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbTipoContrato.getItems().setAll(Comercial.TipoContrato.values());
    }

    /**
     * Carga los datos del comercial seleccionado en el formulario.
     *
     * @param comercial comercial a editar
     */
    public void cargarDatos(Comercial comercial) {
        if (comercial == null) {
            return;
        }

        this.comercialEditar = comercial;

        txtNombre.setText(comercial.getNombre() != null ? comercial.getNombre() : "");
        txtApellidos.setText(comercial.getApellidos() != null ? comercial.getApellidos() : "");
        txtTelefono.setText(comercial.getTelefono() != null ? comercial.getTelefono() : "");
        txtEmail.setText(comercial.getEmail() != null ? comercial.getEmail() : "");
        txtDni.setText(comercial.getDni() != null ? comercial.getDni() : "");
        txtNumeroCuenta.setText(comercial.getNumeroCuenta() != null ? comercial.getNumeroCuenta() : "");
        txtCentroTrabajo.setText(comercial.getCentroTrabajo() != null ? comercial.getCentroTrabajo() : "");
        txtObservaciones.setText(comercial.getObservaciones() != null ? comercial.getObservaciones() : "");
        chkActivo.setSelected(comercial.getActivo());

        if (comercial.getTipoContrato() != null) {
            cbTipoContrato.setValue(comercial.getTipoContrato());
        }

        if (comercial.getDireccion() != null) {
            txtCalle.setText(comercial.getDireccion().getCalle() != null ? comercial.getDireccion().getCalle() : "");
            txtNumero.setText(comercial.getDireccion().getNumero() != null ? comercial.getDireccion().getNumero() : "");
            txtCodigoPostal.setText(comercial.getDireccion().getCodigoPostal() != null ? comercial.getDireccion().getCodigoPostal() : "");
            txtMunicipio.setText(comercial.getDireccion().getMunicipio() != null ? comercial.getDireccion().getMunicipio() : "");
            txtProvincia.setText(comercial.getDireccion().getProvincia() != null ? comercial.getDireccion().getProvincia() : "");

            String direccionTexto = "";
            if (comercial.getDireccion().getCalle() != null) {
                direccionTexto += comercial.getDireccion().getCalle();
            }
            if (comercial.getDireccion().getNumero() != null && !comercial.getDireccion().getNumero().trim().isEmpty()) {
                direccionTexto += " " + comercial.getDireccion().getNumero();
            }
            if (comercial.getDireccion().getMunicipio() != null && !comercial.getDireccion().getMunicipio().trim().isEmpty()) {
                direccionTexto += ", " + comercial.getDireccion().getMunicipio();
            }
            if (comercial.getDireccion().getProvincia() != null && !comercial.getDireccion().getProvincia().trim().isEmpty()) {
                direccionTexto += ", " + comercial.getDireccion().getProvincia();
            }
            if (comercial.getDireccion().getCodigoPostal() != null && !comercial.getDireccion().getCodigoPostal().trim().isEmpty()) {
                direccionTexto += " (" + comercial.getDireccion().getCodigoPostal() + ")";
            }

            txtDireccion.setText(direccionTexto.trim());
        } else {
            txtDireccion.setText("");
        }

        cargarCamposAdicionalesDesdeMongo();
    }

    /**
     * Carga desde MongoDB los campos que no vienen informados
     * en el objeto usado en la tabla.
     */
    private void cargarCamposAdicionalesDesdeMongo() {
        if (comercialEditar == null || comercialEditar.getId() == null || comercialEditar.getId().trim().isEmpty()) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Comerciales");

            Document doc = coleccion.find(new Document("_id", new ObjectId(comercialEditar.getId()))).first();

            if (doc != null) {
                txtUsuario.setText(doc.getString("usuario") != null ? doc.getString("usuario") : "");
                txtPassword.setText(doc.getString("password") != null ? doc.getString("password") : "");
                chkActivo.setSelected(doc.getBoolean("activo", true));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Guarda el comercial en MongoDB.
     *
     * @param event evento de acción
     */
    @FXML
    private void guardarComercial(ActionEvent event) {
        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Comerciales");

            Document direccion = new Document()
                    .append("calle", txtCalle.getText())
                    .append("numero", txtNumero.getText())
                    .append("codigoPostal", txtCodigoPostal.getText())
                    .append("municipio", txtMunicipio.getText())
                    .append("provincia", txtProvincia.getText());

            Document comercialDoc = new Document()
                    .append("nombre", txtNombre.getText())
                    .append("apellidos", txtApellidos.getText())
                    .append("telefono", txtTelefono.getText())
                    .append("email", txtEmail.getText())
                    .append("direccionTexto", txtDireccion.getText())
                    .append("usuario", txtUsuario.getText())
                    .append("password", txtPassword.getText())
                    .append("dni", txtDni.getText())
                    .append("numeroCuenta", txtNumeroCuenta.getText())
                    .append("centroTrabajo", txtCentroTrabajo.getText())
                    .append("observaciones", txtObservaciones.getText())
                    .append("direccion", direccion)
                    .append("activo", chkActivo.isSelected());

            if (cbTipoContrato.getValue() != null) {
                comercialDoc.append("tipoContrato", cbTipoContrato.getValue().toString());
            }

            if (comercialEditar == null) {
                coleccion.insertOne(comercialDoc);
            } else {
                coleccion.updateOne(
                        new Document("_id", new ObjectId(comercialEditar.getId())),
                        new Document("$set", comercialDoc)
                );
            }

            AlertasSolarManager.operacionCorrecta();
            volver(event);

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("Error al guardar el comercial");
        }
    }

    /**
     * Cancela la edición y vuelve a la pantalla anterior.
     *
     * @param event evento de acción
     */
    @FXML
    private void cancelar(ActionEvent event) {
        volver(event);
    }

    /**
     * Vuelve a la pantalla de comerciales.
     *
     * @param event evento de acción
     */
    @FXML
    private void volver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("Error al volver a la pantalla de comerciales");
        }
    }
}