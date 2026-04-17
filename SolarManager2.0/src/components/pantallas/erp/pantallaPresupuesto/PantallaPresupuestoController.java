package components.pantallas.erp.pantallaPresupuesto;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import components.erp.erp.pantallaAltaPresupuesto.PantallaAltaPresupuestoController;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import modelo.Presupuesto;
import org.bson.Document;

public class PantallaPresupuestoController implements Initializable {

    @FXML private TableView<Presupuesto> tablaPresupuestos;
    @FXML private TableColumn<Presupuesto, String> colId;
    @FXML private TableColumn<Presupuesto, String> colIdCliente;
    @FXML private TableColumn<Presupuesto, String> colIdComercial;
    @FXML private TableColumn<Presupuesto, String> colFechaCreacion;
    @FXML private TableColumn<Presupuesto, String> colEstado;
    @FXML private TableColumn<Presupuesto, String> colSubtotal;
    @FXML private TableColumn<Presupuesto, String> colIva;
    @FXML private TableColumn<Presupuesto, String> colTotal;

    @FXML private TextField txtFiltro;

    private ObservableList<Presupuesto> lista = FXCollections.observableArrayList();
 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {       
      
   System.out.println("INIT START");

    try {
        System.out.println("tabla: " + tablaPresupuestos);

        System.out.println("colId: " + colId);
        System.out.println("colCliente: " + colIdCliente);
        System.out.println("colIdComercial: " + colIdComercial);
        System.out.println("colFecha: " + colFechaCreacion);
        System.out.println("colEstado: " + colEstado);
        System.out.println("colSubTotal: " + colSubtotal);
        System.out.println("colIVA: " + colIva);
        System.out.println("colTotal: " + colTotal);

        // ahora añade UNA línea cada vez
        colId.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getId())
        );

        System.out.println("colId OK");

        colIdCliente.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getIdCliente())
        );

        System.out.println("colCliente OK");

         colIdComercial.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getIdComercial())
    );
          System.out.println("colIdComercial OK");
          
    colFechaCreacion.setCellValueFactory(data -> {
        LocalDate fecha = data.getValue().getFechaCreacion();
        return new SimpleStringProperty(fecha != null ? fecha.toString() : "");
    });
    
     System.out.println("colFechaCreacion OK");

    colEstado.setCellValueFactory(data ->
        new SimpleStringProperty(
            data.getValue().getEstado() != null 
                ? data.getValue().getEstado().toString()
                : ""
        )
    );
    
     System.out.println("colEstado OK");

    colSubtotal.setCellValueFactory(data ->
        new SimpleStringProperty(String.valueOf(data.getValue().getSubtotal()))
    );
    
     System.out.println("colSubtotal OK");

    colIva.setCellValueFactory(data ->
        new SimpleStringProperty(String.valueOf(data.getValue().getIva()))
    );
     System.out.println("colIva OK");

    colTotal.setCellValueFactory(data ->
        new SimpleStringProperty(String.valueOf(data.getValue().getTotal()))
    );
     System.out.println("colTotal OK");

    } catch (Exception e) {
        e.printStackTrace();
    }
    }
    
    
    
    private void cargarPresupuestos() throws IOException {
    
     MongoDatabase db = MongoConnection.conectar();
    MongoCollection<Document> coleccion = db.getCollection("Presupuestos");

    lista.clear();

    for (Document doc : coleccion.find()) {

        Presupuesto p = new Presupuesto();

        Object idObj = doc.get("_id");
        p.setId(idObj.toString());

        p.setIdCliente(doc.getString("idCliente"));
        p.setIdComercial(doc.getString("idComercial"));

        String t = doc.getString("fechaCreacion");
        if (t != null) {
            try {
                p.setFechaCreacion(LocalDate.parse(t));
            } catch (DateTimeParseException e) {}
        }

        String estado = doc.getString("estado");
        if (estado != null) {
            try {
                p.setEstado(Presupuesto.EstadoPresupuesto.valueOf(estado.toUpperCase()));
            } catch (Exception e) {
                System.err.println("Estado inválido: " + estado);
            }
        }

        p.setSubtotal(getDouble(doc, "subTotal"));
        p.setIva(getDouble(doc, "iva"));
        p.setTotal(getDouble(doc, "total"));

        lista.add(p);
    }

    tablaPresupuestos.setItems(lista);
       
    }
    
  

    // =========================
    // BOTONES
    // =========================
    
      @FXML
    private void añadirPresupuesto(javafx.event.ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/components/erp/erp/pantallaAltaPresupuesto/pantallaAltaPresupuesto.fxml")
            );

            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (IOException ex) {
        }
    }
    
    
    @FXML
