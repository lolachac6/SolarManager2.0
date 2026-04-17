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
import javafx.scene.control.ListCell;
import modelo.Cliente;
import modelo.Comercial;
import modelo.InstalacionFotovoltaica;
import modelo.Presupuesto;

public class PantallaAltaPresupuestoController implements Initializable {

    // =========================
    // CAMPOS FXML
    // =========================
@FXML private ComboBox<Cliente> cmbCliente;
@FXML private ComboBox<Comercial> cmbComercial;
@FXML private ComboBox<Presupuesto.EstadoPresupuesto> cmbEstado;

    @FXML private DatePicker dpFecha;

    @FXML private TextField txtPotencia;
    @FXML private TextField txtPaneles;
    @FXML private TextField txtProduccion;
    @FXML private TextField txtAhorro;
    
    private Presupuesto presupuesto;
    
    

    // =========================
    // INIT
    // =========================
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Fecha actual por defecto
        dpFecha.setValue(LocalDate.now());

        // Cargar combos (mock por ahora)
      //  cargarClientes();
      //  cargarComerciales();
        cargarEstados();
        
        
         // Mostrar nombre en vez de objeto
        cmbCliente.setCellFactory(cb -> new ListCell<Cliente>() {
         @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
         });

        cmbCliente.setButtonCell(new ListCell<Cliente>() {
          @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
         });

        cmbComercial.setCellFactory(cb -> new ListCell<Comercial>() {
            @Override
            protected void updateItem(Comercial item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });

        cmbComercial.setButtonCell(new ListCell<Comercial>() {
            @Override
            protected void updateItem(Comercial item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
 
  }
    
    
    //===========================
    //SET PRESUPUESTO (EDITAR)
    //===========================
  public void setPresupuesto(Presupuesto presupuesto) {
    this.presupuesto = presupuesto;

    if (presupuesto != null) {

        // Seleccionar cliente por ID
        cmbCliente.getSelectionModel().select(
            cmbCliente.getItems().stream()
                .filter(c -> c.getId().equals(presupuesto.getIdCliente()))
                .findFirst()
                .orElse(null)
        );

        // Seleccionar comercial por ID
        cmbComercial.getSelectionModel().select(
            cmbComercial.getItems().stream()
                .filter(c -> c.getId().equals(presupuesto.getIdComercial()))
                .findFirst()
                .orElse(null)
        );

        // Estado (ya es enum)
        cmbEstado.setValue(presupuesto.getEstado());

        // Fecha
        dpFecha.setValue(presupuesto.getFechaCreacion());

        // Instalación
        if (presupuesto.getInstalacion() != null) {
            txtPotencia.setText(String.valueOf(presupuesto.getInstalacion().getPotenciaInstalada()));
            txtPaneles.setText(String.valueOf(presupuesto.getInstalacion().getNumeroPaneles()));
            txtProduccion.setText(String.valueOf(presupuesto.getInstalacion().getProduccionEstimada()));
            txtAhorro.setText(String.valueOf(presupuesto.getInstalacion().getAhorroEstimado()));
        }
    }
}

    // =========================
    // CARGA DE DATOS (MOCK)
    // =========================
  /** private void cargarClientes() {cmbCliente.getItems().addAll("Clientes");
}

    private void cargarComerciales() {
     cmbComercial.getItems().addAll(
        new Comercial("1", "Iván"),
        new Comercial("2", "Juan")
    );
}*/

private void cargarEstados() {
    cmbEstado.getItems().addAll(Presupuesto.EstadoPresupuesto.values());
}
    
    

    // =========================
    // GUARDAR PRESUPUESTO
    // =========================
    @FXML
private void guardarPresupuesto() {

    if (!validarCampos()) {
        return;
    }

    // Obtener objetos reales
    Cliente cliente = cmbCliente.getValue();
    Comercial comercial = cmbComercial.getValue();
    Presupuesto.EstadoPresupuesto estado = cmbEstado.getValue();
    LocalDate fecha = dpFecha.getValue();

    double potencia = Double.parseDouble(txtPotencia.getText());
    int paneles = Integer.parseInt(txtPaneles.getText());
    double produccion = Double.parseDouble(txtProduccion.getText());
    double ahorro = Double.parseDouble(txtAhorro.getText());

    // Crear instalación
    InstalacionFotovoltaica instalacion = new InstalacionFotovoltaica();
    instalacion.setPotenciaInstalada(potencia);
    instalacion.setNumeroPaneles(paneles);
    instalacion.setProduccionEstimada(produccion);
    instalacion.setAhorroEstimado(ahorro);

    // CREAR o EDITAR
    if (presupuesto == null) {
        presupuesto = new Presupuesto();
    }

    // Setear datos
    presupuesto.setIdCliente(cliente.getId());
    presupuesto.setIdComercial(comercial.getId());
    presupuesto.setFechaCreacion(fecha);
    presupuesto.setEstado(estado);
    presupuesto.setInstalacion(instalacion);

    // DEBUG
    System.out.println("=== PRESUPUESTO ===");
    System.out.println(presupuesto);

    mostrarAlerta(
        presupuesto.getId() == null 
            ? "Presupuesto creado correctamente" 
            : "Presupuesto actualizado correctamente",
        AlertType.INFORMATION
    );

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