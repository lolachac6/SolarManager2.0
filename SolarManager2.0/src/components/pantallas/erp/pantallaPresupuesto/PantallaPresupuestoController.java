package components.pantallas.erp.pantallaPresupuesto;

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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;
import utils.AlertasSolarManager;

/**
 * Controlador de la pantalla de presupuestos.
 *
 * <p>
 * Gestiona la carga de presupuestos en la tabla, el filtrado de resultados,
 * la apertura de la ventana modal de modificación y la navegación entre
 * pantallas del ERP.
 * </p>
 *
 * @author Iván
 */
public class PantallaPresupuestoController implements Initializable {

    @FXML private Button btnVolver;
    @FXML private Button btnBuscar;
    @FXML private Button btnHacerFactura;
    @FXML private Button btnGuardar;
    @FXML private Button btnModificar;

    @FXML private TextField txtFiltro;

    @FXML private TableView<Document> tablaPresupuestos;
    @FXML private TableColumn<Document, String> colId;
    @FXML private TableColumn<Document, String> colIdCliente;
    @FXML private TableColumn<Document, String> colIdComercial;
    @FXML private TableColumn<Document, String> colFechaCreacion;
    @FXML private TableColumn<Document, String> colEstado;
    @FXML private TableColumn<Document, String> colSubtotal;
    @FXML private TableColumn<Document, String> colIva;
    @FXML private TableColumn<Document, String> colTotal;

    private ObservableList<Document> listaPresupuestos;

    /**
     * Inicializa el controlador configurando la tabla y cargando los datos.
     *
     * @param url ubicación del recurso
     * @param rb recursos internacionales
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarPresupuestos();
    }

    /**
     * Configura las columnas de la tabla de presupuestos.
     */
    private void configurarColumnas() {
        colId.setCellValueFactory(data ->
                new SimpleStringProperty(valorComoTexto(data.getValue().get("_id"))));

        colIdCliente.setCellValueFactory(data -> {
            Document cliente = data.getValue().get("cliente", Document.class);

            if (cliente != null) {
                String nombre = valorComoTexto(cliente.getString("nombre"));
                String apellidos = valorComoTexto(cliente.getString("apellidos"));
                String nombreCompleto = (nombre + " " + apellidos).trim();

                if (!nombreCompleto.isEmpty()) {
                    return new SimpleStringProperty(nombreCompleto);
                }
            }

            return new SimpleStringProperty(valorComoTexto(data.getValue().get("idCliente")));
        });

        colIdComercial.setCellValueFactory(data ->
                new SimpleStringProperty(valorComoTexto(data.getValue().get("idComercial"))));

        colFechaCreacion.setCellValueFactory(data ->
                new SimpleStringProperty(valorComoTexto(data.getValue().get("fechaCreacion"))));

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(valorComoTexto(data.getValue().get("estado"))));

        colSubtotal.setCellValueFactory(data ->
                new SimpleStringProperty(valorComoTexto(data.getValue().get("subtotal"))));

        colIva.setCellValueFactory(data ->
                new SimpleStringProperty(valorComoTexto(data.getValue().get("iva"))));

