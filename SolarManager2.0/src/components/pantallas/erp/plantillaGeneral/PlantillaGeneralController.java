package components.pantallas.erp.plantillaGeneral;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.stage.Stage;

public class PlantillaGeneralController implements Initializable {

    @FXML private Button btnClientes;
    @FXML private Button btnComerciales;
    @FXML private Button btnProveedores;
    @FXML private Button btnStock;
    @FXML private Button btnPresupuestos;
    @FXML private Button btnInstalaciones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        cargarIcono(btnClientes, "/assets/iconos/clientes.jpg");
        cargarIcono(btnComerciales, "/assets/iconos/comerciales.jpg");
        cargarIcono(btnProveedores, "/assets/iconos/proveedores.jpg");
        cargarIcono(btnStock, "/assets/iconos/stock.jpg");
        cargarIcono(btnPresupuestos, "/assets/iconos/presupuestos.jpg");
        cargarIcono(btnInstalaciones, "/assets/iconos/instalaciones.jpg");
    }

    // =========================
    // CARGA ICONOS
    // =========================

    private void cargarIcono(Button boton, String rutaIcono) {

        URL recurso = getClass().getResource(rutaIcono);

        if (recurso == null) {
            System.out.println("No se encontró la imagen: " + rutaIcono);
            return;
        }

        Image imagen = new Image(recurso.toExternalForm(), true);
        ImageView imageView = new ImageView(imagen);

        imageView.fitWidthProperty().bind(boton.widthProperty().multiply(0.6));
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        boton.setGraphic(imageView);
        boton.setContentDisplay(ContentDisplay.TOP);
        boton.setGraphicTextGap(15);
    }

    // =========================
    // MÉTODO GENERAL DE NAVEGACIÓN
    // =========================

    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Stage stage = (Stage) nodo.getScene().getWindow();
            
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // NAVEGACIÓN (MENÚ + DASHBOARD)
    // =========================

    @FXML
    private void irClientes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }

    @FXML
    private void irComerciales(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaComerciales/pantallaComerciales.fxml");
    }

    @FXML
    private void irProveedores(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaProveedor/pantallaProveedor.fxml");
    }

    @FXML
    private void irStock(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }

    @FXML
    private void irPresupuestos(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }
    
    @FXML
    private void irInstalaciones(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml");
    }

    @FXML
    private void irInformes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }
    
    @FXML
    private void irInstalaciones(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml");
    }
}