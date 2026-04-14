package components.pantallas.erp.pantallaComerciales;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import components.pantallas.erp.pantallaAltaComercial.PantallaAltaComercialController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import modelo.Comercial;
import modelo.Direccion;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.AlertasSolarManager;

/**
 * Controlador de la pantalla de gestión de comerciales.
 *
 * <p>Gestiona la carga de comerciales en tabla, la búsqueda,
 * la navegación entre pantallas y las operaciones de edición,
 * desactivación y reactivación.</p>
 *
 * @author Iván
 */
public class pantallaComercialesController implements Initializable {

    @FXML private TableView<Comercial> tablaComerciales;
    @FXML private TableColumn<Comercial, String> colId, colNombre, colActivo, colApellidos,
            colTelefono, colEmail, colDireccion, colTipoContrato, colDni,
            colNumeroCuenta, colCentroTrabajo, colObservaciones;
    @FXML private TextField txtFiltro;

    private ObservableList<Comercial> listaOriginal = FXCollections.observableArrayList();

    /**
     * Inicializa el controlador configurando columnas, carga de datos
     * y filtro de búsqueda.
     *
     * @param url URL de inicialización
     * @param rb recursos asociados
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId() != null ? data.getValue().getId() : ""));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNumeroCuenta.setCellValueFactory(new PropertyValueFactory<>("numeroCuenta"));
        colCentroTrabajo.setCellValueFactory(new PropertyValueFactory<>("centroTrabajo"));
        colObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        colDireccion.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDireccion() != null ? data.getValue().getDireccion().toString() : ""));

        colTipoContrato.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getTipoContrato() != null ? data.getValue().getTipoContrato().toString() : ""));

        colActivo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getActivo() ? "Activo" : "No"));

        try {
            obtenerComercialesTabla();
        } catch (IOException e) {
        }

        txtFiltro.textProperty().addListener((obs, oldVal, newVal) -> buscarFiltro());
    }

    /**
     * Obtiene los comerciales desde MongoDB y los carga en la tabla.
     *
     * @throws IOException si ocurre un error de acceso
     */
    public void obtenerComercialesTabla() throws IOException {
        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<Document> coleccion = db.getCollection("Comerciales");
        listaOriginal.clear();

        for (Document doc : coleccion.find()) {
            Document dirDoc = doc.get("direccion", Document.class);
            Direccion direccion = (dirDoc != null) ? new Direccion(
                    dirDoc.getString("calle"), dirDoc.getString("numero"),
                    dirDoc.getString("codigoPostal"), dirDoc.getString("municipio"),
                    dirDoc.getString("provincia")) : null;

            Comercial c = new Comercial();
            c.setId(doc.getObjectId("_id").toString());
            c.setSupabaseId(doc.getString("supabase_id"));
            c.setNombre(doc.getString("nombre"));
            c.setApellidos(doc.getString("apellidos"));
            c.setTelefono(doc.getString("telefono"));
            c.setEmail(doc.getString("email"));
            c.setDireccion(direccion);
            c.setDni(doc.getString("dni"));
            c.setNumeroCuenta(doc.getString("numeroCuenta"));
            c.setCentroTrabajo(doc.getString("centroTrabajo"));
            c.setObservaciones(doc.getString("observaciones"));
            c.setActivo(doc.getBoolean("activo", true));

            String tipo = doc.getString("tipoContrato");
            if (tipo != null) {
                try {
                    c.setTipoContrato(Comercial.TipoContrato.valueOf(tipo));
                } catch (IllegalArgumentException e) {
                    System.err.println("Tipo de contrato no válido: " + tipo);
                }
            }
            listaOriginal.add(c);
        }
        tablaComerciales.setItems(listaOriginal);
    }

