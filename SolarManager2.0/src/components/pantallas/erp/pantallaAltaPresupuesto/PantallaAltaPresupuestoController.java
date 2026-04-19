package components.pantallas.erp.pantallaAltaPresupuesto;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Direccion;
import modelo.InstalacionFotovoltaica;
import modelo.Presupuesto;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.AlertasSolarManager;
import utils.CifradoDatos;

/**
 * Controlador de la ventana modal de alta de presupuesto.
 *
 * <p>
 * Se encarga de cargar los datos del cliente y de la instalación,
 * calcular los importes económicos de cada partida y guardar el
 * presupuesto en MongoDB.
 * </p>
 *
 * @author Iván
 */
public class PantallaAltaPresupuestoController implements Initializable {

    @FXML private TextField txtNombreApellidos;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtTipoCliente;
    @FXML private TextField txtDireccionCompleta;

    @FXML private TextField txtPanelesSolares;
    @FXML private TextField txtEstructura;
    @FXML private TextField txtInversor;
    @FXML private CheckBox chkBateria;
    @FXML private TextField txtMaterialElectrico;
    @FXML private TextField txtManoObra;
    @FXML private TextField txtTramitacion;

    @FXML private TextField txtSubtotal;
    @FXML private TextField txtImpuesto;
    @FXML private TextField txtTotal;

    @FXML private ComboBox<Presupuesto.EstadoPresupuesto> comboEstado;

    private InstalacionFotovoltaica instalacionSeleccionada;
    private Document clienteDoc;

    private final List<Document> lineasPresupuesto = new ArrayList<Document>();

    private double subtotalCalculado;
    private double impuestoCalculado;
    private double totalCalculado;

    private static final double IVA_PORCENTAJE = 0.21;
    private static final double COSTE_MATERIAL_ELECTRICO_POR_PANEL = 50.0;
    private static final double COSTE_TRAMITACION = 500.0;

    private final DecimalFormat formatoImporte;

