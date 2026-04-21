package components.pantallas.erp.pantallaPresupuesto;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dao.FacturaDAO;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Cliente;
import modelo.Direccion;
import modelo.Factura;
import modelo.InstalacionFotovoltaica;
import modelo.LineaPresupuesto;
import modelo.Presupuesto;
import org.bson.Document;
import org.bson.types.ObjectId;
import service.FacturaService;

import utils.CifradoDatos;
import utils.AlertasSolarManager;
import utils.PDFUtils;

/**
 * Controlador de la pantalla de presupuestos.
 *
 * <p>
 * Gestiona la carga de presupuestos en la tabla, el filtrado de resultados, la
 * apertura de la ventana modal de detalle y la navegación entre pantallas del
 * ERP.
 * </p>
 *
 * @author Iván
 */
public class PantallaPresupuestoController implements Initializable {

    @FXML
    private Button btnVolver;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnHacerFactura;
    @FXML
    private Button btnDetalle;
    @FXML
    private Button btnEliminar;

    @FXML
    private TextField txtFiltro;

    @FXML
    private TableView<Document> tablaPresupuestos;

    @FXML
    private TableColumn<Document, String> colIdCliente;
    @FXML
    private TableColumn<Document, String> colIdComercial;
    @FXML
    private TableColumn<Document, String> colFechaCreacion;
    @FXML
    private TableColumn<Document, String> colEstado;
    @FXML
    private TableColumn<Document, String> colSubtotal;
    @FXML
    private TableColumn<Document, String> colIva;
    @FXML
    private TableColumn<Document, String> colTotal;

    private ObservableList<Document> listaPresupuestos;