        colTotal.setCellValueFactory(data ->
                new SimpleStringProperty(valorComoTexto(data.getValue().get("total"))));
    }

    /**
     * Carga los presupuestos desde MongoDB y los muestra en la tabla.
     */
    private void cargarPresupuestos() {
        listaPresupuestos = FXCollections.observableArrayList();

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Presupuestos");

            for (Document doc : coleccion.find()) {
                listaPresupuestos.add(doc);
            }

            tablaPresupuestos.setItems(listaPresupuestos);

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudieron cargar los presupuestos.");
        }
    }

    /**
     * Recarga los datos de la tabla de presupuestos.
     */
    private void recargarTabla() {
        cargarPresupuestos();
    }

    /**
     * Filtra los presupuestos según el texto introducido.
     */
    @FXML
    private void buscarPresupuestos() {
        String filtro = txtFiltro.getText() != null ? txtFiltro.getText().toLowerCase().trim() : "";

        if (filtro.isEmpty()) {
            tablaPresupuestos.setItems(listaPresupuestos);
            return;
        }

        ObservableList<Document> filtrados = FXCollections.observableArrayList();

        for (Document doc : listaPresupuestos) {
            String id = valorComoTexto(doc.get("_id")).toLowerCase();
            String idCliente = valorComoTexto(doc.get("idCliente")).toLowerCase();
            String idComercial = valorComoTexto(doc.get("idComercial")).toLowerCase();
            String fecha = valorComoTexto(doc.get("fechaCreacion")).toLowerCase();
            String estado = valorComoTexto(doc.get("estado")).toLowerCase();

            Document cliente = doc.get("cliente", Document.class);
            String nombreCliente = "";
            if (cliente != null) {
                nombreCliente = (valorComoTexto(cliente.getString("nombre")) + " "
                        + valorComoTexto(cliente.getString("apellidos"))).trim().toLowerCase();
            }

            if (id.contains(filtro)
                    || idCliente.contains(filtro)
                    || idComercial.contains(filtro)
                    || fecha.contains(filtro)
                    || estado.contains(filtro)
                    || nombreCliente.contains(filtro)) {
                filtrados.add(doc);
            }
        }

        tablaPresupuestos.setItems(filtrados);
    }

    /**
     * Abre la ventana modal de modificación del presupuesto seleccionado.
     *
     * @param event evento del botón
     */
    @FXML
    private void modificarPresupuesto(ActionEvent event) {
        Document presupuestoSeleccionado = tablaPresupuestos.getSelectionModel().getSelectedItem();

        if (presupuestoSeleccionado == null) {
            AlertasSolarManager.warning(
                    "Presupuesto no seleccionado",
                    "Debe seleccionar un presupuesto de la tabla para modificar."
            );
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/components/pantallas/erp/pantallaModificacionPresupuesto/pantallaModificacionPresupuesto.fxml")
            );

            Parent root = loader.load();

            components.pantallas.erp.pantallaModificacionPresupuesto.PantallaModificacionPresupuestoController controller =
                    loader.getController();

            controller.cargarPresupuesto(presupuestoSeleccionado);

            Stage stageModal = new Stage();
            stageModal.initModality(Modality.WINDOW_MODAL);
            stageModal.initOwner(((Node) event.getSource()).getScene().getWindow());
            stageModal.setTitle("Modificar Presupuesto");
            stageModal.setResizable(false);
            stageModal.setScene(new Scene(root));
            stageModal.showAndWait();

            recargarTabla();

        } catch (IOException e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo abrir la ventana de modificación del presupuesto.");
        }
    }

    /**
     * Acción del botón guardar de la pantalla principal.
     *
     * @param event evento del botón
     */
    @FXML
    private void guardarPresupuesto(ActionEvent event) {
        AlertasSolarManager.pantallaEnDesarrollo();
    }

    /**
     * Acción del botón hacer factura.
     *
     * @param event evento del botón
     */
    @FXML
    private void hacerFactura(ActionEvent event) {
        AlertasSolarManager.pantallaEnDesarrollo();
    }

    /**
     * Cambia la pantalla actual por otra indicada mediante la ruta FXML.
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

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    stage.setMaximized(true);
                }
            });

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
     * Recarga la pantalla de presupuestos.
     *
     * @param e evento de acción
     */
    @FXML
    private void irPresupuestos(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }

    /**
     * Navega a la pantalla de instalaciones.
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
    * Elimina el presupuesto seleccionado de la colección Presupuestos de MongoDB.
    *
    * @param event evento del botón
    */
    @FXML
    private void eliminarPresupuesto(ActionEvent event) {
        Document presupuestoSeleccionado = tablaPresupuestos.getSelectionModel().getSelectedItem();

        if (presupuestoSeleccionado == null) {
            AlertasSolarManager.warning(
                    "Presupuesto no seleccionado",
                    "Debe seleccionar un presupuesto de la tabla para eliminar."
            );
            return;
        }

        boolean confirmar = AlertasSolarManager.confirmar(
                "Eliminar presupuesto",
                "¿Seguro que deseas eliminar el presupuesto seleccionado?",
                "Eliminar",
                "Cancelar"
        );

        if (!confirmar) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Presupuestos");

            coleccion.deleteOne(new Document("_id", presupuestoSeleccionado.getObjectId("_id")));

            recargarTabla();
            AlertasSolarManager.operacionCorrecta();

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo eliminar el presupuesto.");
        }
    }

    /**
     * Convierte un valor cualquiera a texto controlando valores nulos.
     *
     * @param valor valor a convertir
     * @return representación textual del valor
     */
    private String valorComoTexto(Object valor) {
        return valor == null ? "" : valor.toString();
    }
}