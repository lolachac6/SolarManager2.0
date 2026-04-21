package components.pantallas.erp.pantallaAltaProducto;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Producto;
import org.bson.Document;
import utils.AlertasSolarManager;

/**
 * Controlador de la pantalla de alta y edición de productos del ERP SolarManager.
 *
 * <p>Gestiona la validación del formulario, el guardado o actualización del producto
 * en MongoDB, la carga de datos cuando se edita un producto existente y la navegación
 * de vuelta a la pantalla de stock.</p>
 *
 * <p>Permite tanto crear nuevos productos como modificar productos ya existentes.</p>
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
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/components/pantallas/erp/pantallaStock/PantallaStock.fxml")
            );
            javafx.scene.Parent root = loader.load();

            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setResizable(true);
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            
        }

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
    private void cancelar(ActionEvent e) {
        if (!AlertasSolarManager.confirmar(
                "Salir sin guardar",
                "¿Desea salir sin guardar los cambios?"
        )) {
            return;
        }

        volver(e);
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
     private void volver(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
                "/components/pantallas/erp/pantallaStock/pantallaStock.fxml");
    }
    
    /**
    * Cambia la pantalla actual por otra indicada mediante su ruta FXML.
    *
    * @param nodo nodo que dispara el evento (para obtener el Stage actual)
    * @param rutaFXML ruta del archivo FXML a cargar
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