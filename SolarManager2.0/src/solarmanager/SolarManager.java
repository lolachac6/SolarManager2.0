package solarmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Arranque de la aplicación
 */
public class SolarManager extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource(
                "/components/pantallas/comun/pantallaLogin/PantallaLogin.fxml"
            )
        );

        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("Solar Manager - Login");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}