    /**
     * Abre la pantalla de edición del comercial seleccionado.
     *
     * @param event evento de acción
     */
    @FXML
    public void modificarComercial(ActionEvent event) {
        Comercial seleccionado = tablaComerciales.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertasSolarManager.seleccionarComercial();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/pantallas/erp/pantallaAltaComercial/pantallaAltaComercial.fxml"));
            Parent root = loader.load();

            PantallaAltaComercialController controller = loader.getController();
            controller.cargarDatos(seleccionado);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            AlertasSolarManager.errorGenerico("Error");
        }
    }

    /**
     * Filtra los comerciales según el texto introducido.
     */
    @FXML
    public void buscarFiltro() {
        String texto = txtFiltro.getText().toLowerCase();
        if (texto == null || texto.isEmpty()) {
            tablaComerciales.setItems(listaOriginal);
            return;
        }

        ObservableList<Comercial> listaFiltrada = FXCollections.observableArrayList();
        for (Comercial c : listaOriginal) {
            if (c.getNombre().toLowerCase().contains(texto)
                    || c.getApellidos().toLowerCase().contains(texto)
                    || c.getEmail().toLowerCase().contains(texto)
                    || c.getDni().toLowerCase().contains(texto)) {
                listaFiltrada.add(c);
            }
        }
        tablaComerciales.setItems(listaFiltrada);
    }

    /**
     * Vuelve a la plantilla general.
     *
     * @param e evento de acción
     */
    @FXML
    private void volverInicio(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }

    /**
     * Abre la pantalla de alta de comercial.
     *
     * @param e evento de acción
     */
    @FXML
    private void anadirComercial(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaAltaComercial/pantallaAltaComercial.fxml");
    }

    /**
     * Navega a la pantalla de clientes.
     *
     * @param e evento de acción
     */
    @FXML
    private void irClientes(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }

    /**
     * Navega a la pantalla de comerciales.
     *
     * @param e evento de acción
     */
    @FXML
    private void irComerciales(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }

    /**
     * Navega a la pantalla de proveedores.
     *
     * @param e evento de acción
     */
    @FXML
    private void irProveedores(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaProveedor/PantallaProveedor.fxml");
    }

    /**
     * Navega a la pantalla de stock.
     *
     * @param e evento de acción
     */
    @FXML
    private void irStock(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }

    /**
     * Navega a la pantalla de presupuestos.
     *
     * @param e evento de acción
     */
    @FXML
    private void irPresupuestos(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }
    
    
    @FXML
    private void irInstalaciones(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
            "/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml");
    }
    

    /**
     * Navega a la pantalla de informes.
     *
     * @param e evento de acción
     */
    @FXML
    private void irInformes(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }

    /**
     * Cambia la pantalla actual por otra indicada.
     *
     * @param nodo nodo origen
     * @param ruta ruta del fichero FXML
     */
    private void cambiarPantalla(Node nodo, String ruta) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            AlertasSolarManager.errorGenerico("Error de navegación");
        }
    }

    /**
     * Desactiva el comercial seleccionado.
     *
     * @param event evento de acción
     */
    @FXML
    private void eliminarComercial(ActionEvent event) {
        Comercial seleccionado = tablaComerciales.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            AlertasSolarManager.seleccionarComercial();
            return;
        }

        if (!AlertasSolarManager.confirmarEliminarComercial()) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Comerciales");

            coleccion.updateOne(
                    new Document("_id", new ObjectId(seleccionado.getId())),
                    new Document("$set", new Document("activo", false))
            );

            AlertasSolarManager.operacionCorrecta();
            obtenerComercialesTabla();

        } catch (IOException e) {
            AlertasSolarManager.errorGenerico("Error");
        }
    }

    /**
     * Reactiva el comercial seleccionado.
     */
    @FXML
    private void reactivarComercial() {
        Comercial seleccionado = tablaComerciales.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            AlertasSolarManager.seleccionarComercial();
            return;
        }

        if (!AlertasSolarManager.confirmarReactivarComercial()) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Comerciales");

            coleccion.updateOne(
                    new Document("_id", new ObjectId(seleccionado.getId())),
                    new Document("$set", new Document("activo", true))
            );

            AlertasSolarManager.operacionCorrecta();
            obtenerComercialesTabla();

        } catch (IOException e) {
            AlertasSolarManager.errorGenerico("Error");
        }
    }
}