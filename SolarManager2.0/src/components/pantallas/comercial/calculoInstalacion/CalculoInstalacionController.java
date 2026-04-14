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
    @FXML private TextField IdCliente;

    @FXML private CheckBox chkBateria;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    // =========================
    // NAVEGACIÓN
    // =========================

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

    @FXML
    private void volverInicio(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/comercial/pantallaGeneral/PantallaGeneral.fxml");
    }

    // =========================
    // GUARDAR INSTALACIÓN
    // =========================

    @FXML
    private void guardarInstalacion(ActionEvent event) {
        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Instalaciones");

            Document instalacion = new Document()
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

        chkBateria.setSelected(false);
    }

    // =========================
    // CANCELAR
    // =========================

    @FXML
    private void cancelar(ActionEvent event) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmación");
        confirm.setHeaderText("Salir sin guardar");
        confirm.setContentText("¿Desea salir sin guardar?");

        if (confirm.showAndWait().get() == ButtonType.OK) {

            cambiarPantalla((Node) event.getSource(),
                "/components/pantallas/------- Aqui metemos pagina segun rol--------");
        }
    }

    // =========================
    // CALCULAR (SIN IMPLEMENTAR)
    // =========================

    @FXML
    private void calcularInstalacion(ActionEvent event) {
        // ------Aqui metemos el codigo de la API-------
        
        mostrarAlerta("Falta por desarrollar el metodo, codigo API", Alert.AlertType.INFORMATION);
    }

    // =========================
    // ALERTAS
    // =========================

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}