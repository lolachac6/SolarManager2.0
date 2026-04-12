package components.pantallas.comercial.calculoInstalacion;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import javafx.scene.control.*;

import Integration.google.service.SolarService;
import components.navigation.SessionContext;
import modelo.ResultadoSolar;

/**
 * Controlador de la pantalla de cálculo de instalación fotovoltaica.
 * Gestiona la interacción entre la UI, la lógica de negocio y la persistencia.
 */
public class CalculoInstalacionController implements Initializable {

    // =========================
    // CAMPOS FXML
    // =========================

    @FXML private TextField txtCalle;
    @FXML private TextField txtNumero;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtProvincia;
    @FXML private TextField txtCodigoPostal;
    @FXML private TextField txtConsumoAnual;

    @FXML private TextField txtHorasSol;
    @FXML private TextField txtArea;
    @FXML private TextField txtMaxPaneles;
    @FXML private TextField txtPanelesNecesarios;
    @FXML private TextField txtEnergiaPanel;
    @FXML private TextField txtPresupuesto;
    @FXML private TextField txtIdCliente;

    @FXML private CheckBox chkBateria;

    // =========================
    // INICIALIZACIÓN
    // =========================

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Aquí podrías cargar consumo automáticamente desde BD si quieres
    }

    // =========================
    // NAVEGACIÓN
    // =========================

    /**
     * Cambia de pantalla cargando un nuevo FXML.
     */
    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));
            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Vuelve a la pantalla principal. NUEVO
     */
@FXML
private void volverInicio(ActionEvent e) {

    String destino;

    if (SessionContext.isAdmin()) {
        destino = "/components/pantallas/erp/plantillaGeneral/plantillaGeneral.fxml";
    } else {
        destino = "/components/pantallas/comercial/pantallaGeneral/pantallaGeneral.fxml";
    }

    cambiarPantalla((Node) e.getSource(), destino);
}

    // =========================
    // CALCULAR INSTALACIÓN
    // =========================

    /**
     * Llama al servicio solar para calcular la instalación y muestra los resultados.
     */
    @FXML
    private void calcularInstalacion(ActionEvent event) {

        try {

            // Validación básica
            if (txtCalle.getText().isEmpty() || txtCiudad.getText().isEmpty()) {
                mostrarAlerta("Debe completar la dirección", Alert.AlertType.WARNING);
                return;
            }

            if (txtConsumoAnual.getText().isEmpty()) {
                mostrarAlerta("Debe indicar el consumo anual", Alert.AlertType.WARNING);
                return;
            }

            String direccion = txtCalle.getText() + " " + txtNumero.getText() + ", "
                    + txtCiudad.getText() + ", "
                    + txtCodigoPostal.getText() + ", España";

            double consumo = Double.parseDouble(txtConsumoAnual.getText());

            SolarService service = new SolarService();
            ResultadoSolar resultado = service.calcularInstalacion(direccion, consumo);

            // Pintar resultados en UI
            txtHorasSol.setText(String.valueOf(Math.round(resultado.getHorasSol())));
            txtArea.setText(String.valueOf(Math.round(resultado.getArea())));
            txtMaxPaneles.setText(String.valueOf(resultado.getMaxPaneles()));
            txtPanelesNecesarios.setText(String.valueOf(resultado.getPanelesNecesarios()));
            txtEnergiaPanel.setText(String.valueOf(Math.round(resultado.getEnergiaPorPanel())));
            txtPresupuesto.setText(String.valueOf(resultado.getPresupuesto()));

            mostrarAlerta(
                    resultado.isAutosuficiente()
                            ? "Instalación autosuficiente"
                            : "No hay suficiente espacio en el tejado",
                    Alert.AlertType.INFORMATION
            );

        } catch (NumberFormatException e) {
            mostrarAlerta("El consumo debe ser un número válido", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error en el cálculo: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // =========================
    // GUARDAR INSTALACIÓN
    // =========================

    /**
     * Guarda la instalación en MongoDB.
     */
    @FXML
    private void guardarInstalacion(ActionEvent event) {
        try {

            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Instalaciones");

            Document instalacion = new Document()
                    .append("idCliente", txtIdCliente.getText())
                    .append("calle", txtCalle.getText())
                    .append("numero", txtNumero.getText())
                    .append("ciudad", txtCiudad.getText())
                    .append("provincia", txtProvincia.getText())
                    .append("codigoPostal", txtCodigoPostal.getText())
                    .append("consumoAnual", txtConsumoAnual.getText())
                    .append("horasSol", txtHorasSol.getText())
                    .append("area", txtArea.getText())
                    .append("maxPaneles", txtMaxPaneles.getText())
                    .append("panelesNecesarios", txtPanelesNecesarios.getText())
                    .append("energiaPanel", txtEnergiaPanel.getText())
                    .append("presupuesto", txtPresupuesto.getText())
                    .append("bateria", chkBateria.isSelected());

            coleccion.insertOne(instalacion);

            mostrarAlerta("Instalación guardada correctamente", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al guardar instalación", Alert.AlertType.ERROR);
        }
    }
    

    // =========================
    // LIMPIAR CAMPOS
    // =========================

    /**
     * Limpia todos los campos del formulario.
     */
    @FXML
    private void limpiarCampos(ActionEvent event) {
        txtCalle.clear();
        txtNumero.clear();
        txtCiudad.clear();
        txtProvincia.clear();
        txtCodigoPostal.clear();
        txtConsumoAnual.clear();

        txtHorasSol.clear();
        txtArea.clear();
        txtMaxPaneles.clear();
        txtPanelesNecesarios.clear();
        txtEnergiaPanel.clear();
        txtPresupuesto.clear();

        txtIdCliente.clear();
        chkBateria.setSelected(false);
    }

    // =========================
    // CANCELAR
    // =========================

    /**
     * Cancela la operación y vuelve atrás previa confirmación.
     */
    @FXML
    private void cancelar(ActionEvent event) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmación");
        confirm.setHeaderText("Salir sin guardar");
        confirm.setContentText("¿Desea salir sin guardar?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            cambiarPantalla((Node) event.getSource(),
                "/components/pantallas/comercial/pantallaGeneral/PantallaGeneral.fxml");
        }
    }

    // =========================
    // ALERTAS
    // =========================

    /**
     * Muestra una alerta al usuario.
     */
    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private static class txtNombre {

        private static void setText(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public txtNombre() {
        }
    }

    private static class txtApellidos {

        private static void setText(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public txtApellidos() {
        }
    }

    private static class txtTelefono {

        private static void setText(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public txtTelefono() {
        }
    }

    private static class txtEmail {

        private static void setText(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public txtEmail() {
        }
    }
}