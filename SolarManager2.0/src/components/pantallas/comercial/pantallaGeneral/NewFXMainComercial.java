package components.pantallas.comercial.pantallaGeneral;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main de la pantalla Comercial
 */
public class NewFXMainComercial extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(
                getClass().getResource("/components/pantallas/comercial/pantallaGeneral/pantallaGeneral.fxml")
        );

        Scene scene = new Scene(root, 1200, 800);

        primaryStage.setTitle("Solar Manager - Comercial");

        // Tamaño fijo como en el FXML
        primaryStage.setResizable(false);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}