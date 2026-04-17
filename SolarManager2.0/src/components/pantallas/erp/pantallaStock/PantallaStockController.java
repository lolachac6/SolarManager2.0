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
    @FXML private TableColumn<Producto, String> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colTipo;
    @FXML private TableColumn<Producto, String> colPrecio;
    @FXML private TableColumn<Producto, String> colProveedor;
    @FXML private TableColumn<Producto, String> colStock;
    @FXML private TextField txtFiltro;

    private ObservableList<Producto> listaOriginal = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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

    @FXML
    private void volverInicio(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }

    @FXML
    private void irClientes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }

    @FXML
    private void irComerciales(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }

    @FXML
    private void irProveedores(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaProveedor/PantallaProveedor.fxml");
    }

    @FXML
    private void irStock(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }

    @FXML
    private void irPresupuestos(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }

    @FXML
    private void irInstalaciones(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml");
    }

    @FXML
    private void irInformes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }

    @FXML
    private void anadirProducto(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaAltaProducto/PantallaAltaProducto.fxml");
    }

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

    @FXML
    private void modificarProducto() {
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

            Stage stageActual = (Stage) tablaStock.getScene().getWindow();
            stageActual.close();

            Stage nuevoStage = new Stage();
            nuevoStage.setScene(new Scene(root));
            nuevoStage.setResizable(true);

            Platform.runLater(() -> {
                nuevoStage.setMaximized(true);
                nuevoStage.centerOnScreen();
            });

            nuevoStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        AlertasSolarManager.mostrar(tipo, null, mensaje);
    }
}