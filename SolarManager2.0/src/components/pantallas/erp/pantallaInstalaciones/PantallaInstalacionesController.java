package components.pantallas.erp.pantallaInstalaciones;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import modelo.InstalacionFotovoltaica;
import modelo.Direccion;
import org.bson.Document;
import utils.AlertasSolarManager;

/**
 * Controlador de la pantalla de instalaciones.
 *
 * <p>Gestiona la visualización de instalaciones fotovoltaicas, la carga
 * de datos desde MongoDB, la búsqueda por filtro y la navegación
 * entre pantallas del ERP.</p>
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
    @FXML private TextField txtFiltro;
    @FXML private Button  hacerPresupuesto;

    private ObservableList<InstalacionFotovoltaica> listaInstalaciones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarInstalaciones();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getId() != null ? data.getValue().getId() : ""
                ));

        colIdCliente.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getIdCliente() != null ? data.getValue().getIdCliente() : ""
                ));

        colPotencia.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().getPotenciaInstalada())
                ));

        colPaneles.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().getNumeroPaneles())
                ));

        colProduccion.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().getProduccionEstimada())
                ));

        colAhorro.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().getAhorroEstimado())
                ));

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
    }

    private void cargarInstalaciones() {
        listaInstalaciones = FXCollections.observableArrayList();

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Instalaciones");

            for (Document doc : coleccion.find()) {
                InstalacionFotovoltaica i = new InstalacionFotovoltaica();

                if (doc.getObjectId("_id") != null) {
                    i.setId(doc.getObjectId("_id").toString());
                }

                i.setIdCliente(doc.getString("idCliente"));

                Number potencia = doc.get("potenciaInstalada", Number.class);
                Number paneles = doc.get("numeroPaneles", Number.class);
                Number produccion = doc.get("produccionEstimada", Number.class);
                Number ahorro = doc.get("ahorroEstimado", Number.class);

                i.setPotenciaInstalada(potencia != null ? potencia.doubleValue() : 0.0);
                i.setNumeroPaneles(paneles != null ? paneles.intValue() : 0);
                i.setProduccionEstimada(produccion != null ? produccion.doubleValue() : 0.0);
                i.setAhorroEstimado(ahorro != null ? ahorro.doubleValue() : 0.0);

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
        }
    }

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
    private void añadirInstalacion(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/comercial/calculoInstalacion/CalculoInstalacion.fxml");
    }
    
    @FXML
    private void hacerPresupuesto(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaPresupuesto/pantallaPresupuesto.fxml");
    }
    
    @FXML
    private void modificar(ActionEvent event) {
        InstalacionFotovoltaica seleccionada = tablaInstalaciones.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            AlertasSolarManager.seleccionarInstalacionParaEditar();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/components/pantallas/comercial/calculoInstalacion/CalculoInstalacion.fxml")
            );

            Parent root = loader.load();

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
            mostrarAlerta("Error al abrir pantalla de edición", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void buscar() {
        String filtro = txtFiltro.getText().toLowerCase();
        ObservableList<InstalacionFotovoltaica> filtrados = FXCollections.observableArrayList();

        for (InstalacionFotovoltaica i : listaInstalaciones) {
            if (String.valueOf(i.getPotenciaInstalada()).contains(filtro) ||
                String.valueOf(i.getNumeroPaneles()).contains(filtro) ||
                (i.getIdCliente() != null && i.getIdCliente().toLowerCase().contains(filtro))) {

                filtrados.add(i);
            }
        }

        tablaInstalaciones.setItems(filtrados);
    }

    @FXML
    private void irClientes(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }

    @FXML
    private void irComerciales(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }

    @FXML
    private void irProveedores(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaProveedor/PantallaProveedor.fxml");
    }

    @FXML
    private void irStock(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }

    @FXML
    private void irPresupuestos(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }

    @FXML
    private void irInstalaciones(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml");
    }

    @FXML
    private void irInformes(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }

    @FXML
    private void volverInicio(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
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

    private void mostrarAlerta(String msg, Alert.AlertType tipo) {
        AlertasSolarManager.mostrar(tipo, null, msg);
    }
}