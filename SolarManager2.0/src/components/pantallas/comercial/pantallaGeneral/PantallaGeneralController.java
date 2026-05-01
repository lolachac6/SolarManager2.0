package components.pantallas.comercial.pantallaGeneral;

import components.navigation.SessionContext;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.application.Platform;


import javafx.scene.layout.AnchorPane;


import modelo.Cliente;
import modelo.Direccion;
import org.bson.Document;
import utils.AlertasSolarManager;
import components.tablaClientes.TablaClientesController;
import components.pantallas.comercial.calculoInstalacion.CalculoInstalacionController;

/**
 * Controlador de la pantalla general comercial.
 *
 * <p>Gestiona la carga de iconos, la navegación entre pantallas
 * y la carga dinámica de componentes en el panel central.</p>
 *
 * @author Iván
 */
public class PantallaGeneralController implements Initializable {

    @FXML
    private Button btnClientes;
    @FXML
    private Button btnInstalaciones;


    @FXML private AnchorPane panelTabla;



    private TablaClientesController tablaClientesController;

    /**
     * Inicializa la pantalla y carga los iconos del panel principal.
     *
     * @param url URL de inicialización
     * @param rb recursos asociados
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Platform.runLater(() -> {
            cargarIcono(btnClientes, "/assets/iconos/clientes.jpg");
            cargarIcono(btnInstalaciones, "/assets/iconos/instalaciones.jpg");
        });
    }

    /**
     * Carga un icono en el botón indicado.
     *
     * @param boton botón destino
     * @param rutaIcono ruta de la imagen
     */
    private void cargarIcono(Button boton, String rutaIcono) {

        try {
            URL recurso = getClass().getResource(rutaIcono);

            if (recurso == null) {
                System.out.println("❌ No se encontró la imagen: " + rutaIcono);
                return;
            }

            Image imagen = new Image(recurso.toExternalForm());
            ImageView imageView = new ImageView(imagen);

            double size = boton.getWidth() * 0.6;

            if (size <= 0) {
                size = 120;
            }

            imageView.setFitWidth(size);
            imageView.setPreserveRatio(true);

            boton.setGraphic(imageView);
            boton.setContentDisplay(ContentDisplay.TOP);
            boton.setGraphicTextGap(15);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Abre la pantalla de alta de cliente.
     *
     * @param e evento de acción
     */
    @FXML
    private void abrirAltaCliente(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/comercial/pantallaAltaCliente/AltaCliente.fxml");
    }

    /**
     * Abre la pantalla de cálculo de instalación con el cliente seleccionado
     * en la tabla de clientes cargada en el panel central.
     *
     * @param e evento de acción
     */
    @FXML
    private void abrirCalculoInstalacion(ActionEvent e) {
      /**  cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/comercial/calculoInstalacion/CalculoInstalacion.fxml");*/

        if (tablaClientesController == null) {
            AlertasSolarManager.warning(
                    "Clientes no cargados",
                    "Primero debe cargar la tabla de clientes en el panel lateral."
            );
            return;
        }

        Document clienteDoc = tablaClientesController.getClienteSeleccionado();

        if (clienteDoc == null) {
            AlertasSolarManager.warning(
                    "Cliente no seleccionado",
                    "Debe seleccionar un cliente de la tabla para calcular la instalación."
            );
            return;
        }
        
        SessionContext.setPantallaOrigen("/components/pantallas/comercial/pantallaClientes/PantallaClientes.fxml");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/components/pantallas/comercial/calculoInstalacion/CalculoInstalacion.fxml"
            ));

            Parent root = loader.load();

            CalculoInstalacionController controller = loader.getController();
            controller.setCliente(convertirDocumentoACliente(clienteDoc));

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException ex) {
            ex.printStackTrace();
            AlertasSolarManager.errorCambioPantalla();
        }
    }


    /**
     * Carga la tabla de clientes en el panel central.
     *
     * @param e evento de acción
     */
    @FXML
    private void mostrarClientes(ActionEvent e) {
        cargarEnPanel("/components/tablaClientes/tablaClientes.fxml");
    }

    /**
     * Carga la tabla de Instalaciones en el panel cental
     *
     * @param e evento de acción
     */
    @FXML
    private void mostrarInstalaciones(ActionEvent e) {
        //System.out.println("👉 Instalaciones aún no implementado");
        cargarEnPanel("/components/tablaInstalaciones/tablaInstalaciones.fxml");
    }

    /**
     * Carga la tabla de presupuestos en el panel central.
     *
     * @param e evento de acción
     */
    @FXML
    private void mostrarPresupuestos(ActionEvent e) {
        cargarEnPanel("/components/tablaPresupuestos/tablaPresupuestos.fxml");
    }

    /**
     * Carga un componente FXML dentro del panel central.
     *
     * @param rutaFXML ruta del componente a cargar
     */
    private void cargarEnPanel(String rutaFXML) {
        try {

            URL resource = getClass().getResource(rutaFXML);

            if (resource == null) {
                System.out.println("❌ No se encontró el FXML: " + rutaFXML);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Node contenido = loader.load();

            if ("/components/tablaClientes/tablaClientes.fxml".equals(rutaFXML)) {
                tablaClientesController = loader.getController();
            }

            panelTabla.getChildren().clear();
            panelTabla.getChildren().add(contenido);

            AnchorPane.setTopAnchor(contenido, 0.0);
            AnchorPane.setBottomAnchor(contenido, 0.0);
            AnchorPane.setLeftAnchor(contenido, 0.0);
            AnchorPane.setRightAnchor(contenido, 0.0);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

      
    /**
     * Cambia la pantalla actual por otra indicada.
     *
     * @param nodo nodo origen
     * @param rutaFXML ruta del fichero FXML
     */
    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {

            URL resource = getClass().getResource(rutaFXML);

            if (resource == null) {
                System.out.println("❌ Ruta incorrecta: " + rutaFXML);
                return;
            }

            Parent root = FXMLLoader.load(resource);

            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Convierte un documento de MongoDB a objeto Cliente.
     *
     * @param doc documento del cliente
     * @return cliente convertido
     */
    private Cliente convertirDocumentoACliente(Document doc) {
        Cliente cliente = new Cliente();

        if (doc.getObjectId("_id") != null) {
            cliente.setId(doc.getObjectId("_id").toString());
        }

        cliente.setNombre(doc.getString("nombre"));
        cliente.setApellidos(doc.getString("apellidos"));
        cliente.setTelefono(doc.getString("telefono"));
        cliente.setEmail(doc.getString("email"));
        cliente.setDni(doc.getString("dni"));
        cliente.setCif(doc.getString("cif"));
        cliente.setNumeroCuenta(doc.getString("numeroCuenta"));
        cliente.setObservaciones(doc.getString("observaciones"));
        cliente.setIdComercialAsignado(doc.getString("idComercialAsignado"));

        String tipo = doc.getString("tipoCliente");
        if (tipo != null) {
            cliente.setTipoCliente(Cliente.TipoCliente.valueOf(tipo));
        }

        Document dir = (Document) doc.get("direccion");
        if (dir != null) {
            cliente.setDireccion(new Direccion(
                    dir.getString("calle"),
                    dir.getString("numero"),
                    dir.getString("codigoPostal"),
                    dir.getString("municipio"),
                    dir.getString("provincia")
            ));
        }

        return cliente;
    }
}
