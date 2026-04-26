
package components.pantallas.pantallaDetalleProveedor;

import javafx.scene.control.TextArea;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Direccion;
import modelo.Proveedor;
import utils.AlertasSolarManager;
import utils.CifradoDatos;

/**
 * Controlador del modal de detalle de proveedor.
 */

public class PantallaDetalleProveedorController {

    @FXML private TextField txtNombreEmpresa;
    @FXML private TextField txtRazonSocial;
    @FXML private TextField txtCif;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtCalle;
    @FXML private TextField txtNumero;
    @FXML private TextField txtCodigoPostal;
    @FXML private TextField txtMunicipio;
    @FXML private TextField txtProvincia;
    @FXML private TextArea txtObservaciones;

    @FXML
    public void initialize() {
        // No necesitas nada aquí por ahora
    }

    public void cargarProveedor(Proveedor proveedor) {

         if (proveedor == null) {
        AlertasSolarManager.error(
            "Error al cargar proveedor",
            "No se han podido cargar los datos del proveedor."
        );
        return;
    }

    txtNombreEmpresa.setText(proveedor.getNombreEmpresa());
    txtRazonSocial.setText(proveedor.getRazonSocial());
    txtCif.setText(CifradoDatos.descifrarSiEsPosible(proveedor.getCif()));
    txtTelefono.setText(CifradoDatos.descifrarSiEsPosible(proveedor.getTelefono()));
    txtEmail.setText(proveedor.getEmail());
    txtObservaciones.setText(proveedor.getObservaciones());

        Direccion d = proveedor.getDireccion();

        if (d != null) {
            txtCalle.setText(d.getCalle() != null ? d.getCalle() : "");
            txtNumero.setText(d.getNumero() != null ? d.getNumero() : "");
            txtCodigoPostal.setText(d.getCodigoPostal() != null ? d.getCodigoPostal() : "");
            txtMunicipio.setText(d.getMunicipio() != null ? d.getMunicipio() : "");
            txtProvincia.setText(d.getProvincia() != null ? d.getProvincia() : "");
        }
    }

    @FXML
    private void cerrar() {
        boolean confirmar = AlertasSolarManager.confirmar(
            "Cerrar detalle",
            "¿Deseas cerrar la ventana de detalle?"
        );

        if (!confirmar) {
            return;
        }

        Stage stage = (Stage) txtNombreEmpresa.getScene().getWindow();
        stage.close();
    }
}