package components.pantallas.comun.pantallaLogin;

import ConexionSupabase.UsuarioService;
import ConexionSupabase.SessionManager;
import components.navigation.SessionContext; 

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class PantallaLoginController implements Initializable {

    @FXML
    private ImageView imgLogo;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image logo = new Image(
                getClass().getResource("/assets/iconos/solar_manager_logo.jpg").toExternalForm()
        );
        imgLogo.setImage(logo);
    }

    @FXML
    private void handleAceptar() {

        String email = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            lblMensaje.setText("Introduce el usuario y la contraseña.");
            return;
        }

        try {
            UsuarioService service = new UsuarioService();

            boolean ok = service.login(email, password);

            if (!ok) {
                lblMensaje.setText("Credenciales incorrectas.");
                return;
            }

            // Obtener usuario completo (incluye el rol)
            JSONObject usuario = service.obtenerUsuarioPorEmail(email);

            if (usuario == null) {
                lblMensaje.setText("Error inesperado: usuario no encontrado.");
                return;
            }

            // Guardar sesión
            SessionManager.setUsuario(usuario);

            String rol = usuario.getString("rol");

            // 🔥 AQUÍ ES LO QUE FALTABA
            if (rol.equalsIgnoreCase("admin")) {

                SessionContext.setRol(SessionContext.Rol.ADMIN);

                cargarPantalla("/components/pantallas/erp/plantillaGeneral/plantillaGeneral.fxml");

            } else if (rol.equalsIgnoreCase("comercial")) {

                SessionContext.setRol(SessionContext.Rol.COMERCIAL);

                cargarPantalla("/components/pantallas/comercial/pantallaGeneral/pantallaGeneral.fxml");

            } else {
                lblMensaje.setText("Rol desconocido: " + rol);
            }

        } catch (Exception e) {
            lblMensaje.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBorrar() {
        txtUsuario.clear();
        txtPassword.clear();
        lblMensaje.setText("");
    }

    private void cargarPantalla(String rutaFXML) {
        try {
            URL archivoFXML = getClass().getResource(rutaFXML);

            System.out.println("DEBUG URL = " + archivoFXML);

            if (archivoFXML == null) {
                lblMensaje.setText("No se encontró el archivo FXML:\n" + rutaFXML);
                System.out.println("ERROR → No se encontró el FXML: " + rutaFXML);
                return;
            }

            Parent root = FXMLLoader.load(archivoFXML);

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            lblMensaje.setText("Error cargando pantalla: " + e.getMessage());
            e.printStackTrace();
        }
    }
}