private void modificar(ActionEvent event) {

    Presupuesto seleccionado = tablaPresupuestos.getSelectionModel().getSelectedItem();

    if (seleccionado == null) {
        mostrarAlerta("Debe seleccionar un presupuesto para editar", Alert.AlertType.WARNING);
        return;
    }

    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/components/pantallas/erp/pantallaPresupuesto/pantallaAltaPresupuesto.fxml")
        );

        Parent root = loader.load();

        PantallaAltaPresupuestoController controller = loader.getController();
        controller.setPresupuesto(seleccionado);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();

    } catch (IOException e) {
        e.printStackTrace();
        mostrarAlerta("Error al abrir pantalla de edición", Alert.AlertType.ERROR);
    }
}
    
    
    
    
     @FXML
    public void buscarFiltro() {

    String filtro = txtFiltro.getText();

    if (filtro == null || filtro.trim().isEmpty()) {
        tablaPresupuestos.setItems(lista);
        return;
    }

    String f = filtro.toLowerCase();

    ObservableList<Presupuesto> filtrada = FXCollections.observableArrayList();

    for (Presupuesto p : lista) {

        if ((p.getId() != null && p.getId().toLowerCase().contains(f))
                || (p.getIdCliente() != null && p.getIdCliente().toLowerCase().contains(f))
                || (p.getIdComercial() != null && p.getIdComercial().toLowerCase().contains(f))
                || (p.getEstado() != null && p.getEstado().toString().toLowerCase().contains(f))
                || (p.getFechaCreacion() != null && p.getFechaCreacion().toString().toLowerCase().contains(f))
                || (String.valueOf(p.getSubtotal()).contains(f))
                || (String.valueOf(p.getIva()).contains(f))
                || (String.valueOf(p.getTotal()).contains(f))) {

            filtrada.add(p);
        }
    }

    tablaPresupuestos.setItems(filtrada);
}


    @FXML
    private void eliminarPresupuesto() {

    Presupuesto seleccionado = tablaPresupuestos.getSelectionModel().getSelectedItem();

    if (seleccionado == null) {
        mostrarAlerta("Selecciona un presupuesto de la tabla para eliminar", Alert.AlertType.WARNING);
        return;
    }

    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
    confirmacion.setTitle("Confirmar eliminación");
    confirmacion.setHeaderText("Eliminar Presupuesto");
    confirmacion.setContentText("¿Seguro que deseas eliminar el presupuesto: " + seleccionado.getId() + "?");

    ButtonType aceptar = new ButtonType("Eliminar", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

    confirmacion.getButtonTypes().setAll(aceptar, cancelar);

    if (confirmacion.showAndWait().orElse(cancelar) != aceptar) {
        return;
    }

    try {
        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<Document> coleccion = db.getCollection("Presupuestos");

        coleccion.deleteOne(new Document("_id",
                new org.bson.types.ObjectId(seleccionado.getId())));

        mostrarAlerta("Presupuesto eliminado correctamente", Alert.AlertType.INFORMATION);

        cargarPresupuestos();

    } catch (Exception e) {
        e.printStackTrace();
        mostrarAlerta("Error al eliminar presupuesto: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}
    

  @FXML
private void volverInicio(ActionEvent event) {
    cambiarPantalla((Node) event.getSource(), 
        "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
}

@FXML
private void anadirComercial(ActionEvent event) {
    cambiarPantalla((Node) event.getSource(), 
        "/components/pantallas/erp/pantallaAltaComercial/pantallaAltaComercial.fxml");
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
private void irInformes(ActionEvent event) {
    cambiarPantalla((Node) event.getSource(), 
        "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
}

@FXML
    private void irInstalaciones(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(), "/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml");
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

            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));

            Platform.runLater(() -> stage.setMaximized(true));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setContentText(msg);
        alert.showAndWait();
    }

   private double getDouble(Document doc, String campo) {
    Object valor = doc.get(campo);
    return valor != null ? Double.parseDouble(valor.toString()) : 0.0;
}
}