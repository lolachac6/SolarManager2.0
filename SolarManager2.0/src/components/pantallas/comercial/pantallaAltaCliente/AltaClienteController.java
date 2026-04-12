package components.pantallas.comercial.pantallaAltaCliente;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import components.navigation.SessionContext;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import modelo.Cliente;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;

public class AltaClienteController implements Initializable {

    // =========================
    // CAMPOS FXML (EXACTOS)
    // =========================

    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnCalcular;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;

    @FXML private ComboBox<Cliente.TipoCliente> cbTipoCliente;

    @FXML private TextField txtId;
    @FXML private TextField txtDni;
    @FXML private TextField txtCif;
    @FXML private TextField txtCuenta;
    @FXML private TextField txtIdComercial;

    @FXML private TextArea txtObservaciones;

    @FXML private TextField txtCalle;
    @FXML private TextField txtNumero;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtProvincia;
    @FXML private TextField txtCodigoPostal;

    private Cliente clienteEditar;

    // =========================
    // INIT
    // =========================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Combo
        cbTipoCliente.getItems().setAll(Cliente.TipoCliente.values());

        // Eventos (sin tocar FXML)
        btnGuardar.setOnAction(this::guardar);
        btnCancelar.setOnAction(this::volverInicio);
        btnCalcular.setOnAction(this::irCalculoInstalacion);
    }

    // =========================
    // SET CLIENTE (EDICIÓN)
    // =========================

    public void setCliente(Cliente cliente) {

        this.clienteEditar = cliente;

        txtId.setText(cliente.getId());
        txtNombre.setText(cliente.getNombre());
        txtApellidos.setText(cliente.getApellidos());
        txtTelefono.setText(cliente.getTelefono());
        txtEmail.setText(cliente.getEmail());
        txtDni.setText(cliente.getDni());
        txtCif.setText(cliente.getCif());
        txtCuenta.setText(cliente.getNumeroCuenta());
        txtIdComercial.setText(cliente.getIdComercialAsignado());
        txtObservaciones.setText(cliente.getObservaciones());

        if (cliente.getTipoCliente() != null) {
            cbTipoCliente.setValue(cliente.getTipoCliente());
        }

        if (cliente.getDireccion() != null) {
            txtCalle.setText(cliente.getDireccion().getCalle());
            txtNumero.setText(cliente.getDireccion().getNumero());
            txtCiudad.setText(cliente.getDireccion().getMunicipio());
            txtProvincia.setText(cliente.getDireccion().getProvincia());
            txtCodigoPostal.setText(cliente.getDireccion().getCodigoPostal());
        }
    }

    // =========================
    // DOCUMENTO MONGO
    // =========================

    private Document construirDocumentoCliente() {

        Document direccion = new Document()
                .append("calle", txtCalle.getText())
                .append("numero", txtNumero.getText())
                .append("municipio", txtCiudad.getText())
                .append("provincia", txtProvincia.getText())
                .append("codigoPostal", txtCodigoPostal.getText());

        return new Document()
                .append("nombre", txtNombre.getText())
                .append("apellidos", txtApellidos.getText())
                .append("telefono", txtTelefono.getText())
                .append("email", txtEmail.getText())
                .append("tipoCliente", cbTipoCliente.getValue())
                .append("dni", txtDni.getText())
                .append("cif", txtCif.getText())
                .append("numeroCuenta", txtCuenta.getText())
                .append("idComercialAsignado", txtIdComercial.getText())
                .append("observaciones", txtObservaciones.getText())
                .append("direccion", direccion);
    }

    // =========================
    // GUARDAR
    // =========================

    private void guardar(ActionEvent event) {

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Clientes");

            if (clienteEditar == null) {
                coleccion.insertOne(construirDocumentoCliente());
            } else {
                Document filtro = new Document("_id", new ObjectId(clienteEditar.getId()));
                Document update = new Document("$set", construirDocumentoCliente());
                coleccion.updateOne(filtro, update);
            }

            volver(event);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al guardar cliente", Alert.AlertType.ERROR);
        }
    }

    // =========================
    // VOLVER NUEVO / CANCELAR
    // =========================
    @FXML
    private void volverInicio(ActionEvent e) {

        String destino;

        if (SessionContext.isAdmin()) {
            destino = "/components/pantallas/erp/plantillaGeneral/plantillaGeneral.fxml";
        } else {
            destino = "/components/pantallas/comercial/pantallaGeneral/pantallaGeneral.fxml";
        }

    cambiarPantalla((Node) e.getSource(), destino);
}


    private void volver(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
                "/components/pantallas/erp/pantallaClientes/pantallaClientes.fxml");
    }

    // =========================
    // NAVEGACIÓN
    // =========================

    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));
            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void irCalculoInstalacion(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
                "/components/pantallas/comercial/calculoInstalacion/CalculoInstalacion.fxml");
    }

    // =========================
    // UTIL
    // =========================

    private void mostrarAlerta(String msg, Alert.AlertType tipo) {
        new Alert(tipo, msg).showAndWait();
    }
}