package components.pantallas.pantallaDetalleComerciales;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Comercial;
import modelo.Direccion;

/**
 * Controlador de la pantalla modal de detalle de comerciales.
 *
 * @author Iván
 */
public class PantallaDetalleComercialesController implements Initializable {

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDni;
    @FXML private TextField txtNumeroCuenta;
    @FXML private TextField txtCentroTrabajo;
    @FXML private TextField txtTipoContrato;
    @FXML private TextField txtObservaciones;
    @FXML private TextField txtActivo;
    @FXML private TextField txtCalle;
    @FXML private TextField txtNumero;
    @FXML private TextField txtCodigoPostal;
    @FXML private TextField txtMunicipio;
    @FXML private TextField txtProvincia;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void cargarComercial(Comercial comercial) {
        txtId.setText(comercial.getId());
        txtNombre.setText(comercial.getNombre());
        txtApellidos.setText(comercial.getApellidos());
        txtTelefono.setText(comercial.getTelefono());
        txtEmail.setText(comercial.getEmail());
        txtDni.setText(comercial.getDni());
        txtNumeroCuenta.setText(comercial.getNumeroCuenta());
        txtCentroTrabajo.setText(comercial.getCentroTrabajo());
        txtTipoContrato.setText(comercial.getTipoContrato() != null ? comercial.getTipoContrato().toString() : "");
        txtObservaciones.setText(comercial.getObservaciones());
        txtActivo.setText(comercial.getActivo() ? "Activo" : "No");

        Direccion d = comercial.getDireccion();
        if (d != null) {
            txtCalle.setText(d.getCalle());
            txtNumero.setText(d.getNumero());
            txtCodigoPostal.setText(d.getCodigoPostal());
            txtMunicipio.setText(d.getMunicipio());
            txtProvincia.setText(d.getProvincia());
        }
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) txtId.getScene().getWindow();
        stage.close();
    }
}