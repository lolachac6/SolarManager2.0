package components.pantallas.erp.pantallaAltaComercial;

import ConexionSupabase.config;
import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import modelo.Comercial;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;

import utils.AlertasSolarManager;

/**
 * Controlador de la pantalla de alta y edición de comerciales.
 *
 * <p>Gestiona la validación del formulario, el guardado en MongoDB,
 * la integración con Supabase y la navegación de la pantalla.</p>
 *
 * @author Iván
 */
public class PantallaAltaComercialController implements Initializable {

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
    @FXML private Label txtTituloAltaModificacion;

    private boolean modoEdicion = false;
    private String idComercialSeleccionado;
    private String idSupabaseSeleccionado;
    private final String SUPABASE_URL = config.get("supabase.key");
    private final String SERVICE_ROLE_KEY = config.get("supabase.url");

    /**
     * Inicializa el controlador.
     *
     * @param url URL de inicialización
     * @param rb recursos asociados
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chkActivo.setSelected(true);
        chkActivo.setDisable(true);
        cmbTipoContrato.getItems().setAll(Comercial.TipoContrato.values());

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Comerciales");

            coleccion.createIndex(
                    com.mongodb.client.model.Indexes.ascending("email"),
                    new com.mongodb.client.model.IndexOptions().unique(true)
            );
        } catch (IOException e) {
            System.out.println("El índice ya existe o hay duplicados: " + e.getMessage());
        }
    }

    /**
     * Guarda o actualiza un comercial.
     *
     * @param event evento de acción
     * @throws IOException si se produce un error de entrada/salida
     */
    @FXML
    private void guardarComercial(ActionEvent event) throws IOException {

        if (!validarCampos()) {
            return;
        }

        if (!AlertasSolarManager.confirmar(
                "Guardar comercial",
                "¿Desea guardar los cambios del comercial?"
        )) {
            return;
        }

        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<Document> coleccion = db.getCollection("Comerciales");

        String passwordPlana = txtPassword.getText();
        String passwordHasheada = null;

        if (!passwordPlana.isEmpty()) {
            passwordHasheada = BCrypt.hashpw(passwordPlana, BCrypt.gensalt(12));
        }

        Document doc = new Document()
                .append("nombre", txtNombre.getText())
                .append("apellidos", txtApellidos.getText())
                .append("telefono", txtTelefono.getText())
                .append("email", txtEmail.getText())
                .append("direccion", new Document()
                        .append("calle", txtCalle.getText())
                        .append("numero", txtNumero.getText())
                        .append("codigoPostal", txtCodigoPostal.getText())
                        .append("municipio", txtMunicipio.getText())
                        .append("provincia", txtProvincia.getText()))
                .append("dni", txtDni.getText())
                .append("numeroCuenta", txtNumeroCuenta.getText())
                .append("centroTrabajo", txtCentroTrabajo.getText())
                .append("observaciones", txtObservaciones.getText())
                .append("tipoContrato",
                        cmbTipoContrato.getValue() != null ? cmbTipoContrato.getValue().toString() : "NO_ASIGNADO");

        if (passwordHasheada != null) {
            doc.append("password", passwordHasheada);
        }
        if (!modoEdicion) {
            doc.append("activo", true);
        }

        try {

            if (modoEdicion) {
                if (emailExiste(txtEmail.getText().trim())) {
                    AlertasSolarManager.emailComercialDuplicadoEnActualizacion();
                    return;
                }
                ObjectId objectId = new ObjectId(idComercialSeleccionado);

                coleccion.updateOne(Filters.eq("_id", objectId), new Document("$set", doc));

                if (idSupabaseSeleccionado != null) {
                    actualizarUsuarioSupabase(idSupabaseSeleccionado, txtEmail.getText(), passwordPlana);
                    actualizarTablaUsuariosSupabase(idSupabaseSeleccionado, txtEmail.getText(), txtNombre.getText(), passwordHasheada);
                }

                AlertasSolarManager.comercialActualizadoCorrectamente();
            } else {

                String supabaseId = crearUsuarioSupabase(txtEmail.getText(), passwordPlana);

                insertarEnTablaUsuariosSupabase(supabaseId, txtEmail.getText(), txtNombre.getText(), passwordHasheada);

                doc.append("supabase_id", supabaseId);
                coleccion.insertOne(doc);

                AlertasSolarManager.comercialCreadoCorrectamente();
            }

            cambiarPantalla(event, "/components/pantallas/erp/pantallaComerciales/pantallaComerciales.fxml");

        } catch (IOException e) {
            AlertasSolarManager.errorGenerico(e.getMessage());
        }
    }

