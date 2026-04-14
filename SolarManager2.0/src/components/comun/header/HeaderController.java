package components.comun.header;

import ConexionSupabase.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    @FXML
    private Label lblUsuario;

    @FXML
    private MenuButton btnUsuario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        JSONObject usuario = SessionManager.getUsuario();

        if (usuario != null) {

            String nombre = usuario.optString("nombre", "Usuario");
            String rol = usuario.optString("rol", "");

            lblUsuario.setText(nombre + " (" + rol + ")");

        } else {
            lblUsuario.setText("Sin sesión");
        }
    }

    @FXML
    private void logout() {

        // 🔥 alerta de confirmación
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION
        );

        alert.setTitle("Cerrar sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro que quieres cerrar sesión?");

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {

            // 1. limpiar sesión
            SessionManager.cerrarSesion();

            try {
                // 2. volver a login
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/components/pantallas/comun/pantallaLogin/pantallaLogin.fxml")
                );

                javafx.scene.Parent root = loader.load();

                // 3. cambiar escena
                javafx.stage.Stage stage = (javafx.stage.Stage) btnUsuario.getScene().getWindow();
                javafx.scene.Scene scene = new javafx.scene.Scene(root);

                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
