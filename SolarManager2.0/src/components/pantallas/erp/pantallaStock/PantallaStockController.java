package components.pantallas.erp.pantallaStock;

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

public class PantallaStockController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Solo UI
    }

    // =========================
    // MÉTODO GENERAL DE NAVEGACIÓN (ROBUSTO)
    // =========================

    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
private void abrirAltaProducto(javafx.event.ActionEvent e) {

    try {

        URL resource = getClass().getResource(
            "/components/pantallas/erp/pantallaAltaProducto/PantallaAltaProducto.fxml"
        );

        if (resource == null) {
            System.out.println("❌ No se encontró el FXML de alta de producto");
            return;
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));

        stage.setTitle("Alta Producto");
        stage.setResizable(false);

        // 🔥 Modal (bloquea pantalla de fondo)
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        stage.centerOnScreen();

        stage.showAndWait(); // 🔥 espera a que se cierre

    } catch (IOException ex) {
        System.out.println("❌ Error abriendo alta de producto");
        ex.printStackTrace();
    }
}

    // =========================
    // BOTONES
    // =========================

    @FXML
    private void volverInicio(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }

    @FXML
    private void irClientes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }

    @FXML
    private void irComerciales(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }

    @FXML
    private void irProveedores(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaProveedor/PantallaProveedor.fxml");
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

    // 🔥 ESTE ERA EL QUE FALTABA
    @FXML
    private void irInformes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }
}