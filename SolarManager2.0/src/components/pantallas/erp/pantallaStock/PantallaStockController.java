package components.pantallas.erp.pantallaStock;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import components.pantallas.erp.pantallaAltaProducto.PantallaAltaProductoController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import utils.AlertasSolarManager;

/**
 * Controlador de la pantalla de gestión de stock.
 *
 * <p>Se encarga de mostrar los productos en una tabla, permitir el filtrado,
 * abrir pantallas de alta y modificación, eliminar productos y navegar
 * entre pantallas del ERP.</p>
 *
 * <p>Los datos se obtienen desde MongoDB utilizando la colección
 * "Productos".</p>
 *
 * @author Iván
 */
public class PantallaStockController implements Initializable {

    @FXML private TableView<Producto> tablaStock;   
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colTipo;
    @FXML private TableColumn<Producto, String> colPrecio;
    @FXML private TableColumn<Producto, String> colProveedor;
    @FXML private TableColumn<Producto, String> colStock;
    @FXML private TableColumn<Producto, String> colDescripcion;
    @FXML private TextField txtFiltro;

    private ObservableList<Producto> listaOriginal = FXCollections.observableArrayList();
    
    /**
    * Inicializa la pantalla configurando las columnas de la tabla,
    * cargando los productos desde MongoDB y activando el filtro dinámico.
    *
    * @param url ubicación del archivo FXML
    * @param rb recursos internacionales
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoProducto"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        try {
            obtenerProductosTabla();
        } catch (IOException e) {
            e.printStackTrace();
        }

        txtFiltro.textProperty().addListener((obs, oldVal, newVal) -> buscarFiltro());
    }
    
    /**
    * Cambia la pantalla actual por otra indicada mediante su ruta FXML.
    *
    * <p>Cierra la ventana actual y abre una nueva maximizada.</p>
    *
    * @param nodo nodo que dispara el evento (para obtener el Stage actual)
    * @param rutaFXML ruta del archivo FXML a cargar
    */
    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Stage stageActual = (Stage) nodo.getScene().getWindow();
            stageActual.close();

            Stage nuevoStage = new Stage();
            nuevoStage.setScene(new Scene(root));
            nuevoStage.setResizable(true);

            Platform.runLater(() -> {
                nuevoStage.setMaximized(true);
                nuevoStage.centerOnScreen();
            });

            nuevoStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
    * Abre la pantalla de alta de producto.
    *
    * <p>Si el archivo FXML no se encuentra, se muestra un mensaje en consola.</p>
    *
    * @param e evento de acción del botón
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

            Stage stageActual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageActual.close();

            Stage nuevoStage = new Stage();
            nuevoStage.setScene(new Scene(root));
            nuevoStage.setResizable(true);

            Platform.runLater(() -> {
                nuevoStage.setMaximized(true);
                nuevoStage.centerOnScreen();
            });

