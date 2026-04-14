package components.pantallas.erp.pantallaInformes;

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

/**
 * Controlador de la pantalla de informes.
 *
 * <p>Gestiona únicamente la navegación entre pantallas del módulo ERP.</p>
 *
 * @author Iván
 */
public class PantallaInformesController implements Initializable {

    /**
     * Inicializa el controlador.
     *
     * @param url URL de inicialización
     * @param rb recursos asociados
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * Cambia la pantalla actual por otra indicada mediante su ruta FXML.
     *
     * @param nodo nodo origen
     * @param rutaFXML ruta del fichero FXML
     */
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

    /**
     * Vuelve a la plantilla general.
     *
     * @param e evento de acción
     */
    @FXML
    private void volverInicio(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }

    /**
     * Navega a la pantalla de clientes.
     *
     * @param e evento de acción
     */
    @FXML
    private void irClientes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }

    /**
     * Navega a la pantalla de comerciales.
     *
     * @param e evento de acción
     */
    @FXML
    private void irComerciales(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }

    /**
     * Navega a la pantalla de proveedores.
     *
     * @param e evento de acción
     */
    @FXML
    private void irProveedores(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaProveedor/PantallaProveedor.fxml");
    }

    /**
     * Navega a la pantalla de stock.
     *
     * @param e evento de acción
     */
    @FXML
    private void irStock(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }

    /**
     * Navega a la pantalla de presupuestos.
     *
     * @param e evento de acción
     */
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
}