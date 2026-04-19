package components.pantallas.erp.pantallaInstalaciones;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.net.URL;
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
 * Controlador de la pantalla de instalaciones.
 *
 * @author Iván
 */
public class PantallaInstalacionesController implements Initializable {

    @FXML private TableView<InstalacionFotovoltaica> tablaInstalaciones;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colId;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colIdCliente;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colPotencia;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colPaneles;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colProduccion;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colAhorro;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colDireccion;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colInversor;
    @FXML private TableColumn<InstalacionFotovoltaica, String> colBateria;
    @FXML private TextField txtFiltro;

    private ObservableList<InstalacionFotovoltaica> listaInstalaciones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarInstalaciones();
    }

    /**
     * Configura las columnas de la tabla de instalaciones.
     */
    private void configurarColumnas() {

        colId.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getId()));

        colIdCliente.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getIdCliente()));

        colPotencia.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getPotenciaInstalada())));

        colPaneles.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getNumeroPaneles())));

        colProduccion.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getProduccionEstimada())));

        colAhorro.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getAhorroEstimado())));

        colInversor.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getInversor()));

        colBateria.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBateria() ? "Sí" : "No"));

        colDireccion.setCellValueFactory(data -> {
            Direccion d = data.getValue().getDireccion();
            if (d != null) {
                return new SimpleStringProperty(
                        d.getCalle() + " " + d.getNumero() + ", " + d.getMunicipio()
                );
            }
            return new SimpleStringProperty("");
        });
    }

    /**
     * Carga las instalaciones desde MongoDB en la tabla.
     */
    private void cargarInstalaciones() {

        listaInstalaciones = FXCollections.observableArrayList();

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Instalaciones");

            for (Document doc : coleccion.find()) {

                InstalacionFotovoltaica i = new InstalacionFotovoltaica();

                i.setId(doc.getObjectId("_id").toHexString());
                i.setIdCliente(String.valueOf(doc.get("idCliente")));

                Number potencia = doc.get("potenciaInstalada", Number.class);
                Number paneles = doc.get("numeroPaneles", Number.class);
                Number produccion = doc.get("produccionEstimada", Number.class);
                Number ahorro = doc.get("ahorroEstimado", Number.class);

                i.setPotenciaInstalada(potencia != null ? potencia.doubleValue() : 0.0);
                i.setNumeroPaneles(paneles != null ? paneles.intValue() : 0);
                i.setProduccionEstimada(produccion != null ? produccion.doubleValue() : 0.0);
                i.setAhorroEstimado(ahorro != null ? ahorro.doubleValue() : 0.0);

                i.setInversor(doc.getString("inversor"));

                Boolean bateria = doc.getBoolean("bateria");
                i.setBateria(bateria != null ? bateria : false);

                Document dir = doc.get("direccion", Document.class);
                if (dir != null) {
                    i.setDireccion(new Direccion(
                            dir.getString("calle"),
                            dir.getString("numero"),
                            dir.getString("codigoPostal"),
                            dir.getString("municipio"),
                            dir.getString("provincia")
                    ));
                }

                listaInstalaciones.add(i);
            }

            tablaInstalaciones.setItems(listaInstalaciones);

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudieron cargar las instalaciones.");
        }
    }

    /**
     * Abre la pantalla modal de detalle de la instalación seleccionada.
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

            controller.cargarInstalacionPorId(seleccionada.getId());

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
     * Abre la pantalla de alta de presupuesto cargando los datos de la instalación seleccionada.
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
     * Elimina la instalación seleccionada de la tabla y de MongoDB.
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

            cargarInstalaciones();
            AlertasSolarManager.operacionCorrecta();

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo eliminar la instalación.");
        }
    }

    /**
     * Cambia la pantalla actual por otra indicada mediante su ruta FXML.
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

            if (String.valueOf(i.getPotenciaInstalada()).toLowerCase().contains(filtro)
                    || String.valueOf(i.getNumeroPaneles()).toLowerCase().contains(filtro)
                    || (i.getIdCliente() != null && i.getIdCliente().toLowerCase().contains(filtro))) {

                filtrados.add(i);
            }
        }

        tablaInstalaciones.setItems(filtrados);
    }
}