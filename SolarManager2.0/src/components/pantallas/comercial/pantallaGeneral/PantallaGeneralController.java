package components.pantallas.comercial.pantallaGeneral;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller de la pantalla general comercial
 */
public class PantallaGeneralController implements Initializable {

    // 🔹 BOTONES DASHBOARD
    @FXML private Button btnClientes;
    @FXML private Button btnPresupuestos;
    @FXML private Button btnInstalaciones;

    // TABLA CLIENTES
    @FXML private TableView<?> tablaClientes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Cargar iconos 
        cargarIcono(btnClientes, "/assets/iconos/clientes.jpg");
        cargarIcono(btnPresupuestos, "/assets/iconos/presupuestos.jpg");
        cargarIcono(btnInstalaciones, "/assets/iconos/instalaciones.jpg");

        // (Opcional) aquí luego cargarás datos reales
    }

    /**
     * Método reutilizable para asignar iconos a botones
     */
    private void cargarIcono(Button boton, String rutaIcono) {

        URL recurso = getClass().getResource(rutaIcono);

        if (recurso == null) {
            System.out.println("No se encontró la imagen: " + rutaIcono);
            return;
        }

        Image imagen = new Image(recurso.toExternalForm(), true);

        ImageView imageView = new ImageView(imagen);

        // el icono ocupará aprox el 60% del botón
        imageView.fitWidthProperty().bind(boton.widthProperty().multiply(0.6));

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        boton.setGraphic(imageView);
        boton.setContentDisplay(ContentDisplay.TOP);
        boton.setGraphicTextGap(15);
    }

    // EVENTOS BOTONES

    @FXML
    public void abrirClientes(ActionEvent event) {
        System.out.println("Abrir Clientes");
    }

    @FXML
    public void abrirPresupuestos(ActionEvent event) {
        System.out.println("Abrir Presupuestos");
    }

    @FXML
    public void abrirInstalaciones(ActionEvent event) {
        System.out.println("Abrir Instalaciones");
    }
}