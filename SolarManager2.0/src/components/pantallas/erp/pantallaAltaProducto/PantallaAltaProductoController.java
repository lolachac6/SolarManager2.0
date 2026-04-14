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

/**
 * Controlador de la pantalla de alta y edición de productos dentro del ERP.
 *
 * <p>Esta clase gestiona:
 * <ul>
 *   <li>La creación de nuevos productos.</li>
 *   <li>La edición de productos existentes.</li>
 *   <li>La validación de los campos del formulario.</li>
 *   <li>La comunicación con MongoDB para insertar o actualizar documentos.</li>
 *   <li>La carga de datos en los controles JavaFX.</li>
 * </ul>
 *
 * <p>El controlador permite tanto registrar un producto nuevo como modificar uno existente,
 * dependiendo de si {@code idProducto} es nulo o contiene un ObjectId válido.
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
     * Inicializa la pantalla cargando:
     * <ul>
     *   <li>Los tipos de producto definidos en el enum {@link Producto.TipoProducto}.</li>
     *   <li>La lista de proveedores (actualmente simulada).</li>
     * </ul>
     *
     * Este método se ejecuta automáticamente al cargar el FXML.
     */
    
    @FXML
    public void initialize() {

        // Tipos reales desde el ENUM
        cmbTipo.getItems().addAll(Producto.TipoProducto.values());

        // TODO: cargar proveedores desde MongoDB
        cmbProveedor.getItems().addAll("Proveedor 1", "Proveedor 2", "Proveedor 3");
    }

    /**
     * Limpia todos los campos del formulario para permitir registrar un nuevo producto.
     * No modifica el valor de {@code idProducto}.
     */
    
    @FXML
    private void nuevoProducto() {
        limpiarFormulario();
    }

    /**
     * Guarda un producto en la base de datos.
     *
     * <p>El comportamiento depende del valor de {@code idProducto}:
     * <ul>
     *   <li><b>Nulo</b>: se inserta un nuevo documento en la colección "Productos".</li>
     *   <li><b>No nulo</b>: se actualiza el documento existente con ese ID.</li>
     * </ul>
     *
     * <p>Antes de guardar, se validan todos los campos mediante {@link #validarCampos()}.
     * Si la validación falla, el método se detiene.
     */
    
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

     /**
     * Valida todos los campos del formulario antes de guardar.
     *
     * <p>Comprueba:
     * <ul>
     *   <li>Nombre no vacío.</li>
     *   <li>Tipo de producto seleccionado.</li>
     *   <li>Precio numérico y positivo.</li>
     *   <li>Stock entero y positivo.</li>
     *   <li>Proveedor seleccionado.</li>
     *   <li>Descripción no vacía.</li>
     * </ul>
     *
     * @return {@code true} si todos los campos son válidos, {@code false} en caso contrario.
     */
    
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
    
    /**
     * Carga los datos de un producto existente en el formulario para su edición.
     *
     * <p>Este método:
     * <ul>
     *   <li>Rellena todos los campos del formulario con los valores del producto.</li>
     *   <li>Guarda el ID del producto para permitir su actualización posterior.</li>
     * </ul>
     *
     * @param p Producto a cargar en el formulario.
     */
    
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

    /**
     * Solicita confirmación al usuario para cancelar la operación actual.
     *
     * <p>Si el usuario confirma, se cierra la ventana sin guardar cambios.
     * Si no confirma, la operación se cancela y la ventana permanece abierta.
     *
     * @param e Evento de acción del botón.
     */
    
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
    
    /**
     * Cierra la ventana actual del formulario.
     */
    
    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Cierra la ventana actual y vuelve a la pantalla anterior.
     *
     * @param e Evento de acción del botón.
     */
    
    @FXML
    private void volver(javafx.event.ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Limpia todos los campos del formulario y reinicia las selecciones.
     */
    
    private void limpiarFormulario() {
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtDescripcion.clear();
        cmbTipo.getSelectionModel().clearSelection();
        cmbProveedor.getSelectionModel().clearSelection();
    }
    
    /**
     * Muestra una alerta modal con un mensaje y un tipo específico.
     *
     * @param mensaje Texto a mostrar en la alerta.
     * @param tipo    Tipo de alerta (información, advertencia, error, etc.).
     */
    
    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Solar Manager");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}