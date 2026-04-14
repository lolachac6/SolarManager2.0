package components.erp.erp.pantallaAltaPresupuesto;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import utils.AlertasSolarManager;

/**
 * Controlador de la pantalla de alta de presupuestos.
 *
 * <p>Gestiona la carga inicial de datos, la validación del formulario,
 * el guardado del presupuesto y la navegación asociada a la pantalla.</p>
 *
 * @author Iván
 */
public class pantallaAltaPresupuestoController implements Initializable {

    @FXML private ComboBox<String> cmbCliente;
    @FXML private ComboBox<String> cmbComercial;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private DatePicker dpFecha;

    @FXML private TextField txtPotencia;
    @FXML private TextField txtPaneles;
    @FXML private TextField txtProduccion;
    @FXML private TextField txtAhorro;

    /**
     * Inicializa el controlador y carga los datos por defecto del formulario.
     *
     * @param url URL de inicialización
     * @param rb recursos asociados
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dpFecha.setValue(LocalDate.now());
        cargarClientes();
        cargarComerciales();
        cargarEstados();
    }

    /**
     * Carga los clientes disponibles en el combo.
     */
    private void cargarClientes() {
        cmbCliente.getItems().addAll("Cliente 1", "Cliente 2", "Cliente 3");
    }

    /**
     * Carga los comerciales disponibles en el combo.
     */
    private void cargarComerciales() {
        cmbComercial.getItems().addAll("Iván", "Juan", "Pedro");
    }

    /**
     * Carga los estados disponibles en el combo.
     */
    private void cargarEstados() {
        cmbEstado.getItems().addAll("BORRADOR", "ENVIADO", "ACEPTADO", "RECHAZADO");
    }

    /**
     * Guarda un presupuesto si los datos del formulario son válidos.
     */
    @FXML
    private void guardarPresupuesto() {

        if (!validarCampos()) {
            return;
        }

        String cliente = cmbCliente.getValue();
        String comercial = cmbComercial.getValue();
        String estado = cmbEstado.getValue();
        LocalDate fecha = dpFecha.getValue();

        double potencia = Double.parseDouble(txtPotencia.getText());
        int paneles = Integer.parseInt(txtPaneles.getText());
        double produccion = Double.parseDouble(txtProduccion.getText());
        double ahorro = Double.parseDouble(txtAhorro.getText());

        System.out.println("=== PRESUPUESTO ===");
        System.out.println("Cliente: " + cliente);
        System.out.println("Comercial: " + comercial);
        System.out.println("Estado: " + estado);
        System.out.println("Potencia: " + potencia);

        AlertasSolarManager.presupuestoCreadoCorrectamente();
        limpiarFormulario();
    }

    /**
     * Valida los campos obligatorios y los formatos numéricos del formulario.
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
    private boolean validarCampos() {

        if (cmbCliente.getValue() == null) {
            AlertasSolarManager.seleccionarClientePresupuesto();
            return false;
        }

        if (cmbComercial.getValue() == null) {
            AlertasSolarManager.seleccionarComercialPresupuesto();
            return false;
        }

        if (dpFecha.getValue() == null) {
            AlertasSolarManager.seleccionarFechaPresupuesto();
            return false;
        }

        if (!esNumero(txtPotencia.getText())) {
            AlertasSolarManager.potenciaNoValida();
            return false;
        }

        if (!esEntero(txtPaneles.getText())) {
            AlertasSolarManager.numeroPanelesNoValido();
            return false;
        }

        if (!esNumero(txtProduccion.getText())) {
            AlertasSolarManager.produccionNoValida();
            return false;
        }

        if (!esNumero(txtAhorro.getText())) {
            AlertasSolarManager.ahorroNoValido();
            return false;
        }

        return true;
    }

    /**
     * Comprueba si un texto puede convertirse a número decimal.
     *
     * @param valor valor a comprobar
     * @return true si el valor es numérico, false en caso contrario
     */
    private boolean esNumero(String valor) {
        try {
            Double.parseDouble(valor);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Comprueba si un texto puede convertirse a número entero.
     *
     * @param valor valor a comprobar
     * @return true si el valor es entero, false en caso contrario
     */
    private boolean esEntero(String valor) {
        try {
            Integer.parseInt(valor);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Limpia el formulario y restablece sus valores por defecto.
     */
    private void limpiarFormulario() {
        cmbCliente.setValue(null);
        cmbComercial.setValue(null);
        cmbEstado.setValue(null);

        dpFecha.setValue(LocalDate.now());

        txtPotencia.clear();
        txtPaneles.clear();
        txtProduccion.clear();
        txtAhorro.clear();
    }

    /**
     * Cancela la operación actual y vuelve a la pantalla anterior.
     *
     * @param event evento de acción
     */
    @FXML
    private void cancelar(ActionEvent event) {
        volver(event);
    }

    /**
     * Vuelve a la pantalla general.
     *
     * @param event evento de acción
     */
    @FXML
    private void volver(ActionEvent event) {
        cambiarPantalla(event, "/components/pantallas/erp/pantallaGeneral/pantallaGeneral.fxml");
    }

    /**
     * Cambia la escena actual por otra indicada mediante su ruta FXML.
     *
     * @param event evento de acción
     * @param rutaFXML ruta del fichero FXML
     */
    private void cambiarPantalla(ActionEvent event, String rutaFXML) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            AlertasSolarManager.errorCambioPantalla();
        }
    }
}