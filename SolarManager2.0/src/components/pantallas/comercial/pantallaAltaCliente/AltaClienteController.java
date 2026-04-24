package components.pantallas.comercial.pantallaAltaCliente;

import DB.MongoConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import components.navigation.SessionContext;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

import utils.AlertasSolarManager;
import utils.CifradoDatos;

/**
 * Controlador de la pantalla de alta y edición de clientes.
 *
 * <p>Gestiona la carga de datos en modo edición, la construcción del documento
 * MongoDB, el guardado del cliente y la navegación asociada a la pantalla.</p>
 *
 * @author Iván
 */
public class AltaClienteController implements Initializable {

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
    @FXML private ComboBox<ComercialItem> cbComercialAsignado;

    @FXML private TextArea txtObservaciones;

    @FXML private TextField txtCalle;
    @FXML private TextField txtNumero;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtProvincia;
    @FXML private TextField txtCodigoPostal;

    private Cliente clienteEditar;

    /**
     * Clase auxiliar para cargar comerciales en el ComboBox.
     * Se muestra el nombre, pero se conserva también el id.
     */
    private static class ComercialItem {

        private final String id;
        private final String nombre;

        public ComercialItem(String id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public String getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    /**
     * Inicializa el controlador y configura los eventos iniciales.
     *
     * @param url URL de inicialización
     * @param rb recursos asociados
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbTipoCliente.getItems().setAll(Cliente.TipoCliente.values());

        cargarComercialesActivos();

        btnGuardar.setOnAction(this::guardar);
        btnCancelar.setOnAction(this::cancelar);
        btnCalcular.setOnAction(this::irCalculoInstalacion);
    }

    /**
     * Carga en el ComboBox los comerciales activos de la colección Comerciales.
     * Filtra por activo=true y muestra el campo nombre.
     */
    private void cargarComercialesActivos() {
        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Comerciales");

            FindIterable<Document> comerciales = coleccion.find(Filters.eq("activo", true));

            cbComercialAsignado.getItems().clear();

            for (Document doc : comerciales) {
                ObjectId objectId = doc.getObjectId("_id");
                String nombre = doc.getString("nombre");

                if (objectId != null && nombre != null && !nombre.trim().isEmpty()) {
                    cbComercialAsignado.getItems().add(
                            new ComercialItem(objectId.toHexString(), nombre)
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudieron cargar los comerciales activos.");
        }
    }

    /**
     * Carga en el formulario los datos del cliente recibido para edición.
     *
     * @param cliente cliente a editar
     */
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
        txtObservaciones.setText(cliente.getObservaciones());

        if (cliente.getTipoCliente() != null) {
            cbTipoCliente.setValue(cliente.getTipoCliente());
        }

        if (cliente.getIdComercialAsignado() != null && !cliente.getIdComercialAsignado().trim().isEmpty()) {
            for (ComercialItem item : cbComercialAsignado.getItems()) {
                if (item.getId().equals(cliente.getIdComercialAsignado())) {
                    cbComercialAsignado.setValue(item);
                    break;
                }
            }
        }

        if (cliente.getDireccion() != null) {
            txtCalle.setText(cliente.getDireccion().getCalle());
            txtNumero.setText(cliente.getDireccion().getNumero());
            txtCiudad.setText(cliente.getDireccion().getMunicipio());
            txtProvincia.setText(cliente.getDireccion().getProvincia());
            txtCodigoPostal.setText(cliente.getDireccion().getCodigoPostal());
        }
    }

    /**
     * Construye el documento MongoDB correspondiente al cliente actual.
     *
     * @return documento con los datos del cliente
     */
    private Document construirDocumentoCliente() {

        Document direccion = new Document()
                .append("calle", txtCalle.getText())
                .append("numero", txtNumero.getText())
                .append("municipio", txtCiudad.getText())
                .append("provincia", txtProvincia.getText())
                .append("codigoPostal", txtCodigoPostal.getText());

        String idComercialAsignado = "";
        if (cbComercialAsignado.getValue() != null) {
            idComercialAsignado = cbComercialAsignado.getValue().getId();
        }

        return new Document()
                .append("nombre", txtNombre.getText())
                .append("apellidos", txtApellidos.getText())
                .append("telefono", CifradoDatos.cifrar(txtTelefono.getText()))
                .append("email", txtEmail.getText())
                .append("tipoCliente", cbTipoCliente.getValue())
                .append("dni", CifradoDatos.cifrar(txtDni.getText()))
                .append("cif", txtCif.getText())
                .append("numeroCuenta", CifradoDatos.cifrar(txtCuenta.getText()))
                .append("idComercialAsignado", idComercialAsignado)
                .append("observaciones", txtObservaciones.getText())
                .append("direccion", direccion);
    }

    /**
     * Guarda el cliente actual en la base de datos.
     *
     * @param event evento de acción
     */
    private void guardar(ActionEvent event) {

        if (!validarCampos()) {
            return;
        }

        if (!AlertasSolarManager.confirmar(
                "Guardar cliente",
                "¿Desea guardar los cambios del cliente?"
        )) {
            return;
        }

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
            AlertasSolarManager.errorGuardarCliente();
        }
    }

    /**
     * Valida los campos del formulario.
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
    private boolean validarCampos() {

        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        String apellidos = txtApellidos.getText() != null ? txtApellidos.getText().trim() : "";
        String telefono = txtTelefono.getText() != null ? txtTelefono.getText().trim() : "";
        String email = txtEmail.getText() != null ? txtEmail.getText().trim() : "";
        String dni = txtDni.getText() != null ? txtDni.getText().trim().toUpperCase() : "";
        String cif = txtCif.getText() != null ? txtCif.getText().trim().toUpperCase() : "";
        String cuenta = txtCuenta.getText() != null ? txtCuenta.getText().trim().toUpperCase() : "";
        String calle = txtCalle.getText() != null ? txtCalle.getText().trim() : "";
        String numero = txtNumero.getText() != null ? txtNumero.getText().trim() : "";
        String ciudad = txtCiudad.getText() != null ? txtCiudad.getText().trim() : "";
        String provincia = txtProvincia.getText() != null ? txtProvincia.getText().trim() : "";
        String codigoPostal = txtCodigoPostal.getText() != null ? txtCodigoPostal.getText().trim() : "";

        if (nombre.isEmpty()) {
            AlertasSolarManager.nombreObligatorio();
            return false;
        }

        if (!nombre.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]{2,}$")) {
            AlertasSolarManager.nombreInvalido();
            return false;
        }

        if (apellidos.isEmpty()) {
            AlertasSolarManager.apellidosObligatorios();
            return false;
        }

        if (!apellidos.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]{2,}$")) {
            AlertasSolarManager.apellidosInvalidos();
            return false;
        }

        if (!telefono.isEmpty() && !telefono.matches("^\\d{9}$")) {
            AlertasSolarManager.telefonoInvalido();
            return false;
        }

        if (email.isEmpty()) {
            AlertasSolarManager.emailObligatorio();
            return false;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            AlertasSolarManager.emailInvalido();
            return false;
        }

        if (!dni.isEmpty() && !dni.matches("^\\d{8}[A-Z]$")) {
            AlertasSolarManager.dniInvalido();
            return false;
        }

        if (!cif.isEmpty() && !cif.matches("^[A-HJNPQRSUVW]\\d{7}[0-9A-J]$")) {
            AlertasSolarManager.errorGenerico("El CIF no tiene un formato válido.");
            return false;
        }

        if (!cuenta.isEmpty() && !cuenta.matches("^ES\\d{22}$")) {
            AlertasSolarManager.ibanInvalido();
            return false;
        }

        if (cbTipoCliente.getValue() == null) {
            AlertasSolarManager.errorGenerico("Debe seleccionar un tipo de cliente.");
            return false;
        }

        if (!codigoPostal.isEmpty() && !codigoPostal.matches("^\\d{5}$")) {
            AlertasSolarManager.codigoPostalInvalido();
            return false;
        }

        if (!numero.isEmpty() && !numero.matches("^\\d+[A-Za-z]?$")) {
            AlertasSolarManager.errorGenerico("El número de la dirección no tiene un formato válido.");
            return false;
        }

        if (!ciudad.isEmpty() && !ciudad.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s\\-]+$")) {
            AlertasSolarManager.errorGenerico("La ciudad no tiene un formato válido.");
            return false;
        }

        if (!provincia.isEmpty() && !provincia.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s\\-]+$")) {
            AlertasSolarManager.errorGenerico("La provincia no tiene un formato válido.");
            return false;
        }

        if (cbTipoCliente.getValue() == Cliente.TipoCliente.PARTICULAR) {
            if (dni.isEmpty()) {
                AlertasSolarManager.errorGenerico("Para un cliente particular el DNI es obligatorio.");
                return false;
            }

            if (!cif.isEmpty()) {
                AlertasSolarManager.errorGenerico("Un cliente particular no debe tener CIF informado.");
                return false;
            }
        }

        if (cbTipoCliente.getValue() == Cliente.TipoCliente.EMPRESA) {
            if (cif.isEmpty()) {
                AlertasSolarManager.errorGenerico("Para un cliente de empresa el CIF es obligatorio.");
                return false;
            }
        }

        // Dirección obligatoria
        if (calle.isEmpty() || numero.isEmpty() || ciudad.isEmpty() || provincia.isEmpty() || codigoPostal.isEmpty()) {
            AlertasSolarManager.errorGenerico("Todos los campos de dirección son obligatorios.");
            return false;
        }


        return true;
    }

    /**
     * Cancela la operación actual previa confirmación.
     *
     * @param e evento de acción
     */
    @FXML
    private void cancelar(ActionEvent e) {
        if (!AlertasSolarManager.confirmar(
                "Salir sin guardar",
                "¿Desea salir sin guardar los cambios?"
        )) {
            return;
        }

        volver(e);
    }

    /**
     * Vuelve a la pantalla principal según el rol del usuario actual.
     *
     * @param e evento de acción
     */
    @FXML
    private void volverInicio(ActionEvent e) {

        String destino;

        if (SessionContext.isComercial()) {
            destino = "/components/pantallas/comercial/pantallaGeneral/pantallaGeneral.fxml";
        } else {
            destino = "/components/pantallas/erp/pantallaClientes/pantallaClientes.fxml";
        }

        cambiarPantalla((Node) e.getSource(), destino);
    }

    /**
     * Vuelve a la pantalla de listado de clientes.
     *
     * @param event evento de acción
     */
    @FXML
    private void volver(ActionEvent event) {
        volverInicio(event);
    }

    /**
     * Cambia la pantalla actual por la indicada.
     *
     * @param nodo nodo origen
     * @param rutaFXML ruta del fichero FXML
     */
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

    /**
     * Navega a la pantalla de cálculo de instalación.
     *
     * @param event evento de acción
     */
    private void irCalculoInstalacion(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
                "/components/pantallas/comercial/calculoInstalacion/CalculoInstalacion.fxml");
    }
}