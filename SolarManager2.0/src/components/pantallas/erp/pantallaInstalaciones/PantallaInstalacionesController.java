package components.pantallas.erp.pantallaInstalaciones;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import components.pantallas.comercial.calculoInstalacion.CalculoInstalacionController;

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

import modelo.InstalacionFotovoltaica;
import modelo.Direccion;

import org.bson.Document;

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

    private ObservableList<InstalacionFotovoltaica> listaInstalaciones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarInstalaciones();
    }

    private void configurarColumnas() {

        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().toString()));

        // 🔴 CORRECCIÓN AQUÍ
        colIdCliente.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getIdCliente()
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

                i.setPotenciaInstalada(doc.getDouble("potenciaInstalada"));
                i.setNumeroPaneles(doc.getInteger("numeroPaneles"));
                i.setProduccionEstimada(doc.getDouble("produccionEstimada"));
                i.setAhorroEstimado(doc.getDouble("ahorroEstimado"));

                // 🔴 AÑADIDO (para que no sea null)
                i.setIdCliente(doc.getString("idCliente"));

                Document dir = (Document) doc.get("direccion");

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

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void añadirInstalacion(ActionEvent event) {
        cambiarPantalla(event,
            "/components/pantallas/comercial/calculoInstalacion/calculoInstalacion.fxml");
    }

    @FXML
    private void modificar(ActionEvent event) {

        InstalacionFotovoltaica seleccionada = tablaInstalaciones.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Debe seleccionar una instalación para editar", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/components/pantallas/comercial/calculoInstalacion/calculoInstalacion.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

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
                String.valueOf(i.getNumeroPaneles()).contains(filtro)) {

                filtrados.add(i);
            }
        }

        tablaInstalaciones.setItems(filtrados);
    }

    @FXML private void irClientes(ActionEvent event) {}
    @FXML private void irComerciales(ActionEvent event) {}
    @FXML private void irProveedores(ActionEvent event) {}
    @FXML private void irStock(ActionEvent event) {}
    @FXML private void irPresupuestos(ActionEvent event) {}
    @FXML private void irInformes(ActionEvent event) {}

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