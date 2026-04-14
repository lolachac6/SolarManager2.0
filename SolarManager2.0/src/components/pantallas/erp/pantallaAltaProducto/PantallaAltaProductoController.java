package components.pantallas.erp.pantallaAltaProducto;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Producto;
import org.bson.Document;
import utils.AlertasSolarManager;

/**
 * Controlador de la pantalla de alta y edición de productos.
 *
 * <p>Gestiona la validación del formulario, el guardado del producto,
 * la carga de datos para edición y el cierre de la ventana modal.</p>
 *
 * @author Iván
 */
public class PantallaAltaProductoController {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<Producto.TipoProducto> cmbTipo;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private ComboBox<String> cmbProveedor;
    @FXML private TextArea txtDescripcion;

    private String idProducto = null;

    /**
     * Inicializa la pantalla cargando los tipos de producto y los proveedores.
     */
    @FXML
    public void initialize() {
        cmbTipo.getItems().addAll(Producto.TipoProducto.values());
        cmbProveedor.getItems().addAll("Proveedor 1", "Proveedor 2", "Proveedor 3");
    }

    /**
     * Limpia el formulario para preparar el alta de un nuevo producto.
     */
    @FXML
    private void nuevoProducto() {
        limpiarFormulario();
    }

    /**
     * Guarda o actualiza un producto en la base de datos.
     */
    @FXML
    private void guardarProducto() {

        if (!validarCampos()) return;

        if (!AlertasSolarManager.confirmar(
                "Guardar producto",
                "¿Desea guardar los cambios del producto?"
        )) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Productos");

            Document doc = new Document()
                    .append("nombre", txtNombre.getText().trim())
                    .append("tipo", cmbTipo.getValue().name())
                    .append("precio", Double.parseDouble(txtPrecio.getText()))
                    .append("stock", Integer.parseInt(txtStock.getText()))
                    .append("proveedor", cmbProveedor.getValue())
                    .append("descripcion", txtDescripcion.getText().trim());

            if (idProducto == null) {
                coleccion.insertOne(doc);
            } else {
                coleccion.updateOne(
                    new Document("_id", new org.bson.types.ObjectId(idProducto)),
                    new Document("$set", doc)
                );
            }

            AlertasSolarManager.productoGuardadoCorrectamente();
            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Valida los campos del formulario.
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
    private boolean validarCampos() {

        if (txtNombre.getText().trim().isEmpty()) {
            AlertasSolarManager.nombreProductoObligatorio();
            return false;
        }

        if (cmbTipo.getValue() == null) {
            AlertasSolarManager.tipoProductoObligatorio();
            return false;
        }

        try {
            double precio = Double.parseDouble(txtPrecio.getText());
            if (precio < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertasSolarManager.precioNoValido();
            return false;
        }

        try {
            int stock = Integer.parseInt(txtStock.getText());
            if (stock < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertasSolarManager.stockNoValido();
            return false;
        }

        if (cmbProveedor.getValue() == null) {
            AlertasSolarManager.proveedorObligatorio();
            return false;
        }

        if (txtDescripcion.getText().trim().isEmpty()) {
            AlertasSolarManager.descripcionObligatoria();
            return false;
        }

        return true;
    }

    /**
     * Carga los datos de un producto existente para su edición.
     *
     * @param p producto a editar
     */
    public void cargarProducto(Producto p) {
        txtNombre.setText(p.getNombre());
        cmbTipo.setValue(p.getTipoProducto());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
        cmbProveedor.setValue(p.getIdProveedor());
        txtDescripcion.setText(p.getDescripcion());
        this.idProducto = p.getId();
    }

    /**
     * Cancela la operación actual previa confirmación.
     *
     * @param e evento de acción
     */
    @FXML
    private void cancelar(javafx.event.ActionEvent e) {
        if (!AlertasSolarManager.confirmarCancelarProducto()) {
            return;
        }
        cerrarVentana();
    }

    /**
     * Cierra la ventana actual.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    /**
     * Cierra la ventana actual.
     *
     * @param e evento de acSción
     */
    @FXML
    private void volver(javafx.event.ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtDescripcion.clear();
        cmbTipo.getSelectionModel().clearSelection();
        cmbProveedor.getSelectionModel().clearSelection();
    }
}