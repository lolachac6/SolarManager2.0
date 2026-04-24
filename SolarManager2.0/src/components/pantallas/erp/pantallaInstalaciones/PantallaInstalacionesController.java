package components.pantallas.erp.pantallaInstalaciones;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Direccion;
import modelo.InstalacionFotovoltaica;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.AlertasSolarManager;

/**
 * Controlador de la pantalla de instalaciones fotovoltaicas.
 *
 * Se encarga de cargar las instalaciones desde MongoDB, mostrar el nombre del
 * cliente en la tabla manteniendo internamente el id real del cliente, abrir la
 * ventana de detalle, abrir la ventana de alta de presupuesto y eliminar la
 * instalación seleccionada previa confirmación.
 *
 * @author Iván
 */
public class PantallaInstalacionesController implements Initializable {

    @FXML private TableView<InstalacionFotovoltaica> tablaInstalaciones;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colCliente;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colPotencia;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colPaneles;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colDireccion;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colInversor;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colBateria;
    @FXML private TextField txtFiltro;

    private ObservableList<InstalacionFotovoltaica> listaInstalaciones;
    private final Map<String, String> nombresClientePorInstalacion = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarInstalaciones();
        txtFiltro.textProperty().addListener((obs, oldValue, newValue) -> {
        buscar();
    });
        
        
    }

    /**
     * Configura las columnas de la tabla.
     */
    private void configurarColumnas() {

        colCliente.setCellValueFactory(data ->
                new SimpleStringProperty(obtenerNombreClienteVisible(data.getValue())));

        colPotencia.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getPotenciaInstalada())));

        colPaneles.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getNumeroPaneles())));

        colInversor.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getInversor() != null ? data.getValue().getInversor() : ""));

        colBateria.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBateria() ? "Sí" : "No"));

        colDireccion.setCellValueFactory(data -> {
            Direccion d = data.getValue().getDireccion();
            if (d != null) {
                String calle = d.getCalle() != null ? d.getCalle() : "";
                String numero = d.getNumero() != null ? d.getNumero() : "";
                String municipio = d.getMunicipio() != null ? d.getMunicipio() : "";
                return new SimpleStringProperty((calle + " " + numero + ", " + municipio).trim());
            }
            return new SimpleStringProperty("");
        });
    }

    /**
     * Devuelve el nombre visible del cliente asociado a la instalación.
     *
     * @param instalacion instalación de la fila
     * @return nombre del cliente para mostrar en la tabla
     */
    private String obtenerNombreClienteVisible(InstalacionFotovoltaica instalacion) {
        if (instalacion == null || instalacion.getId() == null) {
            return "";
        }
        String nombre = nombresClientePorInstalacion.get(instalacion.getId());
        return nombre != null ? nombre : "";
    }

    /**
     * Carga las instalaciones desde MongoDB.
     */
    private void cargarInstalaciones() {

        listaInstalaciones = FXCollections.observableArrayList();
        nombresClientePorInstalacion.clear();

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccionInstalaciones = db.getCollection("Instalaciones");
            MongoCollection<Document> coleccionClientes = db.getCollection("Clientes");

            for (Document doc : coleccionInstalaciones.find()) {

                InstalacionFotovoltaica instalacion = new InstalacionFotovoltaica();

                ObjectId objectId = doc.getObjectId("_id");
                if (objectId != null) {
                    instalacion.setId(objectId.toHexString());
                }

                Object idClienteObj = doc.get("idCliente");
                String idCliente = idClienteObj != null ? String.valueOf(idClienteObj) : "";
                instalacion.setIdCliente(idCliente);

                Document clienteDoc = buscarClientePorId(coleccionClientes, idCliente);

                if (clienteDoc != null) {
                    String nombre = clienteDoc.getString("nombre");
                    String apellidos = clienteDoc.getString("apellidos");
                    String nombreCompleto = ((nombre != null ? nombre : "") + " " + (apellidos != null ? apellidos : "")).trim();
                    nombresClientePorInstalacion.put(instalacion.getId(), nombreCompleto);
                } else {
                    nombresClientePorInstalacion.put(instalacion.getId(), "");
                }

                Number potencia = doc.get("potenciaInstalada", Number.class);
                Number paneles = doc.get("numeroPaneles", Number.class);
                Number produccion = doc.get("produccionEstimada", Number.class);
                Number ahorro = doc.get("ahorroEstimado", Number.class);

                instalacion.setPotenciaInstalada(potencia != null ? potencia.doubleValue() : 0.0);
                instalacion.setNumeroPaneles(paneles != null ? paneles.intValue() : 0);
                instalacion.setProduccionEstimada(produccion != null ? produccion.doubleValue() : 0.0);
                instalacion.setAhorroEstimado(ahorro != null ? ahorro.doubleValue() : 0.0);
                instalacion.setInversor(doc.getString("inversor"));

                Boolean bateria = doc.getBoolean("bateria");
                instalacion.setBateria(bateria != null ? bateria : false);

                Document dir = doc.get("direccion", Document.class);
                if (dir != null) {
                    instalacion.setDireccion(new Direccion(
                            dir.getString("calle"),
                            dir.getString("numero"),
                            dir.getString("codigoPostal"),
                            dir.getString("municipio"),
                            dir.getString("provincia")
                    ));
                }

                listaInstalaciones.add(instalacion);
            }

            tablaInstalaciones.setItems(listaInstalaciones);

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudieron cargar las instalaciones.");
        }
    }

    /**
     * Busca un cliente en la colección por su identificador.
     *
     * @param coleccionClientes colección de clientes
     * @param idCliente identificador del cliente
     * @return documento del cliente o null si no existe
     */
    private Document buscarClientePorId(MongoCollection<Document> coleccionClientes, String idCliente) {
        if (idCliente == null || idCliente.trim().isEmpty()) {
            return null;
        }

        try {
            return coleccionClientes.find(new Document("_id", new ObjectId(idCliente))).first();
        } catch (Exception e) {
            return coleccionClientes.find(new Document("_id", idCliente)).first();
        }
    }

    /**
     * Abre la ventana de detalle de la instalación seleccionada.
     *
     * @param event evento del botón
     */
    @FXML
    private void verDetalle(ActionEvent event) {

        InstalacionFotovoltaica seleccionada = tablaInstalaciones.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            AlertasSolarManager.warning("Aviso", "Seleccione una instalación");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/components/pantallas/pantallaDetalleInstalaciones/pantallaDetalleInstalaciones.fxml")
            );

            Parent root = loader.load();

            components.pantallas.pantallaDetalleInstalaciones.PantallaDetalleInstalacionesController controller =
                    loader.getController();

            controller.cargarInstalacion(seleccionada, obtenerNombreClienteVisible(seleccionada));

            Stage modal = new Stage();
            modal.initModality(Modality.WINDOW_MODAL);
            modal.initOwner(((Node) event.getSource()).getScene().getWindow());
            modal.setScene(new Scene(root));
            modal.setTitle("Detalle Instalación");
            modal.setResizable(false);
            modal.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo abrir el detalle de la instalación.");
        }
    }

    /**
     * Abre la pantalla de alta de presupuesto con la instalación seleccionada.
     *
     * @param event evento del botón
     */
    @FXML
    private void hacerPresupuesto(ActionEvent event) {

        InstalacionFotovoltaica seleccionada = tablaInstalaciones.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            AlertasSolarManager.warning("Aviso", "Seleccione una instalación");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/components/pantallas/erp/pantallaAltaPresupuesto/pantallaAltaPresupuesto.fxml")
            );

            Parent root = loader.load();

            components.pantallas.erp.pantallaAltaPresupuesto.PantallaAltaPresupuestoController controller =
                    loader.getController();

            controller.cargarDatosInstalacion(seleccionada);

            Stage modal = new Stage();
            modal.initModality(Modality.WINDOW_MODAL);
            modal.initOwner(((Node) event.getSource()).getScene().getWindow());
            modal.setTitle("Alta de Presupuesto");
            modal.setResizable(false);
            modal.setScene(new Scene(root));
            modal.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo abrir la pantalla de alta de presupuesto.");
        }
    }

    /**
     * Elimina la instalación seleccionada de la base de datos tras confirmación.
     *
     * @param event evento del botón
     */
    @FXML
    private void eliminar(ActionEvent event) {
        InstalacionFotovoltaica seleccionada = tablaInstalaciones.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            AlertasSolarManager.warning("Aviso", "Seleccione una instalación");
            return;
        }

        boolean confirmar = AlertasSolarManager.confirmar(
                "Eliminar instalación",
                "¿Seguro que deseas eliminar la instalación seleccionada?",
                "Eliminar",
                "Cancelar"
        );

        if (!confirmar) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Instalaciones");

            coleccion.deleteOne(new Document("_id", new ObjectId(seleccionada.getId())));
            nombresClientePorInstalacion.remove(seleccionada.getId());

            cargarInstalaciones();
            AlertasSolarManager.operacionCorrecta();

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo eliminar la instalación.");
        }
    }

    /**
     * Cambia la pantalla actual.
     *
     * @param nodo nodo origen
     * @param rutaFXML ruta del archivo FXML
     */
    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));

            Platform.runLater(() -> stage.setMaximized(true));

        } catch (IOException e) {
            e.printStackTrace();
            AlertasSolarManager.errorCambioPantalla();
        }
    }

    /**
     * Navega a la plantilla general del ERP.
     *
     * @param e evento de acción
     */
    @FXML
    private void volverInicio(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }

    /**
     * Navega a la pantalla de clientes.
     *
     * @param e evento de acción
     */
    @FXML
    private void irClientes(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }

    /**
     * Navega a la pantalla de comerciales.
     *
     * @param e evento de acción
     */
    @FXML
    private void irComerciales(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }

    /**
     * Navega a la pantalla de proveedores.
     *
     * @param e evento de acción
     */
    @FXML
    private void irProveedores(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaProveedor/PantallaProveedor.fxml");
    }

    /**
     * Navega a la pantalla de stock.
     *
     * @param e evento de acción
     */
    @FXML
    private void irStock(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }

    /**
     * Navega a la pantalla de presupuestos.
     *
     * @param e evento de acción
     */
    @FXML
    private void irPresupuestos(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }

    /**
     * Recarga la pantalla de instalaciones.
     *
     * @param e evento de acción
     */
    @FXML
    private void irInstalaciones(ActionEvent e) {
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
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }

    /**
 * Filtra las instalaciones según el texto introducido.
 */
