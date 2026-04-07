package components.pantallas.erp.pantallaAltaProducto;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import modelo.Producto;
import org.bson.Document;

public class PantallaAltaProductoController {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<Producto.TipoProducto> cmbTipo;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private ComboBox<String> cmbProveedor;
    @FXML private TextArea txtDescripcion;
    
    private String idProducto = null;

    @FXML
    public void initialize() {

        // Tipos reales desde el ENUM
        cmbTipo.getItems().addAll(Producto.TipoProducto.values());

        // TODO: cargar proveedores desde MongoDB
        cmbProveedor.getItems().addAll("Proveedor 1", "Proveedor 2", "Proveedor 3");
    }

    // =========================
    // NUEVO PRODUCTO
    // =========================
    @FXML
    private void nuevoProducto() {
        limpiarFormulario();
    }

    // =========================
    // GUARDAR PRODUCTO
    // =========================
    @FXML
    private void guardarProducto() {

        if (!validarCampos()) return;

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
                // ALTA
                coleccion.insertOne(doc);
            } else {
                // MODIFICACIÓN
                coleccion.updateOne(
                    new Document("_id", new org.bson.types.ObjectId(idProducto)),
                    new Document("$set", doc)
                );
            }

            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // VALIDACIONES
    // =========================
    private boolean validarCampos() {

        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("El nombre es obligatorio", Alert.AlertType.WARNING);
            return false;
        }

        if (cmbTipo.getValue() == null) {
            mostrarAlerta("Seleccione un tipo de producto", Alert.AlertType.WARNING);
            return false;
        }

        try {
            double precio = Double.parseDouble(txtPrecio.getText());
            if (precio < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("El precio debe ser un número válido y positivo", Alert.AlertType.WARNING);
            return false;
        }

        try {
            int stock = Integer.parseInt(txtStock.getText());
            if (stock < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("El stock debe ser un número entero positivo", Alert.AlertType.WARNING);
            return false;
        }

        if (cmbProveedor.getValue() == null) {
            mostrarAlerta("Seleccione un proveedor", Alert.AlertType.WARNING);
            return false;
        }

        if (txtDescripcion.getText().trim().isEmpty()) {
            mostrarAlerta("La descripción es obligatoria", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }
    
    // =========================
    // CARGAR PRODUCTO
    // =========================
    public void cargarProducto(Producto p) {
    txtNombre.setText(p.getNombre());
    cmbTipo.setValue(p.getTipoProducto());
    txtPrecio.setText(String.valueOf(p.getPrecio()));
    txtStock.setText(String.valueOf(p.getStock()));
    cmbProveedor.setValue(p.getIdProveedor());
    txtDescripcion.setText(p.getDescripcion());

    // Guardamos el ID para saber qué documento actualizar
    this.idProducto = p.getId();
}

    // =========================
    // CANCELAR
    // =========================
    @FXML
    private void cancelar(javafx.event.ActionEvent e) {

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cancelación");
        confirmacion.setHeaderText("¿Cancelar sin guardar?");
        confirmacion.setContentText("Los cambios no guardados se perderán.");

        ButtonType aceptar = new ButtonType("Salir sin guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Volver", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmacion.getButtonTypes().setAll(aceptar, cancelar);

        // Si el usuario NO confirma, no hacemos nada
        if (confirmacion.showAndWait().orElse(cancelar) != aceptar) {
            return;
        }

        // Si confirma, cerramos la ventana
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
    
    // =========================
    // VOLVER
    // =========================
    @FXML
    private void volver(javafx.event.ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }

    // =========================
    // UTILIDADES
    // =========================
    private void limpiarFormulario() {
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtDescripcion.clear();
        cmbTipo.getSelectionModel().clearSelection();
        cmbProveedor.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Solar Manager");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}