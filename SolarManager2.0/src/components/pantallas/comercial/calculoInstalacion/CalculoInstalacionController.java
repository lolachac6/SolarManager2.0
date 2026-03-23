package components.pantallas.comercial.calculoInstalacion;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class CalculoInstalacionController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

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

    @FXML
    private void volverInicio(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/comercial/pantallaGeneral/PantallaGeneral.fxml");
    }
}