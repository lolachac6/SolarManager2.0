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

import javafx.scene.layout.AnchorPane;

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
    // PANEL DINÁMICO
    // =========================
    @FXML private AnchorPane panelTabla;

    // =========================
    // INITIALIZE
    // =========================
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Platform.runLater(() -> {
            cargarIcono(btnClientes, "/assets/iconos/clientes.jpg");
            cargarIcono(btnInstalaciones, "/assets/iconos/instalaciones.jpg");
        });
    }

    // =========================
    // ICONOS
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

            double size = boton.getWidth() * 0.6;

            if (size <= 0) size = 120;

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
    // DASHBOARD
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
    // MENU LATERAL → CARGA COMPONENTES
    // =========================
    @FXML
    private void mostrarClientes(ActionEvent e) {
        cargarEnPanel("/components/tablaClientes/tablaClientes.fxml");
    }

    @FXML
    private void mostrarInstalaciones(ActionEvent e) {
        System.out.println("👉 Instalaciones aún no implementado");
    }

    @FXML
    private void mostrarPresupuestos(ActionEvent e) {
        cargarEnPanel("/components/tablaPresupuestos/tablaPresupuestos.fxml");
    }

    // =========================
    // CARGAR COMPONENTE EN PANEL
    // =========================
    private void cargarEnPanel(String rutaFXML) {
        try {

            URL resource = getClass().getResource(rutaFXML);

            if (resource == null) {
                System.out.println("❌ No se encontró el FXML: " + rutaFXML);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Node contenido = loader.load();

            panelTabla.getChildren().clear();
            panelTabla.getChildren().add(contenido);

            
            AnchorPane.setTopAnchor(contenido, 0.0);
            AnchorPane.setBottomAnchor(contenido, 0.0);
            AnchorPane.setLeftAnchor(contenido, 0.0);
            AnchorPane.setRightAnchor(contenido, 0.0);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // =========================
    // CAMBIO DE PANTALLA
    // =========================
    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {

            URL resource = getClass().getResource(rutaFXML);

            if (resource == null) {
                System.out.println("❌ Ruta incorrecta: " + rutaFXML);
                return;
            }

            Parent root = FXMLLoader.load(resource);

            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException ex) {
        }
    }
}