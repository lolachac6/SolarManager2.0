package solarmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal que inicia la aplicación Solar Manager.
 * Carga la plantilla general del dashboard.
 */
public class SolarManager extends Application {

    /**
     * Método que inicia JavaFX
     */
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/components/plantillaGeneral/PlantillaGeneral.fxml")
        );

        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("Solar Manager");
        stage.setMaximized(true);   // abre en pantalla completa
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Método main que lanza la aplicación
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

}