package components.pantallas.pantallaDetalleStock;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Producto;

/**
 * Controlador de la ventana modal que muestra el detalle de un producto del stock.
 *
 * Esta pantalla se utiliza únicamente para visualizar la información del producto
 * seleccionado desde la pantalla principal de stock. El controlador recibe un
 * objeto {@link Producto} y carga sus datos en los campos de texto correspondientes.
 *
 * La ventana no permite edición y se cierra mediante el botón asociado al método
 * {@code cerrar()}.
 */
public class PantallaDetalleStockController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTipo;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private Button btnCerrar;

    /**
     * Carga en la interfaz los datos del producto recibido.
     *
     * Este método asigna a los campos de texto la información básica del producto:
     * nombre, tipo, precio y stock disponible. Se utiliza cuando el usuario abre
     * el modal desde la pantalla de gestión de stock.
     *
     * @param p el producto cuyos datos deben mostrarse en la vista
     */
    public void cargarDatos(Producto p) {
        txtNombre.setText(p.getNombre());
        txtTipo.setText(p.getTipoProducto().toString());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
    }
    
    /**
     * Cierra la ventana modal de detalle de stock.
     *
     * Obtiene la ventana actual a partir del botón de cierre y ejecuta
     * {@code close()} para cerrar el modal.
     */
    @FXML
    private void cerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}