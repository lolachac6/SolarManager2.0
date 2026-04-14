package components.pantallas.erp.pantallaStock;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import components.pantallas.erp.pantallaAltaProducto.PantallaAltaProductoController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;

import javafx.stage.Stage;
import modelo.Producto;
import org.bson.Document;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

/**
 * Controlador de la pantalla de gestión de stock.
 * 
 * <p>Se encarga de:
 * <ul>
 *     <li>Mostrar los productos en una tabla</li>
 *     <li>Permitir filtrar los productos</li>
 *     <li>Abrir pantallas de alta/modificación</li>
 *     <li>Eliminar productos</li>
 *     <li>Navegar entre pantallas del ERP</li>
 * </ul>
 * 
 * <p>Los datos se obtienen desde MongoDB utilizando la colección "Productos".</p>
 * 
 * @author Iván
 */
public class PantallaStockController implements Initializable {

    @FXML private TableView<Producto> tablaStock;

    @FXML private TableColumn<Producto, String> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colTipo;
    @FXML private TableColumn<Producto, String> colPrecio;
    @FXML private TableColumn<Producto, String> colProveedor;
    @FXML private TableColumn<Producto, String> colStock;

    @FXML private TextField txtFiltro;

    /** Lista original de productos cargados desde la base de datos */
    private ObservableList<Producto> listaOriginal = FXCollections.observableArrayList();

    /**
     * Método de inicialización del controlador.
     * 
     * <p>Configura las columnas de la tabla, carga los productos desde la base de datos
     * y añade un listener al campo de filtro.</p>
     * 
     * @param url ubicación
     * @param rb recursos
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Configuración de columnas
        colId.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getId() != null ? data.getValue().getId() : ""
                )
        );

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoProducto"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));

        try {
            obtenerProductosTabla();
        } catch (IOException e) {
            e.printStackTrace();
        }

        txtFiltro.textProperty().addListener((obs, oldVal, newVal) -> buscarFiltro());
    }

    // =========================
    // CAMBIO DE PANTALLA
    // =========================

    /**
     * Cambia la pantalla actual por otra especificada mediante un archivo FXML.
     * 
     * @param nodo nodo origen del evento
     * @param rutaFXML ruta del archivo FXML
     */
    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // BOTONES
    // =========================

    /**
     * Abre la ventana modal para dar de alta un nuevo producto.
     * 
     * @param e evento de acción
     */
    @FXML
    private void abrirAltaProducto(javafx.event.ActionEvent e) {
        try {
            URL resource = getClass().getResource(
                "/components/pantallas/erp/pantallaAltaProducto/PantallaAltaProducto.fxml"
            );

            if (resource == null) {
                System.out.println("❌ No se encontró el FXML de alta de producto");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Alta Producto");
            stage.setResizable(false);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.centerOnScreen();
            stage.showAndWait();
            
            // Refrescar tabla al cerrar la ventana
            obtenerProductosTabla();

        } catch (IOException ex) {
            System.out.println("❌ Error abriendo alta de producto");
            ex.printStackTrace();
        }
    }

    @FXML private void volverInicio(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }

    @FXML private void irClientes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }

    @FXML private void irComerciales(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }

    @FXML private void irProveedores(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaProveedor/PantallaProveedor.fxml");
    }

    @FXML private void irStock(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }

    @FXML private void irPresupuestos(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }

