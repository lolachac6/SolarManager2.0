package components.pantallas.pantallaDetalleInstalaciones;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Direccion;
import modelo.InstalacionFotovoltaica;

/**
 * Controlador de la pantalla de detalle de instalaciones.
 *
 * Se encarga de mostrar todos los datos de la instalación recibida desde la
 * pantalla de instalaciones, incluyendo el nombre visible del cliente.
 *
 * @author Iván
 */
public class PantallaDetalleInstalacionesController {

    @FXML private TextField txtId;
    @FXML private TextField txtIdCliente;
    @FXML private TextField txtPotencia;
    @FXML private TextField txtNumeroPaneles;
    @FXML private TextField txtProduccion;
    @FXML private TextField txtAhorro;
    @FXML private TextField txtInversor;
    @FXML private TextField txtBateria;
    @FXML private TextField txtCalle;
    @FXML private TextField txtNumero;
    @FXML private TextField txtCodigoPostal;
    @FXML private TextField txtMunicipio;
    @FXML private TextField txtProvincia;

    /**
     * Carga los datos de una instalación en la vista de detalle.
     *
     * @param instalacion instalación seleccionada
     * @param nombreCliente nombre visible del cliente
     */
    public void cargarInstalacion(InstalacionFotovoltaica instalacion, String nombreCliente) {

        if (instalacion == null) {
            return;
        }

        txtId.setText(instalacion.getId() != null ? instalacion.getId() : "");
        txtIdCliente.setText(nombreCliente != null ? nombreCliente : "");
        txtPotencia.setText(String.valueOf(instalacion.getPotenciaInstalada()));
        txtNumeroPaneles.setText(String.valueOf(instalacion.getNumeroPaneles()));
        txtProduccion.setText(String.valueOf(instalacion.getProduccionEstimada()));
        txtAhorro.setText(String.valueOf(instalacion.getAhorroEstimado()));
        txtInversor.setText(instalacion.getInversor() != null ? instalacion.getInversor() : "" + " x 5");
        txtBateria.setText(instalacion.getBateria() ? "Sí" : "No");

        Direccion d = instalacion.getDireccion();

        if (d != null) {
            txtCalle.setText(d.getCalle() != null ? d.getCalle() : "");
            txtNumero.setText(d.getNumero() != null ? d.getNumero() : "");
            txtCodigoPostal.setText(d.getCodigoPostal() != null ? d.getCodigoPostal() : "");
            txtMunicipio.setText(d.getMunicipio() != null ? d.getMunicipio() : "");
            txtProvincia.setText(d.getProvincia() != null ? d.getProvincia() : "");
        } else {
            txtCalle.setText("");
            txtNumero.setText("");
            txtCodigoPostal.setText("");
            txtMunicipio.setText("");
            txtProvincia.setText("");
        }
    }

    /**
     * Cierra la ventana actual.
     */
    @FXML
    private void cerrar() {
        Stage stage = (Stage) txtId.getScene().getWindow();
        stage.close();
    }
}