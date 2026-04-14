package components.pantallas.erp.pantallaClientes;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import modelo.Cliente;
import modelo.Direccion;

import org.bson.Document;

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

        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colApellidos.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getApellidos()));
        colTelefono.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTelefono()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        colDireccion.setCellValueFactory(data -> {
            Direccion d = data.getValue().getDireccion();
            if (d != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        d.getCalle() + " " + d.getNumero() + ", " +
                        d.getCodigoPostal() + " " +
                        d.getMunicipio()
                );
            } else {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });

        colTipoCliente.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getTipoCliente() != null
                                ? data.getValue().getTipoCliente().toString()
                                : ""
                ));

        colDni.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDni()));
        colCif.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCif()));
        colObservaciones.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getObservaciones()));
        colIdComercialAsignado.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getIdComercialAsignado()));
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
                c.setTelefono(doc.getString("telefono"));
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

                c.setDni(doc.getString("dni"));
                c.setCif(doc.getString("cif"));
                c.setNumeroCuenta(doc.getString("numeroCuenta"));
                c.setObservaciones(doc.getString("observaciones"));
                c.setIdComercialAsignado(doc.getString("idComercialAsignado"));

                listaClientes.add(c);
            }

            tablaClientes.setItems(listaClientes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // BOTONES
    // =========================
    
    @FXML
    private void confirmarSalida(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setContentText("¿Desea salir sin guardar?");

        ButtonType si = new ButtonType("Sí");
        ButtonType no = new ButtonType("No");

        alert.getButtonTypes().setAll(si, no);

        alert.showAndWait().ifPresent(respuesta -> {
            if (respuesta == si) {
                volver(event);
            }
        });
    }

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

    @FXML
    private void añadirCliente(ActionEvent event) {
        cambiarPantalla(event,
            "/components/pantallas/comercial/pantallaAltaCliente/altaCliente.fxml");
    }

    @FXML
    private void modificar(ActionEvent event) {

        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Debe seleccionar un cliente para editar", Alert.AlertType.WARNING);
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
            mostrarAlerta("Error al abrir pantalla de edición", Alert.AlertType.ERROR);
        }
    }
    
    
    @FXML
    private void eliminarCliente() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un cliente de la tabla para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar Cliente");
        confirmacion.setContentText("¿Seguro que deseas eliminar el cliente: " + seleccionado.getNombre() + "?");

        ButtonType aceptar = new ButtonType("Eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmacion.getButtonTypes().setAll(aceptar, cancelar);

        if (confirmacion.showAndWait().orElse(cancelar) != aceptar) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Clientes");

            coleccion.deleteOne(new Document("_id", new org.bson.types.ObjectId(seleccionado.getId())));

            mostrarAlerta("Cliente eliminado correctamente", Alert.AlertType.INFORMATION);

            cargarClientes();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al eliminar Cliente: " + e.getMessage(), Alert.AlertType.ERROR);
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

    // =========================
    // MENU LATERAL
    // =========================

    @FXML private void irClientes(ActionEvent event) {}

    @FXML private void irComerciales(ActionEvent event) {
        cambiarPantalla(event, "/components/pantallas/erp/pantallaComerciales/pantallaComerciales.fxml");
    }

    @FXML private void irProveedores(ActionEvent event) {
        cambiarPantalla(event, "/components/pantallas/erp/pantallaProveedor/pantallaProveedor.fxml");
    }

    @FXML private void irStock(ActionEvent event) {
        cambiarPantalla(event, "/components/pantallas/erp/pantallaStock/pantallaStock.fxml");
    }

    @FXML private void irPresupuestos(ActionEvent event) {
        cambiarPantalla(event, "/components/pantallas/erp/pantallaPresupuesto/pantallaPresupuesto.fxml");
    }
    
    @FXML private void irInstalaciones(ActionEvent event) {
        cambiarPantalla(event, "/components/pantallas/erp/pantallaInstalaciones/pantallaInstalaciones.fxml");
    }
    

    @FXML private void irInformes(ActionEvent event) {
        mostrarAlerta("Pantalla en desarrollo", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void volverInicio(ActionEvent event) {
        cambiarPantalla(event,
            "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }

    private void cambiarPantalla(ActionEvent event, String rutaFXML) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            mostrarAlerta("Error al cambiar pantalla", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    
    
}