package components.pantallas.comercial.pantallaGeneral;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

/**
 * Main de la pantalla Comercial
 */
public class NewFXMainComercial extends Application {

    @Override
    public void start(Stage primaryStage) {

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/components/pantallas/comercial/pantallaGeneral/pantallaGeneral.fxml")
            );

            Parent root = loader.load();

            //  Tamaño de pantalla real (sin barra de tareas)
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

            primaryStage.setTitle("Solar Manager - Comercial");

            // 👇 NO usar maximized
            primaryStage.setResizable(true);

            primaryStage.setScene(scene);

          
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}