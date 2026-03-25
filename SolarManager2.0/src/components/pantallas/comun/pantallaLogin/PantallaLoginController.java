package components.pantallas.comun.pantallaLogin;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PantallaLoginController implements Initializable {

    @FXML
    private ImageView imgLogo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Image logo = new Image(
                getClass().getResource("/assets/iconos/solar_manager_logo.jpg").toExternalForm()
        );

        imgLogo.setImage(logo);

    }

}