@FXML
private void buscar() {

    String filtro = txtFiltro.getText() != null
            ? txtFiltro.getText().toLowerCase().trim()
            : "";

    if (filtro.isEmpty()) {
        tablaInstalaciones.setItems(listaInstalaciones);
        return;
    }

    ObservableList<InstalacionFotovoltaica> filtrados = FXCollections.observableArrayList();

    for (InstalacionFotovoltaica i : listaInstalaciones) {

        String nombreCliente = obtenerNombreClienteVisible(i).toLowerCase();
        String potencia = String.valueOf(i.getPotenciaInstalada()).toLowerCase();
        String paneles = String.valueOf(i.getNumeroPaneles()).toLowerCase();
        String produccion = String.valueOf(i.getProduccionEstimada()).toLowerCase();
        String ahorro = String.valueOf(i.getAhorroEstimado()).toLowerCase();
        String inversor = i.getInversor() != null ? i.getInversor().toLowerCase() : "";
        String bateria = i.getBateria() ? "sí" : "no";
        String direccion = obtenerDireccionVisible(i).toLowerCase();

        if (nombreCliente.contains(filtro)
                || potencia.contains(filtro)
                || paneles.contains(filtro)
                || produccion.contains(filtro)
                || ahorro.contains(filtro)
                || inversor.contains(filtro)
                || bateria.contains(filtro)
                || direccion.contains(filtro)) {

            filtrados.add(i);
        }
    }

    tablaInstalaciones.setItems(filtrados);
}

/**
 * Devuelve la dirección visible de la instalación tal como se muestra en tabla.
 *
 * @param instalacion instalación
 * @return dirección formateada
 */
private String obtenerDireccionVisible(InstalacionFotovoltaica instalacion) {
    if (instalacion == null || instalacion.getDireccion() == null) {
        return "";
    }

    Direccion d = instalacion.getDireccion();

    String calle = d.getCalle() != null ? d.getCalle() : "";
    String numero = d.getNumero() != null ? d.getNumero() : "";
    String municipio = d.getMunicipio() != null ? d.getMunicipio() : "";
    String provincia = d.getProvincia() != null ? d.getProvincia() : "";
    String codigoPostal = d.getCodigoPostal() != null ? d.getCodigoPostal() : "";

    return (calle + " " + numero + ", " + municipio + ", " + provincia + " " + codigoPostal).trim();
}
}