    /**
     * Constructor que inicializa el formato de importes monetarios.
     */
    public PantallaAltaPresupuestoController() {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("es", "ES"));
        simbolos.setDecimalSeparator(',');
        simbolos.setGroupingSeparator('.');
        formatoImporte = new DecimalFormat("#,##0.00", simbolos);
    }

    /**
     * Inicializa la ventana cargando los posibles estados del presupuesto.
     *
     * @param url ubicación utilizada para resolver rutas relativas
     * @param rb recursos internacionalizados
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboEstado.getItems().setAll(Presupuesto.EstadoPresupuesto.values());
        chkBateria.setDisable(true);
        chkBateria.setFocusTraversable(false);
    }

    /**
     * Recibe la instalación seleccionada desde la pantalla de instalaciones
     * y carga todos los datos necesarios del presupuesto.
     *
     * @param instalacion instalación seleccionada
     */
    public void cargarDatosInstalacion(InstalacionFotovoltaica instalacion) {
        this.instalacionSeleccionada = instalacion;
        cargarClienteDesdeMongo();
        cargarPanelCliente();
        calcularYCargarLineas();
    }

    /**
     * Recupera el cliente asociado a la instalación desde MongoDB.
     */
    private void cargarClienteDesdeMongo() {
        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccionClientes = db.getCollection("Clientes");

            ObjectId objectIdCliente = convertirAObjectId(instalacionSeleccionada.getIdCliente());

            if (objectIdCliente != null) {
                clienteDoc = coleccionClientes.find(new Document("_id", objectIdCliente)).first();
            } else {
                clienteDoc = coleccionClientes.find(new Document("id", instalacionSeleccionada.getIdCliente())).first();
            }

            if (clienteDoc != null) {
                String telefonoCifrado = clienteDoc.getString("telefono");
                if (telefonoCifrado != null) {
                    CifradoDatos.descifrarSiEsPosible(telefonoCifrado);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudieron cargar los datos del cliente.");
        }
    }

    /**
     * Carga en pantalla los datos del cliente.
     */
    private void cargarPanelCliente() {
        if (clienteDoc == null) {
            txtNombreApellidos.setText("");
            txtDocumento.setText("");
            txtTipoCliente.setText("");
            txtDireccionCompleta.setText("");
            return;
        }

        String nombre = valorTexto(clienteDoc.getString("nombre"));
        String apellidos = valorTexto(clienteDoc.getString("apellidos"));
        String dni = valorTexto(CifradoDatos.descifrarSiEsPosible(clienteDoc.getString("dni")));
        String cif = valorTexto(clienteDoc.getString("cif"));
        String tipoCliente = valorTexto(clienteDoc.getString("tipoCliente"));

        txtNombreApellidos.setText((nombre + " " + apellidos).trim());
        txtDocumento.setText(!dni.isEmpty() ? dni : cif);
        txtTipoCliente.setText(tipoCliente);

        Document direccion = clienteDoc.get("direccion", Document.class);
        if (direccion != null) {
            txtDireccionCompleta.setText(formatearDireccionDesdeDocumento(direccion));
        } else {
            txtDireccionCompleta.setText("");
        }
    }

    /**
     * Calcula las líneas del presupuesto y los importes totales.
     */
    private void calcularYCargarLineas() {
        lineasPresupuesto.clear();
        subtotalCalculado = 0.0;
        impuestoCalculado = 0.0;
        totalCalculado = 0.0;

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccionProductos = db.getCollection("Productos");

            int numeroPaneles = instalacionSeleccionada.getNumeroPaneles();

            Document productoPanel = obtenerUltimoProductoPorTipo(coleccionProductos, "PANEL_SOLAR");
            Document productoEstructura = obtenerUltimoProductoPorTipo(coleccionProductos, "ESTRUCTURA");
            Document productoInversor = obtenerUltimoProductoPorTipo(coleccionProductos, "INVERSOR");
            Document productoBateria = obtenerUltimoProductoPorTipo(coleccionProductos, "BATERIA");

            if (productoPanel != null && numeroPaneles > 0) {
                String nombre = valorTexto(productoPanel.getString("nombre"));
                double precioUnitario = obtenerDouble(productoPanel, "precio");
                double totalLinea = precioUnitario * numeroPaneles;

                txtPanelesSolares.setText(nombre + " x" + numeroPaneles + " - " + formatearImporte(totalLinea));
                anadirLineaProducto(productoPanel, nombre, numeroPaneles, precioUnitario, totalLinea);
                subtotalCalculado += totalLinea;
            } else {
                txtPanelesSolares.setText("");
            }

            if (productoEstructura != null && numeroPaneles > 0) {
                String nombre = valorTexto(productoEstructura.getString("nombre"));
                double precioUnitario = obtenerDouble(productoEstructura, "precio");
                double totalLinea = precioUnitario * numeroPaneles;

                txtEstructura.setText(nombre + " x" + numeroPaneles + " - " + formatearImporte(totalLinea));
                anadirLineaProducto(productoEstructura, nombre, numeroPaneles, precioUnitario, totalLinea);
                subtotalCalculado += totalLinea;
            } else {
                txtEstructura.setText("");
            }

            if (instalacionSeleccionada.getInversor() != null
                    && !instalacionSeleccionada.getInversor().trim().isEmpty()
                    && productoInversor != null) {

                String nombre = valorTexto(instalacionSeleccionada.getInversor());
                double precioUnitario = obtenerDouble(productoInversor, "precio");
                double totalLinea = precioUnitario;

                txtInversor.setText(nombre + " x1 - " + formatearImporte(totalLinea));
                anadirLineaProducto(productoInversor, nombre, 1, precioUnitario, totalLinea);
                subtotalCalculado += totalLinea;
            } else {
                txtInversor.setText(valorTexto(instalacionSeleccionada.getInversor()));
            }

            chkBateria.setSelected(instalacionSeleccionada.getBateria());

            if (instalacionSeleccionada.getBateria() && productoBateria != null) {
                String nombre = valorTexto(productoBateria.getString("nombre"));
                double precioUnitario = obtenerDouble(productoBateria, "precio");
                double totalLinea = precioUnitario;

                anadirLineaProducto(productoBateria, nombre, 1, precioUnitario, totalLinea);
                subtotalCalculado += totalLinea;
            }

            double totalMaterialElectrico = numeroPaneles * COSTE_MATERIAL_ELECTRICO_POR_PANEL;
            txtMaterialElectrico.setText("Material eléctrico x" + numeroPaneles + " - " + formatearImporte(totalMaterialElectrico));
            anadirLineaManual("MATERIAL_ELECTRICO", "Material eléctrico", numeroPaneles,
                    COSTE_MATERIAL_ELECTRICO_POR_PANEL, totalMaterialElectrico);
            subtotalCalculado += totalMaterialElectrico;

            double costeManoObra = calcularCosteManoObra(numeroPaneles);
            txtManoObra.setText("Mano de obra x1 - " + formatearImporte(costeManoObra));
            anadirLineaManual("MANO_OBRA", "Mano de obra", 1, costeManoObra, costeManoObra);
            subtotalCalculado += costeManoObra;

            txtTramitacion.setText("Tramitación de subvenciones y gestiones administrativas x1 - " + formatearImporte(COSTE_TRAMITACION));
            anadirLineaManual("TRAMITACION", "Tramitación de subvenciones y gestiones administrativas", 1,
                    COSTE_TRAMITACION, COSTE_TRAMITACION);
            subtotalCalculado += COSTE_TRAMITACION;

            impuestoCalculado = subtotalCalculado * IVA_PORCENTAJE;
            totalCalculado = subtotalCalculado + impuestoCalculado;

            txtSubtotal.setText(formatearImporte(subtotalCalculado));
            txtImpuesto.setText(formatearImporte(impuestoCalculado));
            txtTotal.setText(formatearImporte(totalCalculado));

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudieron calcular los importes del presupuesto.");
        }
    }

    /**
     * Guarda el presupuesto en la colección de MongoDB.
     */
    @FXML
    private void guardarPresupuesto() {
        if (comboEstado.getValue() == null) {
            AlertasSolarManager.info(null, "Debe elegir un estado para el presupuesto");
            return;
        }

        boolean confirmar = AlertasSolarManager.confirmar(
                "Confirmar guardado",
                "¿Desea guardar el presupuesto?",
                "Sí",
                "No"
        );

        if (!confirmar) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccionPresupuestos = db.getCollection("Presupuestos");

            Document instalacionDoc = construirDocumentoInstalacion();
            Document clienteDireccionDoc = obtenerDireccionClienteDocumento();

            Document presupuestoDoc = new Document();
            presupuestoDoc.append("idCliente", instalacionSeleccionada.getIdCliente());
            presupuestoDoc.append("idComercial", clienteDoc != null ? valorTexto(clienteDoc.getString("idComercialAsignado")) : "");
            presupuestoDoc.append("fechaCreacion", LocalDate.now().toString());
            presupuestoDoc.append("estado", comboEstado.getValue().name());
            presupuestoDoc.append("instalacion", instalacionDoc);
            presupuestoDoc.append("lineas", lineasPresupuesto);
            presupuestoDoc.append("subtotal", subtotalCalculado);
            presupuestoDoc.append("iva", impuestoCalculado);
            presupuestoDoc.append("total", totalCalculado);

            if (clienteDoc != null) {
                presupuestoDoc.append("cliente", new Document()
                        .append("nombre", valorTexto(clienteDoc.getString("nombre")))
                        .append("apellidos", valorTexto(clienteDoc.getString("apellidos")))
                        .append("tipoCliente", valorTexto(clienteDoc.getString("tipoCliente")))
                        .append("dni", valorTexto(clienteDoc.getString("dni")))
                        .append("cif", valorTexto(clienteDoc.getString("cif")))
                        .append("direccion", clienteDireccionDoc)
                );
            }

            coleccionPresupuestos.insertOne(presupuestoDoc);

            AlertasSolarManager.presupuestoCreadoCorrectamente();
            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo guardar el presupuesto en la base de datos.");
        }
    }

    /**
     * Cancela la operación actual solicitando confirmación previa.
     */
    @FXML
    private void cancelar() {
        boolean confirmar = AlertasSolarManager.confirmar(
                "Salir sin guardar",
                "¿Desea salir sin guardar?",
                "Sí",
                "No"
        );

        if (confirmar) {
            cerrarVentana();
        }
    }

    /**
     * Construye el documento embebido de instalación que se guardará en MongoDB.
     *
     * @return documento con los datos de la instalación
     */
    private Document construirDocumentoInstalacion() {
        Document direccionInstalacion = new Document();

        Direccion direccion = instalacionSeleccionada.getDireccion();
        if (direccion != null) {
            direccionInstalacion.append("calle", direccion.getCalle());
            direccionInstalacion.append("numero", direccion.getNumero());
            direccionInstalacion.append("codigoPostal", direccion.getCodigoPostal());
            direccionInstalacion.append("municipio", direccion.getMunicipio());
            direccionInstalacion.append("provincia", direccion.getProvincia());
        }

        return new Document()
                .append("idInstalacion", instalacionSeleccionada.getId())
                .append("idCliente", instalacionSeleccionada.getIdCliente())
                .append("potenciaInstalada", instalacionSeleccionada.getPotenciaInstalada())
                .append("numeroPaneles", instalacionSeleccionada.getNumeroPaneles())
                .append("produccionEstimada", instalacionSeleccionada.getProduccionEstimada())
                .append("ahorroEstimado", instalacionSeleccionada.getAhorroEstimado())
                .append("inversor", instalacionSeleccionada.getInversor())
                .append("bateria", instalacionSeleccionada.getBateria())
                .append("direccion", direccionInstalacion);
    }

    /**
     * Obtiene la dirección del cliente en formato documento MongoDB.
     *
     * @return documento con la dirección del cliente
     */
    private Document obtenerDireccionClienteDocumento() {
        Document direccion = clienteDoc != null ? clienteDoc.get("direccion", Document.class) : null;

        if (direccion == null) {
            return new Document();
        }

        return new Document()
                .append("calle", direccion.getString("calle"))
                .append("numero", direccion.getString("numero"))
                .append("codigoPostal", direccion.getString("codigoPostal"))
                .append("municipio", direccion.getString("municipio"))
                .append("provincia", direccion.getString("provincia"));
    }

    /**
     * Añade una línea de presupuesto asociada a un producto real de MongoDB.
     *
     * @param producto documento del producto
     * @param nombreProducto nombre del producto
     * @param cantidad cantidad de unidades
     * @param precioUnitario precio por unidad
     * @param totalLinea total de la línea
     */
    private void anadirLineaProducto(Document producto, String nombreProducto, int cantidad,
                                     double precioUnitario, double totalLinea) {

        String idProducto = "";
        if (producto.getObjectId("_id") != null) {
            idProducto = producto.getObjectId("_id").toHexString();
        }

        lineasPresupuesto.add(new Document()
                .append("idProducto", idProducto)
                .append("nombreProducto", nombreProducto)
                .append("cantidad", cantidad)
                .append("precioUnitario", precioUnitario)
                .append("totalLinea", totalLinea));
    }

    /**
     * Añade una línea manual del presupuesto no asociada a un producto persistido.
     *
     * @param idProducto identificador simbólico de la línea
     * @param nombreProducto descripción de la línea
     * @param cantidad cantidad
     * @param precioUnitario precio unitario
     * @param totalLinea total de la línea
     */
    private void anadirLineaManual(String idProducto, String nombreProducto, int cantidad,
                                   double precioUnitario, double totalLinea) {

        lineasPresupuesto.add(new Document()
                .append("idProducto", idProducto)
                .append("nombreProducto", nombreProducto)
                .append("cantidad", cantidad)
                .append("precioUnitario", precioUnitario)
                .append("totalLinea", totalLinea));
    }

    /**
     * Obtiene el último producto creado de un tipo determinado.
     *
     * @param coleccionProductos colección de productos
     * @param tipoProducto tipo de producto a buscar
     * @return último documento creado para ese tipo o null si no existe
     */
    private Document obtenerUltimoProductoPorTipo(MongoCollection<Document> coleccionProductos, String tipoProducto) {
        return coleccionProductos.find(new Document("tipoProducto", tipoProducto))
                .sort(Sorts.descending("_id"))
                .first();
    }

    /**
     * Calcula el coste de mano de obra según el número de paneles.
     *
     * @param numeroPaneles número de paneles de la instalación
     * @return coste de mano de obra
     */
    private double calcularCosteManoObra(int numeroPaneles) {
        if (numeroPaneles <= 12) {
            return 1200.0;
        } else if (numeroPaneles >= 13 && numeroPaneles <= 18) {
            return 1500.0;
        } else {
            return 2000.0;
        }
    }

    /**
     * Formatea un importe con dos decimales y símbolo de euro.
     *
     * @param valor importe numérico
     * @return texto formateado
     */
    private String formatearImporte(double valor) {
        return formatoImporte.format(valor) + " €";
    }

    /**
     * Convierte la dirección del cliente a una cadena legible.
     *
     * @param direccion documento de dirección
     * @return dirección completa en una sola línea
     */
    private String formatearDireccionDesdeDocumento(Document direccion) {
        String calle = valorTexto(direccion.getString("calle"));
        String numero = valorTexto(direccion.getString("numero"));
        String municipio = valorTexto(direccion.getString("municipio"));
        String codigoPostal = valorTexto(direccion.getString("codigoPostal"));
        String provincia = valorTexto(direccion.getString("provincia"));

        return (calle + " " + numero + ", " + municipio + ", " + codigoPostal + ", " + provincia).trim();
    }

    /**
     * Devuelve un texto no nulo.
     *
     * @param valor valor recibido
     * @return valor original o cadena vacía si era nulo
     */
    private String valorTexto(String valor) {
        return valor != null ? valor : "";
    }

    /**
     * Obtiene un valor numérico double de un documento Mongo.
     *
     * @param doc documento Mongo
     * @param campo nombre del campo
     * @return valor double o 0.0 si no existe
     */
    private double obtenerDouble(Document doc, String campo) {
        Number n = doc.get(campo, Number.class);
        return n != null ? n.doubleValue() : 0.0;
    }

    /**
     * Convierte un String a ObjectId cuando el formato es válido.
     *
     * @param id identificador en texto
     * @return ObjectId correspondiente o null si no es válido
     */
    private ObjectId convertirAObjectId(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return null;
            }

            String limpio = id.trim();

            if (limpio.startsWith("ObjectId('") && limpio.endsWith("')")) {
                limpio = limpio.substring(10, limpio.length() - 2);
            } else if (limpio.startsWith("ObjectId(\"") && limpio.endsWith("\")")) {
                limpio = limpio.substring(10, limpio.length() - 2);
            }

            if (ObjectId.isValid(limpio)) {
                return new ObjectId(limpio);
            }

            return null;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Cierra la ventana modal actual.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) txtTotal.getScene().getWindow();
        stage.close();
    }
}