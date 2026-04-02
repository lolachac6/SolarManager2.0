package components.pantallas.comercial.pantallaGeneral;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.application.Platform;

/**
 * Controller de la pantalla general comercial
 */
public class PantallaGeneralController implements Initializable {

    // =========================
    // BOTONES DASHBOARD
    // =========================

    @FXML private Button btnClientes;
    @FXML private Button btnInstalaciones;

    // =========================
    // INITIALIZE
    // =========================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // 🔥 IMPORTANTE: esperar a que la UI esté renderizada
        Platform.runLater(() -> {
            cargarIcono(btnClientes, "/assets/iconos/clientes.jpg");
            cargarIcono(btnInstalaciones, "/assets/iconos/instalaciones.jpg");
        });
    }

    // =========================
    // CARGA ICONOS (ESTABLE)
    // =========================

    private void cargarIcono(Button boton, String rutaIcono) {

        try {
            URL recurso = getClass().getResource(rutaIcono);

            if (recurso == null) {
                System.out.println("❌ No se encontró la imagen: " + rutaIcono);
                return;
            }

            Image imagen = new Image(recurso.toExternalForm());
            ImageView imageView = new ImageView(imagen);

            // 🔥 Tamaño controlado (evita bugs al volver)
            double size = boton.getWidth() * 0.6;

            if (size <= 0) {
                size = 120; // fallback seguro
            }

            imageView.setFitWidth(size);
            imageView.setPreserveRatio(true);

            boton.setGraphic(imageView);
            boton.setContentDisplay(ContentDisplay.TOP);
            boton.setGraphicTextGap(15);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // DASHBOARD → NAVEGACIÓN
    // =========================

    @FXML
    private void abrirAltaCliente(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/comercial/pantallaAltaCliente/AltaCliente.fxml");
    }

    @FXML
    private void abrirCalculoInstalacion(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/comercial/calculoInstalacion/CalculoInstalacion.fxml");
    }

    // =========================
    // MENÚ LATERAL (SIN LÓGICA AÚN)
    // =========================

    @FXML
    private void mostrarClientes(ActionEvent e) {
        System.out.println("👉 Mostrar tabla CLIENTES");
    }

    @FXML
    private void mostrarInstalaciones(ActionEvent e) {
        System.out.println("👉 Mostrar tabla INSTALACIONES");
    }

    @FXML
    private void mostrarPresupuestos(ActionEvent e) {
        System.out.println("👉 Mostrar tabla PRESUPUESTOS");
    }

    // =========================
    // MÉTODO CAMBIO PANTALLA
    // =========================

    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException ex) {
        }
    }
}