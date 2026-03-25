package components.pantallas.erp.pantallaProveedor;

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

public class PantallaProveedorController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Solo UI
    }

    // =========================
    // MÉTODO GENERAL DE NAVEGACIÓN (MEJORADO)
    // =========================

    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {

            URL resource = getClass().getResource(rutaFXML);

            if (resource == null) {
                System.out.println("❌ No se encontró: " + rutaFXML);
                return;
            }

            Parent root = FXMLLoader.load(resource);

            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
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

    // 🔥 ESTE FALTABA
    @FXML
    private void irInformes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }
}