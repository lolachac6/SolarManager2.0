package components.pantallas.erp.plantillaGeneral;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal que inicia la aplicación Solar Manager.
 *
 * <p>
 * Esta clase carga la interfaz principal definida en el archivo
 * PlantillaGeneral.fxml y abre la aplicación en pantalla completa.
 * </p>
 *
 * <p>
 * La interfaz se redimensiona automáticamente gracias al uso de
 * layouts adaptativos como BorderPane, VBox y GridPane.
 * </p>
 *
 * @author ivang
 */
public class NewFXMain extends Application {

    /**
     * Método que inicia la interfaz gráfica de JavaFX.
     *
     * @param primaryStage ventana principal de la aplicación
     * @throws Exception si ocurre un error al cargar el FXML
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/components//pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml")
        );

        Parent root = loader.load();

        Scene scene = new Scene(root);

        primaryStage.setTitle("Solar Manager");

        // abrir en pantalla completa
        primaryStage.setMaximized(true);

        // permitir redimensionar
        primaryStage.setResizable(false);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Punto de entrada de la aplicación.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}