    @FXML private void irInformes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }

    /**
     * Navega a la pantalla de alta de producto (no modal).
     * 
     * @param e evento de acción
     */
    @FXML
    private void anadirProducto(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaAltaProducto/PantallaAltaProducto.fxml");
    }

    // =========================
    // CARGAR PRODUCTOS
    // =========================

    /**
     * Obtiene los productos desde MongoDB y los carga en la tabla.
     * 
     * @throws IOException en caso de error de acceso
     */
    public void obtenerProductosTabla() throws IOException {

        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<Document> coleccion = db.getCollection("Productos");

        listaOriginal.clear();

        for (Document doc : coleccion.find()) {

            Producto p = new Producto();

            if (doc.getObjectId("_id") != null) {
                p.setId(doc.getObjectId("_id").toString());
            }

            p.setNombre(doc.getString("nombre"));

            String tipoStr = doc.getString("tipo");
            if (tipoStr != null) {
                try {
                    p.setTipoProducto(Producto.TipoProducto.valueOf(tipoStr));
                } catch (Exception e) {
                    p.setTipoProducto(Producto.TipoProducto.MATERIAL_ELECTRICO);
                }
            }

            Object precioObj = doc.get("precio");
            double precio = 0.0;

            if (precioObj instanceof Number) {
                precio = ((Number) precioObj).doubleValue();
            }

            p.setPrecio(precio);

            Object stockObj = doc.get("stock");
            int stock = 0;

            if (stockObj instanceof Number) {
                stock = ((Number) stockObj).intValue();
            }

            p.setStock(stock);

            p.setIdProveedor(doc.getString("proveedor"));
            p.setDescripcion(doc.getString("descripcion"));

            listaOriginal.add(p);
        }

        tablaStock.setItems(listaOriginal);
    }

    // =========================
    // FILTRO
    // =========================

    /**
     * Filtra los productos mostrados en la tabla según el texto introducido.
     */
    @FXML
    public void buscarFiltro() {

        String filtro = txtFiltro.getText().toLowerCase();

        if (filtro.isEmpty()) {
            tablaStock.setItems(listaOriginal);
            return;
        }

        ObservableList<Producto> filtrada = FXCollections.observableArrayList();

        for (Producto p : listaOriginal) {

            String tipo = p.getTipoProducto() != null
                    ? p.getTipoProducto().name().toLowerCase()
                    : "";

            if ((p.getNombre() != null && p.getNombre().toLowerCase().contains(filtro))
                    || tipo.contains(filtro)
                    || (p.getIdProveedor() != null && p.getIdProveedor().toLowerCase().contains(filtro))
                    || String.valueOf(p.getPrecio()).contains(filtro)
                    || String.valueOf(p.getStock()).contains(filtro)
                    || p.getId().toLowerCase().contains(filtro)) {

                filtrada.add(p);
            }
        }

        tablaStock.setItems(filtrada);
    }
    
    /**
    * Abre una ventana modal para modificar el producto seleccionado en la tabla.
    *
    * <p>Si no hay ningún producto seleccionado, muestra una alerta de advertencia.
    * En caso contrario:
    * <ul>
    *   <li>Carga el FXML de alta/modificación de producto</li>
    *   <li>Pasa el producto seleccionado al controlador correspondiente</li>
    *   <li>Abre la ventana en modo modal</li>
    *   <li>Refresca la tabla al cerrarse la ventana</li>
    * </ul>
    */
    
    @FXML
    private void modificarProducto() {

        Producto seleccionado = tablaStock.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto de la tabla para modificar", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/components/pantallas/erp/pantallaAltaProducto/PantallaAltaProducto.fxml"
            ));

            Parent root = loader.load();

            PantallaAltaProductoController controller = loader.getController();
            controller.cargarProducto(seleccionado);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modificar Producto");
            stage.setResizable(false);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.centerOnScreen();
            stage.showAndWait();

            obtenerProductosTabla();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    * Elimina el producto seleccionado de la base de datos tras confirmación del usuario.
    *
    * <p>El proceso es:
    * <ul>
    *   <li>Verificar que haya un producto seleccionado</li>
    *   <li>Solicitar confirmación mediante un cuadro de diálogo</li>
    *   <li>Eliminar el documento correspondiente en MongoDB</li>
    *   <li>Actualizar la tabla de productos</li>
    * </ul>
    *
    * <p>Si ocurre un error durante la eliminación, se muestra una alerta de error.</p>
    */
    
    @FXML
    private void eliminarProducto() {
        Producto seleccionado = tablaStock.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto de la tabla para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar producto");
        confirmacion.setContentText("¿Seguro que deseas eliminar el producto: " + seleccionado.getNombre() + "?");

        ButtonType aceptar = new ButtonType("Eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmacion.getButtonTypes().setAll(aceptar, cancelar);

        if (confirmacion.showAndWait().orElse(cancelar) != aceptar) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Productos");

            coleccion.deleteOne(new Document("_id", new org.bson.types.ObjectId(seleccionado.getId())));

            mostrarAlerta("Producto eliminado correctamente", Alert.AlertType.INFORMATION);

            obtenerProductosTabla();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al eliminar producto: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
    * Muestra una alerta simple al usuario con un mensaje y un tipo determinado.
    *
    * @param mensaje texto que se mostrará en la alerta
    * @param tipo tipo de alerta (información, advertencia, error, etc.)
    */
    
    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Solar Manager");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }    
}
    