    /**
     * Crea un usuario en Supabase.
     *
     * @param email email del usuario
     * @param password contraseña del usuario
     * @return identificador del usuario creado
     * @throws IOException si se produce un error de comunicación
     */
    private String crearUsuarioSupabase(String email, String password) throws IOException {
        URL url = new URL(SUPABASE_URL + "/auth/v1/admin/users");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        configurarHeadersBase(conn);

        String json = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"email_confirm\":true}";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes("utf-8"));
        }

        int code = conn.getResponseCode();
        if (code == 200 || code == 201) {
            try (Scanner sc = new Scanner(conn.getInputStream())) {
                String response = sc.useDelimiter("\\A").next();
                return response.split("\"id\":\"")[1].split("\"")[0];
            }
        }
        throw new RuntimeException("Error Auth Supabase. Código: " + code);
    }

    /**
     * Inserta el usuario en la tabla de usuarios de Supabase.
     *
     * @param uuid identificador del usuario
     * @param email email
     * @param nombre nombre
     * @param passwordHash hash de contraseña
     * @throws IOException si se produce un error de comunicación
     */
    private void insertarEnTablaUsuariosSupabase(String uuid, String email, String nombre, String passwordHash) throws IOException {
        URL url = new URL(SUPABASE_URL + "/rest/v1/usuarios");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        configurarHeadersBase(conn);

        String json = "{\"id\":\"" + uuid + "\",\"email\":\"" + email + "\",\"nombre\":\"" + nombre + "\","
                + "\"password_hash\":\"" + passwordHash + "\",\"rol\":\"comercial\"}";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes("utf-8"));
        }

        if (conn.getResponseCode() >= 300) {
            throw new RuntimeException("Error insert tabla usuarios Supabase: " + conn.getResponseCode());
        }
    }

    /**
     * Actualiza el usuario en Supabase.
     *
     * @param uuid identificador del usuario
     * @param email email actualizado
     * @param password contraseña actualizada
     */
    private void actualizarUsuarioSupabase(String uuid, String email, String password) {

        try {
            URL url = new URL(SUPABASE_URL + "/auth/v1/admin/users/" + uuid);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            configurarHeadersBase(conn);

            String json = "{\"email\":\"" + email + "\""
                    + (password != null && !password.isEmpty()
                    ? ",\"password\":\"" + password + "\""
                    : "")
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes("utf-8"));
            }

            int code = conn.getResponseCode();

            if (code >= 300) {
                AlertasSolarManager.errorSupabaseDuplicadoOGeneral();
            }

        } catch (IOException e) {
            AlertasSolarManager.falloActualizarUsuarioSupabase(e.getMessage());
            System.err.println("Supabase error: " + e.getMessage());
        }
    }

    /**
     * Actualiza la tabla de usuarios en Supabase.
     *
     * @param uuid identificador del usuario
     * @param email email actualizado
     * @param nombre nombre actualizado
     * @param passwordHash hash actualizado
     */
    private void actualizarTablaUsuariosSupabase(String uuid, String email, String nombre, String passwordHash) {

        try {
            System.out.println("aqui es el actualizarTablaUsuariosSupabase " + uuid);

            URL url = new URL(SUPABASE_URL + "/rest/v1/usuarios?id=eq." + uuid);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");

            configurarHeadersBase(conn);

            conn.setRequestProperty("Prefer", "resolution=merge-duplicates");
            conn.setDoOutput(true);

            String json = "{\"id\":\"" + uuid + "\",\"email\":\"" + email + "\",\"nombre\":\"" + nombre + "\""
                    + (passwordHash != null ? ",\"password_hash\":\"" + passwordHash + "\"" : "") + "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes("utf-8"));
            }

            int code = conn.getResponseCode();

            if (code >= 300) {
                AlertasSolarManager.errorActualizarTablaUsuariosSupabase(code);
            }

        } catch (IOException e) {
            AlertasSolarManager.falloActualizarUsuario(e.getMessage());
        }
    }

    /**
     * Configura las cabeceras base para las peticiones HTTP a Supabase.
     *
     * @param conn conexión HTTP
     */
    private void configurarHeadersBase(HttpURLConnection conn) {
        conn.setRequestProperty("apikey", SERVICE_ROLE_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + SERVICE_ROLE_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
    }

    /**
     * Carga en el formulario los datos del comercial indicado.
     *
     * @param c comercial a editar
     */
    public void cargarDatos(Comercial c) {
        this.modoEdicion = true;
        this.idComercialSeleccionado = c.getId();
        this.idSupabaseSeleccionado = c.getSupabaseId();

        txtTituloAltaModificacion.setText("Modificar Comercial");
        txtNombre.setText(c.getNombre());
        txtApellidos.setText(c.getApellidos());
        txtTelefono.setText(c.getTelefono());
        txtEmail.setText(c.getEmail());
        txtDni.setText(c.getDni());
        txtNumeroCuenta.setText(c.getNumeroCuenta());
        txtCentroTrabajo.setText(c.getCentroTrabajo());
        txtObservaciones.setText(c.getObservaciones());
        chkActivo.setSelected(true);
        chkActivo.setDisable(true);
        cmbTipoContrato.setValue(c.getTipoContrato());
        txtPassword.setText("");

        if (c.getDireccion() != null) {
            txtCalle.setText(c.getDireccion().getCalle());
            txtNumero.setText(c.getDireccion().getNumero());
            txtCodigoPostal.setText(c.getDireccion().getCodigoPostal());
            txtMunicipio.setText(c.getDireccion().getMunicipio());
            txtProvincia.setText(c.getDireccion().getProvincia());
        }
    }

    /**
     * Valida los campos del formulario.
     *
     * @return true si todos los campos son válidos, false en caso contrario
     * @throws IOException si se produce un error al comprobar email duplicado
     */
    private boolean validarCampos() throws IOException {

        if (txtNombre.getText().isEmpty()) {
            AlertasSolarManager.nombreObligatorio();
            return false;
        }

        if (!txtNombre.getText().matches("^[A-Za-zÁÉÍÓÚáéíóúñÑ ]{2,}$")) {
            AlertasSolarManager.nombreInvalido();
            return false;
        }

        if (txtApellidos.getText().isEmpty()) {
            AlertasSolarManager.apellidosObligatorios();
            return false;
        }

        if (!txtApellidos.getText().matches("^[A-Za-zÁÉÍÓÚáéíóúñÑ ]{2,}$")) {
            AlertasSolarManager.apellidosInvalidos();
            return false;
        }

        if (txtEmail.getText().isEmpty()) {
            AlertasSolarManager.emailObligatorio();
            return false;
        }

        if (!txtEmail.getText().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            AlertasSolarManager.emailInvalido();
            return false;
        }

        if (!txtTelefono.getText().isEmpty()
                && !txtTelefono.getText().matches("^\\d{9}$")) {
            AlertasSolarManager.telefonoInvalido();
            return false;
        }

        if (!txtDni.getText().isEmpty()
                && !txtDni.getText().matches("^\\d{8}[A-Za-z]$")) {
            AlertasSolarManager.dniInvalido();
            return false;
        }

        if (!txtCodigoPostal.getText().isEmpty()
                && !txtCodigoPostal.getText().matches("^\\d{5}$")) {
            AlertasSolarManager.codigoPostalInvalido();
            return false;
        }

        if (!txtNumeroCuenta.getText().isEmpty()
                && !txtNumeroCuenta.getText().matches("^ES\\d{22}$")) {
            AlertasSolarManager.ibanInvalido();
            return false;
        }

        if (emailExiste(txtEmail.getText().trim()) && !modoEdicion) {
            AlertasSolarManager.emailComercialDuplicado();
            return false;
        }

        if (!modoEdicion && txtPassword.getText().isEmpty()) {
            AlertasSolarManager.passwordObligatoria();
            return false;
        }

        if (!txtPassword.getText().isEmpty()
                && !txtPassword.getText().matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            AlertasSolarManager.passwordDebil();
            return false;
        }

        return true;
    }

    /**
     * Comprueba si ya existe un comercial con el email indicado.
     *
     * @param email email a comprobar
     * @return true si existe, false en caso contrario
     * @throws IOException si se produce un error de acceso a la base de datos
     */
    private boolean emailExiste(String email) throws IOException {
        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<Document> coleccion = db.getCollection("Comerciales");

        Document filtro;

        if (modoEdicion && idComercialSeleccionado != null) {
            filtro = new Document("email", email.trim())
                    .append("_id", new Document("$ne", new ObjectId(idComercialSeleccionado)));
        } else {
            filtro = new Document("email", email.trim());
        }

        return coleccion.find(filtro).first() != null;
    }

    /**
     * Cambia la pantalla actual por otra indicada.
     *
     * @param event evento de acción
     * @param ruta ruta del fichero FXML
     * @throws IOException si ocurre un error de carga
     */
    private void cambiarPantalla(ActionEvent event, String ruta) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(ruta));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
    }

    /**
     * Cancela la operación actual.
     *
     * @param e evento de acción
     * @throws IOException si ocurre un error de navegación
     */
    @FXML
    private void cancelar(ActionEvent e) throws IOException {
        if (!AlertasSolarManager.confirmar(
                "Salir sin guardar",
                "¿Desea salir sin guardar los cambios?"
        )) {
            return;
        }

        volver(e);
    }

    /**
     * Vuelve a la pantalla de comerciales.
     *
     * @param event evento de acción
     */
    @FXML
    public void volver(ActionEvent event) {
        try {
            cambiarPantalla(event, "/components/pantallas/erp/pantallaComerciales/pantallaComerciales.fxml");
        } catch (IOException e) {
            AlertasSolarManager.errorAlVolver(e.getMessage());
        }
    }
}