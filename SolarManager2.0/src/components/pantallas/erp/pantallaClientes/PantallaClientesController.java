package components.pantallas.erp.pantallaClientes;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarClientes();
    }

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

    @FXML
    private void confirmarSalida(ActionEvent event) {
        if (AlertasSolarManager.confirmar("Confirmación", "¿Desea salir sin guardar?")) {
            volver(event);
        }
    }

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

    @FXML
    private void añadirCliente(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/comercial/pantallaAltaCliente/altaCliente.fxml");
    }

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
            AlertasSolarManager.errorCambioPantalla();
        }
    }

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
            AlertasSolarManager.errorAbrirEdicionCliente();
        }
    }

    @FXML
    private void detalle(ActionEvent event) {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            AlertasSolarManager.seleccionarClienteParaEditar();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/components/pantallas/pantallaDetalleClientes/PantallaDetalleClientes.fxml")
            );

            Parent root = loader.load();

            components.pantallas.pantallaDetalleClientes.PantallaDetalleClientesController controller =
                    loader.getController();
            controller.cargarClientePorId(seleccionado.getId());

            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Stage modal = new Stage();
            modal.initOwner(stageActual);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.setScene(new Scene(root));
            modal.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo abrir el detalle del cliente.");
        }
    }

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

    @FXML private void irClientes(ActionEvent event) {}

    @FXML
    private void irComerciales(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }

    @FXML
    private void irProveedores(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(), "/components/pantallas/erp/pantallaProveedor/pantallaProveedor.fxml");
    }

    @FXML
    private void irStock(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(), "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }

    @FXML
    private void irPresupuestos(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(), "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }

    @FXML
    private void irInstalaciones(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(), "/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml");
    }

    @FXML
    private void irInformes(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(), "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }

    @FXML
    private void volverInicio(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }

    /**
     * Cambia la pantalla actual por otra indicada mediante su ruta FXML.
     *
     * @param nodo nodo origen desde el que se obtiene el Stage
     * @param rutaFXML ruta del fichero FXML a cargar
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
}