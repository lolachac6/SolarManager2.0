package components.pantallas.erp.pantallaComerciales;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import components.pantallas.erp.pantallaAltaComercial.PantallaAltaComercialController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import modelo.Comercial;
import modelo.Direccion;
import org.bson.Document;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

public class pantallaComercialesController implements Initializable {

    // =========================
    // TABLA
    // =========================
    @FXML
    private TableView<Comercial> tablaComerciales;

    @FXML
    private TableColumn<Comercial, String> colId;
    @FXML
    private TableColumn<Comercial, String> colNombre;
    @FXML
    private TableColumn<Comercial, String> colActivo;
    @FXML
    private TableColumn<Comercial, String> colApellidos;
    @FXML
    private TableColumn<Comercial, String> colTelefono;
    @FXML
    private TableColumn<Comercial, String> colEmail;
    @FXML
    private TableColumn<Comercial, String> colDireccion;
    @FXML
    private TableColumn<Comercial, String> colTipoContrato;
    @FXML
    private TableColumn<Comercial, String> colDni;
    @FXML
    private TableColumn<Comercial, String> colNumeroCuenta;
    @FXML
    private TableColumn<Comercial, String> colCentroTrabajo;
    @FXML
    private TableColumn<Comercial, String> colObservaciones;

    @FXML
    private TextField txtFiltro;

    private ObservableList<Comercial> listaOriginal = FXCollections.observableArrayList();

    // =========================
    // INIT
    // =========================
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colId.setCellValueFactory(data
                -> new SimpleStringProperty(
                        data.getValue().getId() != null ? data.getValue().getId() : ""
                )
        );

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNumeroCuenta.setCellValueFactory(new PropertyValueFactory<>("numeroCuenta"));
        colCentroTrabajo.setCellValueFactory(new PropertyValueFactory<>("centroTrabajo"));
        colObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        colDireccion.setCellValueFactory(data
                -> new SimpleStringProperty(
                        data.getValue().getDireccion() != null
                        ? data.getValue().getDireccion().toString()
                        : ""
                )
        );

        colTipoContrato.setCellValueFactory(data
                -> new SimpleStringProperty(
                        data.getValue().getTipoContrato() != null
                        ? data.getValue().getTipoContrato().toString()
                        : ""
                )
        );

        colActivo.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getActivo() ? "Activo" : "No")
        );

        try {
            obtenerComercialesTabla();
        } catch (IOException e) {
        }

        txtFiltro.textProperty().addListener((obs, oldVal, newVal) -> {
            buscarFiltro();
        });
    }

    // =========================
    // NAVEGACIÓN
    // =========================
    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException e) {
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
    private void irInformes(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }

    @FXML
    private void anadirComercial(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaAltaComercial/pantallaAltaComercial.fxml");
    }

    // =========================
    // CARGAR DATOS
    // =========================
    public void obtenerComercialesTabla() throws IOException {

        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<Document> coleccion = db.getCollection("Comerciales");

        listaOriginal.clear();

        for (Document doc : coleccion.find()) {

            Document dirDoc = doc.get("direccion", Document.class);

            Direccion direccion = null;
            if (dirDoc != null) {
                direccion = new Direccion(
                        dirDoc.getString("calle"),
                        dirDoc.getString("numero"),
                        dirDoc.getString("codigoPostal"),
                        dirDoc.getString("municipio"),
                        dirDoc.getString("provincia")
                );
            }

            Comercial c = new Comercial();

            c.setId(doc.getObjectId("_id").toString());
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
                c.setTipoContrato(Comercial.TipoContrato.valueOf(tipo));
            }

            listaOriginal.add(c);
        }

        tablaComerciales.setItems(listaOriginal);
    }

    // =========================
    // FILTRO
    // =========================
    @FXML
    public void buscarFiltro() {

        String filtro = txtFiltro.getText().toLowerCase();

        if (filtro.isEmpty()) {
            tablaComerciales.setItems(listaOriginal);
            return;
        }

        ObservableList<Comercial> filtrada = FXCollections.observableArrayList();

        for (Comercial c : listaOriginal) {

            String tipoContrato = c.getTipoContrato() != null
                    ? c.getTipoContrato().toString().toLowerCase()
                    : "";

            String activoTexto = c.getActivo() ? "activo" : "no";

            if ((c.getNombre() != null && c.getNombre().toLowerCase().contains(filtro))
                    || (c.getApellidos() != null && c.getApellidos().toLowerCase().contains(filtro))
                    || (c.getEmail() != null && c.getEmail().toLowerCase().contains(filtro))
                    || (c.getDni() != null && c.getDni().toLowerCase().contains(filtro))
                    || (c.getTelefono() != null && c.getTelefono().toLowerCase().contains(filtro))
                    || tipoContrato.contains(filtro)
                    || activoTexto.contains(filtro)) {
                filtrada.add(c);
            }
        }

        tablaComerciales.setItems(filtrada);
    }

    @FXML

    public void modificarComercial(ActionEvent event) {
        Comercial seleccionado = tablaComerciales.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, selecciona un comercial de la tabla para modificar.", AlertType.WARNING);
            return;
        }

        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/pantallas/erp/pantallaAltaComercial/pantallaAltaComercial.fxml"));
            Parent root = loader.load();

            PantallaAltaComercialController controller = loader.getController();

            controller.cargarDatos(seleccionado);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen(); // Opcional, para que quede bien posicionada
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la pantalla de edición: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String mensaje, String por_favor_selecciona_un_comercial_de_la_t, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Solar Manager");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