    /**
     * Inicializa el controlador configurando la tabla y cargando los datos.
     *
     * @param url ubicación del recurso
     * @param rb recursos internacionales
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarPresupuestos();
    }

    /**
     * Configura las columnas de la tabla de presupuestos.
     */
    private void configurarColumnas() {

        colIdCliente.setCellValueFactory(data -> {
            try {
                Object idClienteObj = data.getValue().get("idCliente");
                if (idClienteObj == null) {
                    return new SimpleStringProperty("");
                }

                String idCliente = String.valueOf(idClienteObj);

                MongoDatabase db = MongoConnection.conectar();
                MongoCollection<Document> coleccionClientes = db.getCollection("Clientes");

                Document clienteDoc;

                try {
                    clienteDoc = coleccionClientes.find(new Document("_id", new ObjectId(idCliente))).first();
                } catch (Exception e) {
                    clienteDoc = coleccionClientes.find(new Document("_id", idCliente)).first();
                }

                if (clienteDoc != null) {
                    String nombre = valorComoTexto(clienteDoc.get("nombre"));
                    String apellidos = valorComoTexto(clienteDoc.get("apellidos"));
                    String nombreCompleto = (nombre + " " + apellidos).trim();

                    if (!nombreCompleto.isEmpty()) {
                        return new SimpleStringProperty(nombreCompleto);
                    }
                }

                return new SimpleStringProperty(idCliente);

            } catch (Exception e) {
                return new SimpleStringProperty(valorComoTexto(data.getValue().get("idCliente")));
            }
        });

        colIdComercial.setCellValueFactory(data -> {
            try {
                Object idClienteObj = data.getValue().get("idCliente");
                if (idClienteObj == null) {
                    return new SimpleStringProperty("");
                }

                String idCliente = String.valueOf(idClienteObj);

                MongoDatabase db = MongoConnection.conectar();
                MongoCollection<Document> coleccionClientes = db.getCollection("Clientes");
                MongoCollection<Document> coleccionComerciales = db.getCollection("Comerciales");

                Document clienteDoc = null;

                try {
                    clienteDoc = coleccionClientes.find(new Document("_id", new ObjectId(idCliente))).first();
                } catch (Exception e) {
                    clienteDoc = coleccionClientes.find(new Document("_id", idCliente)).first();
                }

                if (clienteDoc == null) {
                    return new SimpleStringProperty("");
                }

                Object idComercialAsignadoObj = clienteDoc.get("idComercialAsignado");
                if (idComercialAsignadoObj == null) {
                    return new SimpleStringProperty("");
                }

                Document comercialDoc = null;

                if (idComercialAsignadoObj instanceof ObjectId) {
                    comercialDoc = coleccionComerciales
                            .find(new Document("_id", (ObjectId) idComercialAsignadoObj))
                            .first();
                } else {
                    String idComercialAsignado = String.valueOf(idComercialAsignadoObj);
                    try {
                        comercialDoc = coleccionComerciales
                                .find(new Document("_id", new ObjectId(idComercialAsignado)))
                                .first();
                    } catch (Exception e) {
                        comercialDoc = null;
                    }
                }

                if (comercialDoc != null) {
                    return new SimpleStringProperty(valorComoTexto(comercialDoc.get("nombre")));
                }

                return new SimpleStringProperty("");

            } catch (Exception e) {
                return new SimpleStringProperty("");
            }
        });

        colFechaCreacion.setCellValueFactory(data
                -> new SimpleStringProperty(valorComoTexto(data.getValue().get("fechaCreacion"))));

        colEstado.setCellValueFactory(data
                -> new SimpleStringProperty(valorComoTexto(data.getValue().get("estado"))));

        colSubtotal.setCellValueFactory(data
                -> new SimpleStringProperty(valorComoTexto(data.getValue().get("subtotal"))));

        colIva.setCellValueFactory(data
                -> new SimpleStringProperty(valorComoTexto(data.getValue().get("iva"))));

        colTotal.setCellValueFactory(data
                -> new SimpleStringProperty(valorComoTexto(data.getValue().get("total"))));
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
            AlertasSolarManager.errorGenerico("No se pudieron cargar los presupuestos.");
        }
    }

    /**
     * Recarga los datos de la tabla de presupuestos.
     */
    private void recargarTabla() {
        cargarPresupuestos();
    }

    /**
     * Filtra los presupuestos según el texto introducido.
     */
    @FXML
    private void buscarPresupuestos() {
        String filtro = txtFiltro.getText() != null ? txtFiltro.getText().toLowerCase().trim() : "";

        if (filtro.isEmpty()) {
            tablaPresupuestos.setItems(listaPresupuestos);
            return;
        }

        ObservableList<Document> filtrados = FXCollections.observableArrayList();

        for (Document doc : listaPresupuestos) {
            String id = valorComoTexto(doc.get("_id")).toLowerCase();
            String idCliente = valorComoTexto(doc.get("idCliente")).toLowerCase();
            String idComercial = valorComoTexto(doc.get("idComercial")).toLowerCase();
            String fecha = valorComoTexto(doc.get("fechaCreacion")).toLowerCase();
            String estado = valorComoTexto(doc.get("estado")).toLowerCase();

            Document cliente = doc.get("cliente", Document.class);
            String nombreCliente = "";
            if (cliente != null) {
                nombreCliente = (valorComoTexto(cliente.get("nombre")) + " "
                        + valorComoTexto(cliente.get("apellidos"))).trim().toLowerCase();
            }

            if (id.contains(filtro)
                    || idCliente.contains(filtro)
                    || idComercial.contains(filtro)
                    || fecha.contains(filtro)
                    || estado.contains(filtro)
                    || nombreCliente.contains(filtro)) {
                filtrados.add(doc);
            }
        }

        tablaPresupuestos.setItems(filtrados);
    }

    /**
     * Abre la ventana modal de detalle del presupuesto seleccionado.
     *
     * @param event evento del botón
     */
    @FXML
    private void irDetallePresupuesto(ActionEvent event) {
        Document presupuestoSeleccionado = tablaPresupuestos.getSelectionModel().getSelectedItem();

        if (presupuestoSeleccionado == null) {
            AlertasSolarManager.warning(
                    "Presupuesto no seleccionado",
                    "Debe seleccionar un presupuesto de la tabla para ver el detalle."
            );
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/components/pantallas/pantallaDetallePresupuesto/pantallaDetallePresupuesto.fxml")
            );

            Parent root = loader.load();

            components.pantallas.pantallaDetallePresupuesto.pantallaDetallePresupuestoController controller
                    = loader.getController();

            controller.cargarPresupuesto(presupuestoSeleccionado);

            Stage stageModal = new Stage();
            stageModal.initModality(Modality.WINDOW_MODAL);
            stageModal.initOwner(((Node) event.getSource()).getScene().getWindow());
            stageModal.setTitle("Detalle Presupuesto");
            stageModal.setResizable(false);
            stageModal.setScene(new Scene(root));
            stageModal.showAndWait();

            recargarTabla();

        } catch (IOException e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo abrir la ventana de detalle del presupuesto.");
        }
    }

    /**
     * Genera una factura a partir del presupuesto seleccionado, crea su PDF en
     * memoria, la guarda en la colección Facturas de MongoDB y actualiza el
     * estado del presupuesto a FACTURADO.
     *
     * @param event evento del botón
     */
    @FXML
    private void hacerFactura(ActionEvent event) {
        Document presupuestoSeleccionado = tablaPresupuestos.getSelectionModel().getSelectedItem();

        if (presupuestoSeleccionado == null) {
            AlertasSolarManager.warning(
                    "Presupuesto no seleccionado",
                    "Debe seleccionar un presupuesto de la tabla para generar la factura."
            );
            return;
        }

        try {
            Presupuesto presupuesto = convertirDocumentoAPresupuesto(presupuestoSeleccionado);
            Cliente cliente = obtenerClienteDesdePresupuesto(presupuestoSeleccionado);
            String numeroFactura = generarNumeroFactura();

            FacturaService facturaService = new FacturaService();
            Factura factura = facturaService.generarYGuardarFactura(presupuesto, cliente, numeroFactura);

            if (factura != null && factura.getId() != null && !factura.getId().trim().isEmpty()) {
                marcarPresupuestoComoFacturado(presupuestoSeleccionado);
                recargarTabla();

                AlertasSolarManager.info(
                        "Factura generada correctamente",
                        "La factura " + factura.getNumeroFactura() + " se ha guardado en MongoDB."
                );
            } else {
                AlertasSolarManager.warning(
                        "Factura generada",
                        "La factura se generó, pero no se pudo confirmar el identificador devuelto."
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo generar la factura.");
        }
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

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    stage.setMaximized(true);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            AlertasSolarManager.errorCambioPantalla();
        }
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
     * Recarga la pantalla de presupuestos.
     *
     * @param e evento de acción
     */
    @FXML
    private void irPresupuestos(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }

    /**
     * Navega a la pantalla de instalaciones.
     *
     * @param e evento de acción
     */
    @FXML
    private void irInstalaciones(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml");
    }

    /**
     * Navega a la pantalla de informes.
     *
     * @param e evento de acción
     */
    @FXML
    private void irInformes(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }

    /**
     * Elimina el presupuesto seleccionado de la colección Presupuestos de
     * MongoDB.
     *
     * @param event evento del botón
     */
    @FXML
    private void eliminarPresupuesto(ActionEvent event) {
        Document presupuestoSeleccionado = tablaPresupuestos.getSelectionModel().getSelectedItem();

        if (presupuestoSeleccionado == null) {
            AlertasSolarManager.warning(
                    "Presupuesto no seleccionado",
                    "Debe seleccionar un presupuesto de la tabla para eliminar."
            );
            return;
        }

        boolean confirmar = AlertasSolarManager.confirmar(
                "Eliminar presupuesto",
                "¿Seguro que deseas eliminar el presupuesto seleccionado?",
                "Eliminar",
                "Cancelar"
        );

        if (!confirmar) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Presupuestos");

            coleccion.deleteOne(new Document("_id", presupuestoSeleccionado.getObjectId("_id")));

            recargarTabla();
            AlertasSolarManager.operacionCorrecta();

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo eliminar el presupuesto.");
        }
    }

    /**
     * Convierte el documento MongoDB del presupuesto seleccionado a un objeto
     * del modelo Presupuesto.
     *
     * @param doc documento del presupuesto
     * @return presupuesto convertido
     */
    private Presupuesto convertirDocumentoAPresupuesto(Document doc) {
        Presupuesto presupuesto = new Presupuesto();

        Object idObj = doc.get("_id");
        if (idObj instanceof ObjectId) {
            presupuesto.setId(((ObjectId) idObj).toHexString());
        } else {
            presupuesto.setId(valorComoTexto(idObj));
        }

        presupuesto.setIdCliente(valorComoTexto(doc.get("idCliente")));
        presupuesto.setIdComercial(valorComoTexto(doc.get("idComercial")));
        presupuesto.setFechaCreacion(convertirALocalDate(doc.get("fechaCreacion")));
        presupuesto.setEstado(convertirEstadoPresupuesto(doc.get("estado")));
        presupuesto.setInstalacion(convertirInstalacion(doc.get("instalacion", Document.class)));
        presupuesto.setLineas(convertirLineas(doc.getList("lineas", Document.class)));
        presupuesto.setSubtotal(convertirADouble(doc.get("subtotal")));
        presupuesto.setIva(convertirADouble(doc.get("iva")));
        presupuesto.setTotal(convertirADouble(doc.get("total")));

        return presupuesto;
    }

    /**
     * Recupera el cliente asociado al presupuesto seleccionado desde la
     * colección Clientes de MongoDB.
     *
     * @param presupuestoDoc documento del presupuesto
     * @return cliente asociado
     */
    private Cliente obtenerClienteDesdePresupuesto(Document presupuestoDoc) throws IOException {
        Object idClienteObj = presupuestoDoc.get("idCliente");

        if (idClienteObj == null) {
            throw new IllegalArgumentException("El presupuesto no tiene idCliente.");
        }

        String idCliente = String.valueOf(idClienteObj);

        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<Document> coleccionClientes = db.getCollection("Clientes");

        Document clienteDoc;

        try {
            clienteDoc = coleccionClientes.find(new Document("_id", new ObjectId(idCliente))).first();
        } catch (Exception e) {
            clienteDoc = coleccionClientes.find(new Document("_id", idCliente)).first();
        }

        if (clienteDoc == null) {
            throw new IllegalArgumentException("No se encontró el cliente asociado al presupuesto.");
        }

        return convertirDocumentoACliente(clienteDoc);
    }

    /**
     * Convierte un documento MongoDB de cliente a un objeto del modelo Cliente.
     *
     * @param doc documento del cliente
     * @return cliente convertido
     */
    private Cliente convertirDocumentoACliente(Document doc) {
        Cliente cliente = new Cliente();

        Object idObj = doc.get("_id");
        if (idObj instanceof ObjectId) {
            cliente.setId(((ObjectId) idObj).toHexString());
        } else {
            cliente.setId(valorComoTexto(idObj));
        }

        cliente.setTipoCliente(convertirTipoCliente(doc.get("tipoCliente")));
        cliente.setDni(CifradoDatos.descifrarSiEsPosible(valorComoTexto(doc.get("dni"))));
        cliente.setTelefono(CifradoDatos.descifrarSiEsPosible(valorComoTexto(doc.get("telefono"))));
        cliente.setCif(valorComoTexto(doc.get("cif")));
        cliente.setNumeroCuenta(valorComoTexto(doc.get("numeroCuenta")));
        cliente.setObservaciones(valorComoTexto(doc.get("observaciones")));
        cliente.setIdComercialAsignado(valorComoTexto(doc.get("idComercialAsignado")));
        cliente.setNombre(valorComoTexto(doc.get("nombre")));
        cliente.setApellidos(valorComoTexto(doc.get("apellidos")));
        cliente.setEmail(valorComoTexto(doc.get("email")));
        cliente.setDireccion(convertirDireccion(doc.get("direccion", Document.class)));

        return cliente;
    }

    /**
     * Genera el número de factura con el formato AAAAMMNNNN, reiniciando la
     * secuencia cada mes.
     *
     * @return número de factura generado
     */
    private String generarNumeroFactura() throws IOException {
        YearMonth yearMonth = YearMonth.now();
        String prefijo = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));

        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<Document> coleccionFacturas = db.getCollection("Facturas");

        long cantidadMes = coleccionFacturas.countDocuments(
                new Document("numeroFactura", new Document("$regex", "^" + prefijo))
        );

        long siguiente = cantidadMes + 1L;

        return prefijo + String.format("%04d", siguiente);
    }

    /**
     * Actualiza el estado del presupuesto indicado en MongoDB a FACTURADO.
     *
     * @param presupuestoDoc documento del presupuesto a actualizar
     */
    private void marcarPresupuestoComoFacturado(Document presupuestoDoc) throws IOException {
        Object idObj = presupuestoDoc.get("_id");

        if (idObj == null) {
            throw new IllegalArgumentException("El presupuesto seleccionado no tiene identificador.");
        }

        MongoDatabase db = MongoConnection.conectar();
        MongoCollection<Document> coleccion = db.getCollection("Presupuestos");

        if (idObj instanceof ObjectId) {
            coleccion.updateOne(
                    new Document("_id", (ObjectId) idObj),
                    new Document("$set", new Document("estado", "FACTURADO"))
            );
        } else {
            coleccion.updateOne(
                    new Document("_id", idObj),
                    new Document("$set", new Document("estado", "FACTURADO"))
            );
        }
    }

    /**
     * Convierte el valor de estado almacenado en MongoDB al enumerado del
     * modelo.
     *
     * @param valor valor de estado
     * @return estado del presupuesto
     */
    private Presupuesto.EstadoPresupuesto convertirEstadoPresupuesto(Object valor) {
        String texto = valorComoTexto(valor).trim().toUpperCase();

        if (texto.isEmpty()) {
            return null;
        }

        try {
            return Presupuesto.EstadoPresupuesto.valueOf(texto);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convierte el valor del tipo de cliente almacenado en MongoDB al enumerado
     * del modelo.
     *
     * @param valor valor del tipo de cliente
     * @return tipo de cliente
     */
    private Cliente.TipoCliente convertirTipoCliente(Object valor) {
        String texto = valorComoTexto(valor).trim().toUpperCase();

        if (texto.isEmpty()) {
            return null;
        }

        try {
            return Cliente.TipoCliente.valueOf(texto);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convierte un documento MongoDB a un objeto InstalacionFotovoltaica.
     *
     * @param doc documento de instalación
     * @return instalación convertida
     */
    private InstalacionFotovoltaica convertirInstalacion(Document doc) {
        if (doc == null) {
            return null;
        }

        InstalacionFotovoltaica instalacion = new InstalacionFotovoltaica();
        instalacion.setId(valorComoTexto(doc.get("id")));
        instalacion.setIdCliente(valorComoTexto(doc.get("idCliente")));
        instalacion.setNombreCliente(valorComoTexto(doc.get("nombreCliente")));
        instalacion.setPotenciaInstalada(convertirADouble(doc.get("potenciaInstalada")));
        instalacion.setNumeroPaneles(convertirAInt(doc.get("numeroPaneles")));
        instalacion.setProduccionEstimada(convertirADouble(doc.get("produccionEstimada")));
        instalacion.setAhorroEstimado(convertirADouble(doc.get("ahorroEstimado")));
        instalacion.setInversor(valorComoTexto(doc.get("inversor")));
        instalacion.setBateria(convertirABoolean(doc.get("bateria")));
        instalacion.setDireccion(convertirDireccion(doc.get("direccion", Document.class)));

        return instalacion;
    }

    /**
     * Convierte la lista de documentos de líneas en una lista del modelo
     * LineaPresupuesto.
     *
     * @param listaDocumentos lista de documentos de líneas
     * @return lista de líneas convertidas
     */
    private List<LineaPresupuesto> convertirLineas(List<Document> listaDocumentos) {
        List<LineaPresupuesto> lineas = new ArrayList<LineaPresupuesto>();

        if (listaDocumentos == null) {
            return lineas;
        }

        for (Document lineaDoc : listaDocumentos) {
            LineaPresupuesto linea = new LineaPresupuesto();
            linea.setIdProducto(valorComoTexto(lineaDoc.get("idProducto")));
            linea.setNombreProducto(valorComoTexto(lineaDoc.get("nombreProducto")));
            linea.setCantidad(convertirAInt(lineaDoc.get("cantidad")));
            linea.setPrecioUnitario(convertirADouble(lineaDoc.get("precioUnitario")));
            linea.setTotalLinea(convertirADouble(lineaDoc.get("totalLinea")));
            lineas.add(linea);
        }

        return lineas;
    }

    /**
     * Convierte un documento MongoDB de dirección a un objeto del modelo
     * Direccion.
     *
     * @param doc documento de dirección
     * @return dirección convertida
     */
    private Direccion convertirDireccion(Document doc) {
        if (doc == null) {
            return null;
        }

        Direccion direccion = new Direccion();
        direccion.setCalle(valorComoTexto(doc.get("calle")));
        direccion.setNumero(valorComoTexto(doc.get("numero")));
        direccion.setCodigoPostal(valorComoTexto(doc.get("codigoPostal")));
        direccion.setMunicipio(valorComoTexto(doc.get("municipio")));
        direccion.setProvincia(valorComoTexto(doc.get("provincia")));

        return direccion;
    }

    /**
     * Convierte un valor a LocalDate.
     *
     * @param valor valor de entrada
     * @return fecha convertida
     */
    private LocalDate convertirALocalDate(Object valor) {
        String texto = valorComoTexto(valor).trim();

        if (texto.isEmpty()) {
            return null;
        }

        try {
            if (texto.length() >= 10) {
                return LocalDate.parse(texto.substring(0, 10));
            }
            return LocalDate.parse(texto);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convierte un valor a double.
     *
     * @param valor valor de entrada
     * @return número convertido
     */
    private double convertirADouble(Object valor) {
        if (valor == null) {
            return 0.0;
        }

        if (valor instanceof Number) {
            return ((Number) valor).doubleValue();
        }

        try {
            return Double.parseDouble(String.valueOf(valor).replace(",", ".").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Convierte un valor a entero.
     *
     * @param valor valor de entrada
     * @return entero convertido
     */
    private int convertirAInt(Object valor) {
        if (valor == null) {
            return 0;
        }

        if (valor instanceof Number) {
            return ((Number) valor).intValue();
        }

        try {
            return Integer.parseInt(String.valueOf(valor).trim());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Convierte un valor a booleano.
     *
     * @param valor valor de entrada
     * @return booleano convertido
     */
    private boolean convertirABoolean(Object valor) {
        if (valor == null) {
            return false;
        }

        if (valor instanceof Boolean) {
            return (Boolean) valor;
        }

        return "true".equalsIgnoreCase(String.valueOf(valor).trim())
                || "si".equalsIgnoreCase(String.valueOf(valor).trim())
                || "sí".equalsIgnoreCase(String.valueOf(valor).trim());
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

    /**
     * Visualiza una factura creada
     *
     * @param event
     */
    @FXML
    private void verFactura(ActionEvent event) {

        Document presupuestoSeleccionado = tablaPresupuestos.getSelectionModel().getSelectedItem();

        if (presupuestoSeleccionado == null) {
            AlertasSolarManager.warning(
                    "Presupuesto no seleccionado",
                    "Debe seleccionar un presupuesto."
            );
            return;
        }

        try {
            String idPresupuesto = String.valueOf(presupuestoSeleccionado.get("_id"));

            FacturaDAO facturaDAO = new FacturaDAO();
            Factura factura = facturaDAO.obtenerFacturaPorPresupuesto(idPresupuesto);

            if (factura == null || factura.getPdfFactura() == null) {
                AlertasSolarManager.warning(
                        "Factura no encontrada",
                        "Este presupuesto no tiene factura generada."
                );
                return;
            }

            PDFUtils.abrirPDF(factura.getPdfFactura());

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo abrir la factura.");
        }
    }
}