            nuevoStage.show();

        } catch (IOException ex) {
            System.out.println("❌ Error abriendo alta de producto");
            ex.printStackTrace();
        }
    }
    
    /**
    * Navega a la pantalla de inicio del ERP.
    *
    * @param e evento de acción
    */
    @FXML
    private void volverInicio(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }
    
    /**
    * Navega a la pantalla de clientes.
    *
    * @param e evento de acción
    */
    @FXML
    private void irClientes(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }
    
    /**
    * Navega a la pantalla de comerciales.
    *
    * @param e evento de acción
    */
    @FXML
    private void irComerciales(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }
    
    /**
    * Navega a la pantalla de proveedores.
    *
    * @param e evento de acción
    */
    @FXML
    private void irProveedores(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaProveedor/PantallaProveedor.fxml");
    }
    
    /**
    * Recarga la pantalla de stock.
    *
    * @param e evento de acción
    */
    @FXML
    private void irStock(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }
    
    /**
    * Navega a la pantalla de presupuestos.
    *
    * @param e evento de acción
    */
    @FXML
    private void irPresupuestos(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }
    
    /**
    * Navega a la pantalla de instalaciones.
    *
    * @param e evento de acción
    */
    @FXML
    private void irInstalaciones(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml");
    }
    
    /**
    * Navega a la pantalla de informes.
    *
    * @param e evento de acción
    */
    @FXML
    private void irInformes(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }
    
    /**
    * Abre la pantalla de alta de producto desde el botón "Añadir".
    *
    * @param e evento de acción
    */
    @FXML
    private void anadirProducto(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaAltaProducto/PantallaAltaProducto.fxml");
    }
    
    /**
     * Confirma si el usuario desea salir sin guardar y vuelve a la pantalla anterior.
     */
    @FXML
    private void confirmarSalida(ActionEvent event) {
        if (AlertasSolarManager.confirmar("Confirmación", "¿Desea salir sin guardar?")) {
            volver(event);
        }
    }
    
    /**
     * Cambia a la pantalla anterior cerrando la actual.
     *
     * @param event evento que contiene el nodo origen.
     */
    private void volver(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml")
            );

            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageActual.close();

            Stage nuevoStage = new Stage();
            nuevoStage.setScene(new Scene(root));
            nuevoStage.setResizable(true);

            Platform.runLater(() -> {
                nuevoStage.setMaximized(true);
                nuevoStage.centerOnScreen();
            });

            nuevoStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
    * Obtiene todos los productos desde MongoDB y los carga en la tabla.
    *
    * <p>Convierte los documentos BSON en objetos {@link Producto} y los almacena
    * en la lista observable utilizada por la tabla.</p>
    *
    * @throws IOException si ocurre un error al acceder a la base de datos
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
    
    /**
    * Filtra dinámicamente los productos mostrados en la tabla según el texto
    * introducido en el campo de búsqueda.
    *
    * <p>El filtro se aplica sobre nombre, tipo, proveedor, precio, stock e ID.</p>
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
    * Abre una ventana modal con el detalle del producto seleccionado en la tabla de stock.
    *
    * El método verifica primero si hay un producto seleccionado; en caso contrario,
    * muestra una alerta informando al usuario. Si existe selección, carga la vista
    * {@code PantallaDetalleStock.fxml}, obtiene su controlador y le pasa el objeto
    * {@link Producto} seleccionado mediante {@code cargarDatos()}.
    *
    * A continuación, crea una ventana modal bloqueante asociada a la ventana actual
    * y muestra el detalle del producto. Si ocurre un error durante la carga del FXML,
    * se captura la excepción y se muestra un mensaje de error genérico.
    *
    * @param event el evento de acción que dispara la apertura del detalle
    */
    @FXML
    private void detalle(ActionEvent event) {
        Producto seleccionado = tablaStock.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            AlertasSolarManager.warning("Aviso", "Debe seleccionar un producto");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/components/pantallas/pantallaDetalleStock/PantallaDetalleStock.fxml")
            );

            Parent root = loader.load();

            components.pantallas.pantallaDetalleStock.PantallaDetalleStockController controller =
                    loader.getController();
            controller.cargarDatos(seleccionado);

            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Stage modal = new Stage();
            modal.initOwner(stageActual);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.setScene(new Scene(root));
            modal.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo abrir el detalle del producto.");
        }
    }
    
    /**
    * Abre la pantalla de modificación de producto cargando previamente
    * los datos del producto seleccionado en la tabla.
    *
    * <p>Si no hay ningún producto seleccionado, se muestra una alerta.</p>
    */
    @FXML
    private void modificarProducto(ActionEvent event) {
        Producto seleccionado = tablaStock.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            AlertasSolarManager.seleccionarProductoModificar();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/components/pantallas/erp/pantallaAltaProducto/PantallaAltaProducto.fxml"
            ));

            Parent root = loader.load();

            PantallaAltaProductoController controller = loader.getController();
            controller.cargarProducto(seleccionado);

            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageActual.close();

            Stage nuevoStage = new Stage();
            nuevoStage.setScene(new Scene(root));
            nuevoStage.setResizable(true);

            Platform.runLater(() -> {
                nuevoStage.setMaximized(true);
                nuevoStage.centerOnScreen();
            });

            nuevoStage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
    * Elimina el producto seleccionado de la base de datos tras confirmación
    * del usuario y actualiza la tabla.
    *
    * <p>Si no hay selección o ocurre un error en MongoDB, se muestra una alerta.</p>
    */
    @FXML
    private void eliminarProducto() {
        Producto seleccionado = tablaStock.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            AlertasSolarManager.seleccionarProductoEliminar();
            return;
        }

        boolean confirmar = AlertasSolarManager.confirmar(
                "Eliminar producto",
                "¿Seguro que deseas eliminar el producto: " + seleccionado.getNombre() + "?",
                "Eliminar",
                "Cancelar"
        );

        if (!confirmar) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Productos");

            coleccion.deleteOne(new Document("_id", new org.bson.types.ObjectId(seleccionado.getId())));

            AlertasSolarManager.productoEliminadoCorrectamente();

            obtenerProductosTabla();

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorEliminarProducto(e.getMessage());
        }
    }
    
    /**
    * Muestra una alerta utilizando el sistema de alertas del ERP.
    *
    * @param mensaje texto a mostrar
    * @param tipo tipo de alerta (información, advertencia, error)
    */
    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        AlertasSolarManager.mostrar(tipo, null, mensaje);
    }
}