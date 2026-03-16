package components.pantallas.erp.plantillaGeneral;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controlador de la pantalla principal del sistema Solar Manager.
 *
 * Esta pantalla actúa como dashboard de la aplicación y permite acceder
 * a los distintos módulos mediante botones con iconos.
 *
 * Los iconos se cargan desde:
 * src/assets/iconos/
 *
 * @author ivang
 */
public class PlantillaGeneralController implements Initializable {

    @FXML
    private Button btnClientes;

    @FXML
    private Button btnComerciales;

    @FXML
    private Button btnProveedores;

    @FXML
    private Button btnStock;

    @FXML
    private Button btnPresupuestos;

    @FXML
    private Button btnInformes;

    /**
     * Inicializa la pantalla y carga los iconos en los botones.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        cargarIcono(btnClientes, "/assets/iconos/clientes.jpg");
        cargarIcono(btnComerciales, "/assets/iconos/comerciales.jpg");
        cargarIcono(btnProveedores, "/assets/iconos/proveedores.jpg");
        cargarIcono(btnStock, "/assets/iconos/stock.jpg");
        cargarIcono(btnPresupuestos, "/assets/iconos/presupuestos.jpg");
        cargarIcono(btnInformes, "/assets/iconos/informes.jpg");

    }

    /**
     * Carga un icono dentro de un botón y lo escala dinámicamente
     * para que funcione bien en pantalla completa.
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

    @FXML
    private void abrirClientes(ActionEvent event) {
        System.out.println("Abrir pantalla Clientes");
    }

    @FXML
    private void abrirComerciales(ActionEvent event) {
        System.out.println("Abrir pantalla Comerciales");
    }

    @FXML
    private void abrirProveedores(ActionEvent event) {
        System.out.println("Abrir pantalla Proveedores");
    }

    @FXML
    private void abrirStock(ActionEvent event) {
        System.out.println("Abrir pantalla Stock");
    }

    @FXML
    private void abrirPresupuestos(ActionEvent event) {
        System.out.println("Abrir pantalla Presupuestos");
    }

    @FXML
    private void abrirInformes(ActionEvent event) {
        System.out.println("Abrir pantalla Informes");
    }

}