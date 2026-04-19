package components.pantallas.erp.pantallaAltaComercial;

import ConexionSupabase.config;
import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import modelo.Comercial;
import org.mindrot.jbcrypt.BCrypt;

import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.util.Scanner;

import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.model.Filters;

public class PantallaAltaComercialController implements Initializable {

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
    private String idSupabaseSeleccionado;
    private final String SUPABASE_URL = config.get("supabase.key");
    private final String SERVICE_ROLE_KEY = config.get("supabase.url");
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

    @FXML
    private void guardarComercial(ActionEvent event) throws IOException {

        if (!validarCampos()) {
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
                    mostrarAlerta("No se puede actualizar: El email ya está en uso.", Alert.AlertType.ERROR);
                    return;
                }
                ObjectId objectId = new ObjectId(idComercialSeleccionado);

                coleccion.updateOne(Filters.eq("_id", objectId), new Document("$set", doc));

                if (idSupabaseSeleccionado != null) {

                    actualizarUsuarioSupabase(idSupabaseSeleccionado, txtEmail.getText(), passwordPlana);
                    actualizarTablaUsuariosSupabase(idSupabaseSeleccionado, txtEmail.getText(), txtNombre.getText(), passwordHasheada);
                }

                mostrarAlerta("Comercial actualizado correctamente", Alert.AlertType.INFORMATION);
            } else {

                String supabaseId = crearUsuarioSupabase(txtEmail.getText(), passwordPlana);

                insertarEnTablaUsuariosSupabase(supabaseId, txtEmail.getText(), txtNombre.getText(), passwordHasheada);

                doc.append("supabase_id", supabaseId);
                coleccion.insertOne(doc);

                mostrarAlerta("Comercial creado correctamente", Alert.AlertType.INFORMATION);
            }

            cambiarPantalla(event, "/components/pantallas/erp/pantallaComerciales/pantallaComerciales.fxml");

        } catch (IOException e) {
            mostrarAlerta("Error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String crearUsuarioSupabase(String email, String password) throws IOException {
        URL url = new URL("https://yhwsvqefbaefaxdfekzo.supabase.co"+ "/auth/v1/admin/users");
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

    private void insertarEnTablaUsuariosSupabase(String uuid, String email, String nombre, String passwordHash) throws IOException {
        URL url = new URL("https://yhwsvqefbaefaxdfekzo.supabase.co" + "/rest/v1/usuarios");
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

    private void actualizarUsuarioSupabase(String uuid, String email, String password) {

        try {
            URL url = new URL("https://yhwsvqefbaefaxdfekzo.supabase.co" + "/auth/v1/admin/users/" + uuid);
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
                mostrarAlerta("No se pudo guardar el comercial.\nPosible email duplicado o error en Supabase.",
                        Alert.AlertType.ERROR
                );
            }

        } catch (IOException e) {
            mostrarAlerta("Fallo al actualizar usuario en Supabase:\n" + e.getMessage(),
                    Alert.AlertType.ERROR
            );

            System.err.println("Supabase error: " + e.getMessage());
        }
    }

    private void actualizarTablaUsuariosSupabase(String uuid, String email, String nombre, String passwordHash) {

        try {
            System.out.println("aqui es el actualizarTablaUsuariosSupabase " + uuid);

            URL url = new URL("https://yhwsvqefbaefaxdfekzo.supabase.co" + "/rest/v1/usuarios?id=eq." + uuid);
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
                mostrarAlerta("No se pudo actualizar la tabla de usuarios.\nCódigo HTTP: " + code,
                        Alert.AlertType.ERROR
                );
            }

        } catch (IOException e) {
            mostrarAlerta("Fallo al actualizar usuario:\n" + e.getMessage(),
                    Alert.AlertType.ERROR
            );

        }
    }

    private void configurarHeadersBase(HttpURLConnection conn) {
        conn.setRequestProperty("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inlod3N2cWVmYmFlZmF4ZGZla3pvIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3MjgxNjc2MCwiZXhwIjoyMDg4MzkyNzYwfQ.B6jgKqTKjwMOnRbDaqRUI1GbliH2eSEZUFPOQ-os27A");
        conn.setRequestProperty("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inlod3N2cWVmYmFlZmF4ZGZla3pvIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3MjgxNjc2MCwiZXhwIjoyMDg4MzkyNzYwfQ.B6jgKqTKjwMOnRbDaqRUI1GbliH2eSEZUFPOQ-os27A");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
    }

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

    private boolean validarCampos() throws IOException {

        if (txtNombre.getText().isEmpty()) {
            return alerta("Nombre obligatorio");
        }

        if (!txtNombre.getText().matches("^[A-Za-zÁÉÍÓÚáéíóúñÑ ]{2,}$")) {
            return alerta("Nombre inválido");
        }

        if (txtApellidos.getText().isEmpty()) {
            return alerta("Apellidos obligatorios");
        }

        if (!txtApellidos.getText().matches("^[A-Za-zÁÉÍÓÚáéíóúñÑ ]{2,}$")) {
            return alerta("Apellidos inválidos");
        }

        if (txtEmail.getText().isEmpty()) {
            return alerta("Email obligatorio");
        }

        if (!txtEmail.getText().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            return alerta("Email inválido");
        }

        if (!txtTelefono.getText().isEmpty()
                && !txtTelefono.getText().matches("^\\d{9}$")) {
            return alerta("Teléfono inválido (9 dígitos)");
        }

        if (!txtDni.getText().isEmpty()
                && !txtDni.getText().matches("^\\d{8}[A-Za-z]$")) {
            return alerta("DNI inválido");
        }

        if (!txtCodigoPostal.getText().isEmpty()
                && !txtCodigoPostal.getText().matches("^\\d{5}$")) {
            return alerta("Código postal inválido");
        }

        if (!txtNumeroCuenta.getText().isEmpty()
                && !txtNumeroCuenta.getText().matches("^ES\\d{22}$")) {
            return alerta("Cuenta bancaria inválida (IBAN)");
        }

        if (emailExiste(txtEmail.getText().trim()) && !modoEdicion) {
            return alerta("Ya existe un comercial con este email");
        }

        if (!modoEdicion && txtPassword.getText().isEmpty()) {
            return alerta("Password obligatoria");
        }

        if (!txtPassword.getText().isEmpty()
                && !txtPassword.getText().matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            return alerta("Password débil (mín 8 caracteres, 1 mayúscula y 1 número)");
        }

        return true;
    }

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

    private boolean alerta(String msg) {
        mostrarAlerta(msg, Alert.AlertType.WARNING);
        return false;
    }

    private void cambiarPantalla(ActionEvent event, String ruta) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(ruta));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
    }

    @FXML
    private void cancelar(ActionEvent e) throws IOException {
        volver(e);
    }

    @FXML
    public void volver(ActionEvent event) {
        try {
            cambiarPantalla(event, "/components/pantallas/erp/pantallaComerciales/pantallaComerciales.fxml");
        } catch (IOException e) {
            mostrarAlerta("Error al volver: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Sistema");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
