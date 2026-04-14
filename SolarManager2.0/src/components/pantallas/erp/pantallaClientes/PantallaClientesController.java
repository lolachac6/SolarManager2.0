package components.pantallas.erp.pantallaClientes;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Cliente;
import modelo.Direccion;
import org.bson.Document;
import utils.AlertasSolarManager;
import utils.CifradoDatos;

/**
 * Controlador de la pantalla de gestión de clientes.
 *
 * <p>Gestiona la carga de clientes en tabla, la búsqueda,
 * la navegación y las operaciones de alta, edición, cálculo de instalación
 * y eliminación.</p>
 *
 * @author Iván
 */
public class PantallaClientesController implements Initializable {

    @FXML private TableView<Cliente> tablaClientes;

    @FXML private TableColumn<Cliente, String> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colApellidos;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private TableColumn<Cliente, String> colTipoCliente;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colCif;
    @FXML private TableColumn<Cliente, String> colObservaciones;
    @FXML private TableColumn<Cliente, String> colIdComercialAsignado;

    @FXML private TextField txtFiltro;

    private ObservableList<Cliente> listaClientes;

    /**
     * Inicializa el controlador configurando columnas y cargando clientes.
     *
     * @param url URL de inicialización
     * @param rb recursos asociados
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarClientes();
    }

    /**
     * Configura las columnas de la tabla de clientes.
     */
    private void configurarColumnas() {

        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colApellidos.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApellidos()));
        colTelefono.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTelefono()));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        colDireccion.setCellValueFactory(data -> {
            Direccion d = data.getValue().getDireccion();
            if (d != null) {
                return new SimpleStringProperty(
                        d.getCalle() + " " + d.getNumero() + ", " +
                        d.getCodigoPostal() + " " +
                        d.getMunicipio()
                );
            } else {
                return new SimpleStringProperty("");
            }
        });

        colTipoCliente.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getTipoCliente() != null
                                ? data.getValue().getTipoCliente().toString()
                                : ""
                ));

        colDni.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDni()));
        colCif.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCif()));
        colObservaciones.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getObservaciones()));
        colIdComercialAsignado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getIdComercialAsignado()));
    }

    /**
     * Carga los clientes desde MongoDB en la tabla.
     */
    private void cargarClientes() {

        listaClientes = FXCollections.observableArrayList();

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Clientes");

            for (Document doc : coleccion.find()) {

                Cliente c = new Cliente();

                c.setId(doc.getObjectId("_id").toString());
                c.setNombre(doc.getString("nombre"));
                c.setApellidos(doc.getString("apellidos"));
                c.setTelefono(CifradoDatos.descifrarSiEsPosible(doc.getString("telefono")));
                c.setEmail(doc.getString("email"));

                Document dir = (Document) doc.get("direccion");

                if (dir != null) {
                    c.setDireccion(new Direccion(
                            dir.getString("calle"),
                            dir.getString("numero"),
                            dir.getString("codigoPostal"),
                            dir.getString("municipio"),
                            dir.getString("provincia")
                    ));
                }

                String tipo = doc.getString("tipoCliente");
                if (tipo != null) {
                    c.setTipoCliente(Cliente.TipoCliente.valueOf(tipo));
                }

                c.setDni(CifradoDatos.descifrarSiEsPosible(doc.getString("dni")));
                c.setCif(doc.getString("cif"));
                c.setNumeroCuenta(CifradoDatos.descifrarSiEsPosible(doc.getString("numeroCuenta")));
                c.setObservaciones(doc.getString("observaciones"));
                c.setIdComercialAsignado(doc.getString("idComercialAsignado"));

                listaClientes.add(c);
            }

            tablaClientes.setItems(listaClientes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Solicita confirmación antes de salir.
     *
     * @param event evento de acción
     */
    @FXML
    private void confirmarSalida(ActionEvent event) {
        if (AlertasSolarManager.confirmar("Confirmación", "¿Desea salir sin guardar?")) {
            volver(event);
        }
    }

    /**
     * Vuelve a la pantalla principal.
     *
     * @param event evento de acción
     */
    private void volver(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/components/pantallas/erp/plantillaGeneral/plantillaGeneral.fxml")
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la pantalla de alta de cliente.
     *
     * @param event evento de acción
     */
    @FXML
    private void añadirCliente(ActionEvent event) {
        cambiarPantalla(event,
            "/components/pantallas/comercial/pantallaAltaCliente/altaCliente.fxml");
    }

    /**
     * Abre la pantalla de cálculo de instalación del cliente seleccionado.
     *
     * @param event evento de acción
     */
    @FXML
    private void calcularInstalacion(ActionEvent event) {

        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            AlertasSolarManager.seleccionarClienteParaEditar();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/components/pantallas/comercial/calculoInstalacion/CalculoInstalacion.fxml")
            );

            Parent root = loader.load();

            components.pantallas.comercial.calculoInstalacion.CalculoInstalacionController controller = loader.getController();
            controller.setCliente(seleccionado);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            AlertasSolarManager.errorCambioPantalla();
        }
    }

    /**
     * Abre la pantalla de edición del cliente seleccionado.
     *
     * @param event evento de acción
     */
    @FXML
    private void modificar(ActionEvent event) {

        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            AlertasSolarManager.seleccionarClienteParaEditar();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/components/pantallas/comercial/pantallaAltaCliente/altaCliente.fxml")
            );

            Parent root = loader.load();

            components.pantallas.comercial.pantallaAltaCliente.AltaClienteController controller = loader.getController();
            controller.setCliente(seleccionado);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            AlertasSolarManager.errorAbrirEdicionCliente();
        }
    }

    /**
     * Elimina el cliente seleccionado previa confirmación.
     */
    @FXML
    private void eliminarCliente() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            AlertasSolarManager.seleccionarClienteParaEliminar();
            return;
        }

        boolean confirmar = AlertasSolarManager.confirmar(
                "Eliminar Cliente",
                "¿Seguro que deseas eliminar el cliente: " + seleccionado.getNombre() + "?",
                "Eliminar",
                "Cancelar"
        );

        if (!confirmar) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Clientes");

            coleccion.deleteOne(new Document("_id", new org.bson.types.ObjectId(seleccionado.getId())));

            AlertasSolarManager.clienteEliminadoCorrectamente();
            cargarClientes();

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("Error al eliminar Cliente: " + e.getMessage());
        }
    }

    /**
     * Filtra la tabla de clientes según el texto introducido.
     */
    @FXML
    private void buscar() {
        String filtro = txtFiltro.getText().toLowerCase();

        ObservableList<Cliente> filtrados = FXCollections.observableArrayList();

        for (Cliente c : listaClientes) {
            if (c.getNombre().toLowerCase().contains(filtro) ||
                c.getApellidos().toLowerCase().contains(filtro) ||
                c.getEmail().toLowerCase().contains(filtro)) {

                filtrados.add(c);
            }
        }

        tablaClientes.setItems(filtrados);
    }

    /**
     * Acción del menú lateral de clientes.
     *
     * @param event evento de acción
     */
    @FXML private void irClientes(ActionEvent event) {}

    /**
     * Navega a la pantalla de comerciales.
     *
     * @param event evento de acción
     */
    @FXML private void irComerciales(ActionEvent event) {
        cambiarPantalla(event, "/components/pantallas/erp/pantallaComerciales/pantallaComerciales.fxml");
    }

    /**
     * Navega a la pantalla de proveedores.
     *
     * @param event evento de acción
     */
    @FXML private void irProveedores(ActionEvent event) {
        cambiarPantalla(event, "/components/pantallas/erp/pantallaProveedor/pantallaProveedor.fxml");
    }

    /**
     * Navega a la pantalla de stock.
     *
     * @param event evento de acción
     */
    @FXML private void irStock(ActionEvent event) {
        cambiarPantalla(event, "/components/pantallas/erp/pantallaStock/pantallaStock.fxml");
    }

    /**
     * Navega a la pantalla de presupuestos.
     *
     * @param event evento de acción
     */
    @FXML private void irPresupuestos(ActionEvent event) {
        cambiarPantalla(event, "/components/pantallas/erp/pantallaPresupuesto/pantallaPresupuesto.fxml");
    }
    
    @FXML private void irInstalaciones(ActionEvent event) {
        cambiarPantalla(event, "/components/pantallas/erp/pantallaInstalaciones/pantallaInstalaciones.fxml");
    }
    

    /**
     * Muestra la pantalla de informes en desarrollo.
     *
     * @param event evento de acción
     */
    @FXML private void irInformes(ActionEvent event) {
        AlertasSolarManager.pantallaEnDesarrollo();
    }

    /**
     * Vuelve a la plantilla general.
     *
     * @param event evento de acción
     */
    @FXML
    private void volverInicio(ActionEvent event) {
        cambiarPantalla(event,
            "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }

    /**
     * Cambia la pantalla actual por otra indicada.
     *
     * @param event evento de acción
     * @param rutaFXML ruta del fichero FXML
     */
    private void cambiarPantalla(ActionEvent event, String rutaFXML) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            AlertasSolarManager.errorCambioPantalla();
        }
    }
}