package components.pantallas.erp.pantallaClientes;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
 * Controlador de la pantalla de gestión de clientes del ERP SolarManager.
 *
 * <p>Se encarga de:
 * <ul>
 *   <li>Cargar y mostrar los clientes en la tabla.</li>
 *   <li>Concatenar nombre y apellidos en la columna "Cliente".</li>
 *   <li>Resolver el nombre del comercial asignado mediante un mapa precargado.</li>
 *   <li>Aplicar filtros dinámicos mientras el usuario escribe.</li>
 *   <li>Gestionar navegación hacia pantallas de alta, edición, detalle e instalación.</li>
 *   <li>Eliminar clientes de la base de datos.</li>
 * </ul>
 * </p>
 *
 * <p>La tabla se actualiza automáticamente gracias a un listener sobre el campo de búsqueda.</p>
 *
 * @author Iván
 */
public class PantallaClientesController implements Initializable {
    

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colCliente;;   
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
    
    private Map<String, String> mapaComerciales = new HashMap<>();
    
    /**
     * Inicializa la pantalla configurando columnas, cargando comerciales,
     * cargando clientes y activando el filtro dinámico.
     *
     * @param url ubicación del archivo FXML.
     * @param rb recursos adicionales.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {       
        configurarColumnas();
        cargarMapaComerciales();
        cargarClientes();
        
        txtFiltro.textProperty().addListener((obs, oldVal, newVal) -> buscar());
    }
    
    /**
     * Carga todos los comerciales desde MongoDB y los almacena en un mapa
     * para resolver rápidamente el nombre del comercial asignado a cada cliente.
     */
    private void cargarMapaComerciales() {
        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Comerciales");

            for (Document doc : coleccion.find()) {
                String id = doc.getObjectId("_id").toHexString();
                String nombre = doc.getString("nombre");
                String apellidos = doc.getString("apellidos");
                
                String nombreCompleto = nombre + " " + apellidos;
                mapaComerciales.put(id, nombreCompleto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Configura todas las columnas de la tabla, incluyendo:
     * <ul>
     *   <li>Concatenación de nombre + apellidos.</li>
     *   <li>Dirección formateada.</li>
     *   <li>Nombre del comercial asignado.</li>
     * </ul>
     */
    private void configurarColumnas() {

        colCliente.setCellValueFactory(data -> {String nombre = data.getValue().getNombre();
            String apellidos = data.getValue().getApellidos();
            return new SimpleStringProperty(nombre + " " + apellidos);
        });
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
        colIdComercialAsignado.setCellValueFactory(data -> {String id = data.getValue().getIdComercialAsignado();
                String nombre = mapaComerciales.getOrDefault(id, "Sin comercial");
                return new SimpleStringProperty(nombre);
            });
         }
    
    /**
    * Obtiene el nombre completo (nombre + apellidos) de un comercial a partir de su ID.
    *
    * <p>El método consulta la colección "Comerciales" en MongoDB buscando un documento
    * cuyo campo <code>_id</code> coincida con el ID proporcionado. Si el comercial existe,
    * devuelve su nombre y apellidos concatenados. Si no existe, el ID es inválido o ocurre
    * algún error durante la consulta, devuelve una cadena vacía.</p>
    *
    * @param idComercial ID del comercial en formato hexadecimal (ObjectId).
    * @return el nombre completo del comercial, o una cadena vacía si no se encuentra
    *         o si el ID es nulo o vacío.
    */
    private String obtenerNombreComercial(String idComercial) {

    if (idComercial == null || idComercial.isEmpty()) {
        return "";
    }

    try {
        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<Document> clientes = db.getCollection("Comerciales");

        Document cliente = clientes.find(new Document("_id", new org.bson.types.ObjectId(idComercial))).first();

        if (cliente != null) {
            String nombre = valorSeguro(cliente.getString("nombre"));
            String apellidos = valorSeguro(cliente.getString("apellidos"));
            return nombre + " " + apellidos;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return "";
    }
    
    /**
    * Devuelve el valor proporcionado o una cadena vacía si es nulo.
    *
    * <p>Este método evita excepciones por valores nulos al concatenar cadenas
    * o al mostrar información en la interfaz.</p>
    *
    * @param valor cadena que puede ser nula.
    * @return el valor original si no es nulo, o una cadena vacía en caso contrario.
    */
    private String valorSeguro(String valor) {
        return valor != null ? valor : "";
    }
    
    /**
     * Carga todos los clientes desde MongoDB, descifra los campos necesarios
     * y los añade a la lista observable que alimenta la tabla.
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
                
                String idCom = doc.getString("idComercialAsignado");
                String nombreCompleto = mapaComerciales.getOrDefault(idCom, "Sin comercial");
                c.setIdComercialAsignado(idCom);          // ID real

                listaClientes.add(c);
            }

            tablaClientes.setItems(listaClientes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Confirma si el usuario desea salir sin guardar y vuelve a la pantalla anterior.
     */
    @FXML
    private void confirmarSalida(ActionEvent event) {
        if (AlertasSolarManager.confirmar("Confirmación", "¿Desea salir sin guardar?")) {
            volver(event);
        }
    }
    
    /**
     * Cambia a la pantalla anterior cerrando la actual.
     *
     * @param event evento que contiene el nodo origen.
     */
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
    
    /**
     * Abre la pantalla de alta de cliente.
     */
    @FXML
    private void añadirCliente(ActionEvent event) {
        cambiarPantalla((Node) event.getSource(),
            "/components/pantallas/comercial/pantallaAltaCliente/altaCliente.fxml");
    }
    
    /**
     * Abre la pantalla de cálculo de instalación para el cliente seleccionado.
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
    
    /**
     * Abre la pantalla de edición del cliente seleccionado.
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
    
    /**
     * Abre una ventana modal con el detalle del cliente seleccionado.
     */
    @FXML
    private void detalle(ActionEvent event) {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            AlertasSolarManager.warning("Aviso", "Debe seleccionar un cliente");
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
    
    /**
     * Elimina el cliente seleccionado de la base de datos tras confirmación del usuario.
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
     * Filtra la tabla de clientes en tiempo real según el texto introducido
     * en el campo de búsqueda. Busca por:
     * <ul>
     *   <li>Nombre</li>
     *   <li>Apellidos</li>
     *   <li>Nombre completo</li>
     *   <li>Email</li>
     *   <li>Teléfono</li>
     *   <li>DNI</li>
     *   <li>CIF</li>
     * </ul>
     */
    @FXML
    public void buscar() {
        String filtro = txtFiltro.getText().toLowerCase().trim();

        if (filtro.isEmpty()) {
            tablaClientes.setItems(listaClientes);
            return;
        }

        ObservableList<Cliente> filtrada = FXCollections.observableArrayList();

        for (Cliente c : listaClientes) {

            String nombre = c.getNombre() != null ? c.getNombre().toLowerCase() : "";
            String apellidos = c.getApellidos() != null ? c.getApellidos().toLowerCase() : "";
            String nombreCompleto = (c.getNombre() + " " + c.getApellidos()).toLowerCase();
            String email = c.getEmail() != null ? c.getEmail().toLowerCase() : "";
            String telefono = c.getTelefono() != null ? c.getTelefono().toLowerCase() : "";
            String dni = c.getDni() != null ? c.getDni().toLowerCase() : "";
            String cif = c.getCif() != null ? c.getCif().toLowerCase() : "";

            if (nombre.contains(filtro)
                    || apellidos.contains(filtro)
                    || nombreCompleto.contains(filtro)
                    || email.contains(filtro)
                    || telefono.contains(filtro)
                    || dni.contains(filtro)
                    || cif.contains(filtro)) {

                filtrada.add(c);
            }
        }

        tablaClientes.setItems(filtrada);
    }
    
    /** Navegación hacia otras pantallas del ERP. */
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