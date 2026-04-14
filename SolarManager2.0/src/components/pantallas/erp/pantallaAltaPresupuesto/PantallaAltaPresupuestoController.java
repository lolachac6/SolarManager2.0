package components.pantallas.erp.pantallaAltaPresupuesto;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import utils.AlertasSolarManager;

/**
 * Controlador de la pantalla de presupuestos.
 *
 * <p>Gestiona la carga de presupuestos en tabla, el filtrado de resultados
 * y la navegación entre pantallas del ERP.</p>
 *
 * @author Iván
 */
public class PantallaAltaPresupuestoController implements Initializable {

    @FXML private Button btnVolver;
    @FXML private Button btnBuscar;
    @FXML private Button btnnuevoPresupeusto;
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
     * Inicializa el controlador configurando la tabla, cargando los
     * presupuestos y asociando los eventos de los botones.
     *
     * @param url ubicación del recurso
     * @param rb recursos internacionales
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarPresupuestos();

        btnBuscar.setOnAction(e -> buscarPresupuestos());
        btnnuevoPresupeusto.setOnAction(this::anadirPresupuesto);
        btnModificar.setOnAction(this::modificarPresupuesto);
        btnGuardar.setOnAction(this::guardarPresupuesto);
        btnHacerFactura.setOnAction(this::hacerFactura);
    }

    /**
     * Configura las columnas de la tabla de presupuestos.
     */
    private void configurarColumnas() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(valorComoTexto(data.getValue().get("_id"))));
        colIdCliente.setCellValueFactory(data -> new SimpleStringProperty(valorComoTexto(data.getValue().get("idCliente"))));
        colIdComercial.setCellValueFactory(data -> new SimpleStringProperty(valorComoTexto(data.getValue().get("idComercial"))));
        colFechaCreacion.setCellValueFactory(data -> new SimpleStringProperty(valorComoTexto(data.getValue().get("fechaCreacion"))));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(valorComoTexto(data.getValue().get("estado"))));
        colSubtotal.setCellValueFactory(data -> new SimpleStringProperty(valorComoTexto(data.getValue().get("subtotal"))));
        colIva.setCellValueFactory(data -> new SimpleStringProperty(valorComoTexto(data.getValue().get("iva"))));
        colTotal.setCellValueFactory(data -> new SimpleStringProperty(valorComoTexto(data.getValue().get("total"))));
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
        }
    }

    /**
     * Filtra la tabla de presupuestos según el texto introducido.
     */
    @FXML
    private void buscarPresupuestos() {
        String filtro = txtFiltro.getText().toLowerCase();

        if (filtro.isEmpty()) {
            tablaPresupuestos.setItems(listaPresupuestos);
            return;
        }

        ObservableList<Document> filtrados = FXCollections.observableArrayList();

        for (Document doc : listaPresupuestos) {
            String id = valorComoTexto(doc.get("_id")).toLowerCase();
            String idCliente = valorComoTexto(doc.get("idCliente")).toLowerCase();
            String idComercial = valorComoTexto(doc.get("idComercial")).toLowerCase();
            String estado = valorComoTexto(doc.get("estado")).toLowerCase();

            if (id.contains(filtro)
                    || idCliente.contains(filtro)
                    || idComercial.contains(filtro)
                    || estado.contains(filtro)) {
                filtrados.add(doc);
            }
        }

        tablaPresupuestos.setItems(filtrados);
    }

    /**
     * Acción del botón de añadir presupuesto.
     *
     * @param event evento de acción
     */
    @FXML
    private void anadirPresupuesto(ActionEvent event) {
        AlertasSolarManager.pantallaEnDesarrollo();
    }

    /**
     * Acción del botón de modificar presupuesto.
     *
     * @param event evento de acción
     */
    @FXML
    private void modificarPresupuesto(ActionEvent event) {
        AlertasSolarManager.pantallaEnDesarrollo();
    }

    /**
     * Acción del botón de guardar.
     *
     * @param event evento de acción
     */
    @FXML
    private void guardarPresupuesto(ActionEvent event) {
        AlertasSolarManager.pantallaEnDesarrollo();
    }

    /**
     * Acción del botón de hacer factura.
     *
     * @param event evento de acción
     */
    @FXML
    private void hacerFactura(ActionEvent event) {
        AlertasSolarManager.pantallaEnDesarrollo();
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
     * Navega a la pantalla de informes.
     *
     * @param e evento de acción
     */
    @FXML
    private void irInformes(ActionEvent e) {
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
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
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