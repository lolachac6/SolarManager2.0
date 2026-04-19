package components.pantallas.pantallaDetalleStock;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Producto;

/**
 * Controlador del modal de detalle de stock.
 */
public class PantallaDetalleStockController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTipo;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private Button btnCerrar;

    /**
     * Carga los datos del producto seleccionado.
     */
    public void cargarDatos(Producto p) {
        txtNombre.setText(p.getNombre());
        txtTipo.setText(p.getTipoProducto().toString());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}