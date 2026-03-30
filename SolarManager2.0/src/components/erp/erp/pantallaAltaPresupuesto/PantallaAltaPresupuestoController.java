package components.erp.erp.pantallaAltaPresupuesto;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PantallaAltaPresupuestoController implements Initializable {

    // =========================
    // CAMPOS FXML
    // =========================
    @FXML private ComboBox<String> cmbCliente;
    @FXML private ComboBox<String> cmbComercial;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private DatePicker dpFecha;

    @FXML private TextField txtPotencia;
    @FXML private TextField txtPaneles;
    @FXML private TextField txtProduccion;
    @FXML private TextField txtAhorro;

    // =========================
    // INIT
    // =========================
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Fecha actual por defecto
        dpFecha.setValue(LocalDate.now());

        // Cargar combos (mock por ahora)
        cargarClientes();
        cargarComerciales();
        cargarEstados();
    }

    // =========================
    // CARGA DE DATOS (MOCK)
    // =========================
    private void cargarClientes() {
        cmbCliente.getItems().addAll("Cliente 1", "Cliente 2", "Cliente 3");
    }

    private void cargarComerciales() {
        cmbComercial.getItems().addAll("Iván", "Juan", "Pedro");
    }

    private void cargarEstados() {
        cmbEstado.getItems().addAll("BORRADOR", "ENVIADO", "ACEPTADO", "RECHAZADO");
    }

    // =========================
    // GUARDAR PRESUPUESTO
    // =========================
    @FXML
    private void guardarPresupuesto() {

        if (!validarCampos()) {
            return;
        }

        // Obtener datos
        String cliente = cmbCliente.getValue();
        String comercial = cmbComercial.getValue();
        String estado = cmbEstado.getValue();
        LocalDate fecha = dpFecha.getValue();

        double potencia = Double.parseDouble(txtPotencia.getText());
        int paneles = Integer.parseInt(txtPaneles.getText());
        double produccion = Double.parseDouble(txtProduccion.getText());
        double ahorro = Double.parseDouble(txtAhorro.getText());

        // DEBUG (simulación)
        System.out.println("=== PRESUPUESTO ===");
        System.out.println("Cliente: " + cliente);
        System.out.println("Comercial: " + comercial);
        System.out.println("Estado: " + estado);
        System.out.println("Potencia: " + potencia);

        // TODO: guardar en BD (DAO / Service)

        mostrarAlerta("Presupuesto creado correctamente", AlertType.INFORMATION);

        limpiarFormulario();
    }

    // =========================
    // VALIDACIONES
    // =========================
    private boolean validarCampos() {

        if (cmbCliente.getValue() == null) {
            mostrarAlerta("Seleccione un cliente", AlertType.WARNING);
            return false;
        }

        if (cmbComercial.getValue() == null) {
            mostrarAlerta("Seleccione un comercial", AlertType.WARNING);
            return false;
        }

        if (dpFecha.getValue() == null) {
            mostrarAlerta("Seleccione una fecha", AlertType.WARNING);
            return false;
        }

        if (!esNumero(txtPotencia.getText())) {
            mostrarAlerta("Potencia no válida", AlertType.WARNING);
            return false;
        }

        if (!esEntero(txtPaneles.getText())) {
            mostrarAlerta("Número de paneles no válido", AlertType.WARNING);
            return false;
        }

        if (!esNumero(txtProduccion.getText())) {
            mostrarAlerta("Producción no válida", AlertType.WARNING);
            return false;
        }

        if (!esNumero(txtAhorro.getText())) {
            mostrarAlerta("Ahorro no válido", AlertType.WARNING);
            return false;
        }

        return true;
    }

    // =========================
    // VALIDADORES
    // =========================
    private boolean esNumero(String valor) {
        try {
            Double.parseDouble(valor);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean esEntero(String valor) {
        try {
            Integer.parseInt(valor);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // =========================
    // LIMPIAR
    // =========================
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

    // =========================
    // CANCELAR
    // =========================
    @FXML
    private void cancelar(ActionEvent event) {
        volver(event);
    }

    // =========================
    // VOLVER
    // =========================
    @FXML
    private void volver(ActionEvent event) {
        cambiarPantalla(event, "/components/pantallas/erp/pantallaGeneral/pantallaGeneral.fxml");
    }

    // =========================
    // NAVEGACIÓN
    // =========================
    private void cambiarPantalla(ActionEvent event, String rutaFXML) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cambiar de pantalla", AlertType.ERROR);
        }
    }

    // =========================
    // ALERTAS
    // =========================
    private void mostrarAlerta(String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Solar Manager");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}