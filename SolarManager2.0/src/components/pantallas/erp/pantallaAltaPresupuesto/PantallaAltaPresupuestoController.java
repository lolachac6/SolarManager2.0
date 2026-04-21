package components.pantallas.erp.pantallaAltaPresupuesto;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Direccion;
import modelo.InstalacionFotovoltaica;
import modelo.LineaPresupuesto;
import modelo.Presupuesto;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.AlertasSolarManager;
import utils.CifradoDatos;

/**
 * Controlador de la pantalla de alta de presupuesto.
 *
 * Se encarga de recibir la instalación seleccionada, recuperar los datos del
 * cliente asociado, calcular las líneas del presupuesto a partir de las
 * colecciones Productos y GastosInstalacion, calcular subtotal, IVA y total,
 * y guardar toda la información en la colección Presupuestos de MongoDB.
 *
 * @author Iván
 */
public class PantallaAltaPresupuestoController {

    @FXML private TextField txtNombreApellidos;
    @FXML private TextField txtTelefono;
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
    @FXML private ComboBox<String> comboEstado;

    private InstalacionFotovoltaica instalacionSeleccionada;
    private String idCliente;
    private List<LineaPresupuesto> lineasCalculadas;
    private double subtotalCalculado;
    private double ivaCalculado;
    private double totalCalculado;

    /**
     * Carga los datos de la instalación y del cliente asociado.
     *
     * @param instalacion instalación seleccionada
     */
    public void cargarDatosInstalacion(InstalacionFotovoltaica instalacion) {

        if (instalacion == null) {
            return;
        }

        this.instalacionSeleccionada = instalacion;
        this.idCliente = instalacion.getIdCliente();

        cargarDatosCliente();
        cargarDatosInstalacionBase();
        cargarEstados();
        calculoPresupuesto();
    }

    /**
     * Carga en pantalla los datos básicos de la instalación seleccionada.
     */
    private void cargarDatosInstalacionBase() {
        txtPanelesSolares.setText(String.valueOf(instalacionSeleccionada.getNumeroPaneles()));
        txtInversor.setText(instalacionSeleccionada.getInversor() != null ? instalacionSeleccionada.getInversor() : "");
        chkBateria.setSelected(instalacionSeleccionada.getBateria());
    }

    /**
     * Carga los posibles estados del presupuesto.
     */
    private void cargarEstados() {
        
            comboEstado.getItems().addAll(
                    Presupuesto.EstadoPresupuesto.BORRADOR.name(),
                    Presupuesto.EstadoPresupuesto.ENVIADO.name(),
                    Presupuesto.EstadoPresupuesto.ACEPTADO.name(),
                    Presupuesto.EstadoPresupuesto.RECHAZADO.name(),
                    Presupuesto.EstadoPresupuesto.FACTURADO.name()
            );
        

        if (comboEstado.getValue() == null) {
            comboEstado.setValue(Presupuesto.EstadoPresupuesto.BORRADOR.name());
        }
    }

    /**
     * Recupera y muestra los datos del cliente a partir de su identificador.
     */
    private void cargarDatosCliente() {
        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccionClientes = db.getCollection("Clientes");

            Document clienteDoc = buscarPorId(coleccionClientes, idCliente);

            if (clienteDoc == null) {
                limpiarCamposCliente();
                return;
            }

            String nombre = clienteDoc.getString("nombre");
            String apellidos = clienteDoc.getString("apellidos");
            txtNombreApellidos.setText(((nombre != null ? nombre : "") + " " + (apellidos != null ? apellidos : "")).trim());
            
            String telefonoCifrado = clienteDoc.getString("telefono");
            String telefono = CifradoDatos.descifrar(telefonoCifrado);
            txtTelefono.setText(telefono);

            String dniCifrado = clienteDoc.getString("dni");
            String dni = CifradoDatos.descifrarSiEsPosible(dniCifrado);

            String cif = clienteDoc.getString("cif");
            txtDocumento.setText(dni != null && !dni.trim().isEmpty() ? dni : (cif != null ? cif : ""));

            Object tipoCliente = clienteDoc.get("tipoCliente");
            txtTipoCliente.setText(tipoCliente != null ? String.valueOf(tipoCliente) : "");

            Document dir = clienteDoc.get("direccion", Document.class);
            if (dir != null) {
                Direccion direccion = new Direccion(
                        dir.getString("calle"),
                        dir.getString("numero"),
                        dir.getString("codigoPostal"),
                        dir.getString("municipio"),
                        dir.getString("provincia")
                );
                txtDireccionCompleta.setText(formatearDireccion(direccion));
            } else {
                txtDireccionCompleta.setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
            limpiarCamposCliente();
        }
    }

    /**
     * Calcula todas las líneas del presupuesto y actualiza los importes en
     * pantalla.
     */
    private void calculoPresupuesto() {
        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccionProductos = db.getCollection("Productos");
            MongoCollection<Document> coleccionGastos = db.getCollection("GastosInstalacion");

            lineasCalculadas = new ArrayList<>();

            int numeroPaneles = instalacionSeleccionada.getNumeroPaneles();
            double potenciaInstalada = instalacionSeleccionada.getPotenciaInstalada();

            Document productoPanel = buscarProductoPorTipo(coleccionProductos, "PANEL_SOLAR");
            Document productoEstructura = buscarProductoPorTipo(coleccionProductos, "ESTRUCTURA");
            Document productoInversor = buscarProductoPorTipo(coleccionProductos, "INVERSOR");
            Document productoBateria = buscarProductoPorTipo(coleccionProductos, "BATERIA");
            Document gastosDoc = coleccionGastos.find().first();

            double precioPanel = obtenerPrecio(productoPanel);
            double precioEstructura = obtenerPrecio(productoEstructura);
            double precioInversor = obtenerPrecio(productoInversor);
            double precioBateria = obtenerPrecio(productoBateria);

            int cantidadPaneles = numeroPaneles;
            int cantidadEstructura = numeroPaneles;
            int cantidadInversores = (int) Math.ceil(potenciaInstalada / 5.0);
            int cantidadBateria = instalacionSeleccionada.getBateria() ? 1 : 0;

            double precioMaterialElectrico = gastosDoc != null ? obtenerDouble(gastosDoc, "materialElectrico") : 0.0;
            double precioManoObra = gastosDoc != null ? obtenerDouble(gastosDoc, "manoObra") : 0.0;
            double precioTramitacion = gastosDoc != null ? obtenerDouble(gastosDoc, "tramitacion") : 0.0;

            int bloquesCadaSeisPaneles = (int) Math.ceil(numeroPaneles / 6.0);
            int cantidadMaterialElectrico = bloquesCadaSeisPaneles;
            int cantidadManoObra = bloquesCadaSeisPaneles;
            int cantidadTramitacion = 1;

            if (productoPanel != null && cantidadPaneles > 0) {
                lineasCalculadas.add(crearLineaProducto(productoPanel, cantidadPaneles, precioPanel));
            }

            if (productoEstructura != null && cantidadEstructura > 0) {
                lineasCalculadas.add(crearLineaProducto(productoEstructura, cantidadEstructura, precioEstructura));
            }

            if (productoInversor != null && cantidadInversores > 0) {
                lineasCalculadas.add(crearLineaProducto(productoInversor, cantidadInversores, precioInversor));
            }

            if (productoBateria != null && cantidadBateria > 0) {
                lineasCalculadas.add(crearLineaProducto(productoBateria, cantidadBateria, precioBateria));
            }

            if (cantidadMaterialElectrico > 0) {
                lineasCalculadas.add(crearLineaManual(
                        gastosDoc != null ? gastosDoc.getObjectId("_id").toHexString() : "",
                        "Material eléctrico",
                        cantidadMaterialElectrico,
                        precioMaterialElectrico
                ));
            }

            if (cantidadManoObra > 0) {
                lineasCalculadas.add(crearLineaManual(
                        gastosDoc != null ? gastosDoc.getObjectId("_id").toHexString() : "",
                        "Mano de obra",
                        cantidadManoObra,
                        precioManoObra
                ));
            }

            if (cantidadTramitacion > 0) {
                lineasCalculadas.add(crearLineaManual(
                        gastosDoc != null ? gastosDoc.getObjectId("_id").toHexString() : "",
                        "Tramitación",
                        cantidadTramitacion,
                        precioTramitacion
                ));
            }

            subtotalCalculado = calcularSubtotal(lineasCalculadas);
            ivaCalculado = subtotalCalculado * 0.21;
            totalCalculado = subtotalCalculado + ivaCalculado;

            txtEstructura.setText(formatearImporte(cantidadEstructura * precioEstructura));
            txtInversor.setText(formatearImporte(cantidadInversores * precioInversor));
            chkBateria.setSelected(cantidadBateria > 0);
            txtMaterialElectrico.setText(formatearImporte(cantidadMaterialElectrico * precioMaterialElectrico));
            txtManoObra.setText(formatearImporte(cantidadManoObra * precioManoObra));
            txtTramitacion.setText(formatearImporte(cantidadTramitacion * precioTramitacion));
            txtSubtotal.setText(formatearImporte(subtotalCalculado));
            txtImpuesto.setText(formatearImporte(ivaCalculado));
            txtTotal.setText(formatearImporte(totalCalculado));

        } catch (Exception e) {
            e.printStackTrace();
            txtEstructura.setText("");
            txtMaterialElectrico.setText("");
            txtManoObra.setText("");
            txtTramitacion.setText("");
            txtSubtotal.setText("");
            txtImpuesto.setText("");
            txtTotal.setText("");
        }
    }

    /**
     * Crea una línea de presupuesto a partir de un producto de la colección
     * Productos.
     *
     * @param producto documento del producto
     * @param cantidad cantidad de unidades
     * @param precio precio unitario
     * @return línea de presupuesto creada
     */
    private LineaPresupuesto crearLineaProducto(Document producto, int cantidad, double precio) {
        LineaPresupuesto linea = new LineaPresupuesto();
        linea.setIdProducto(producto.getObjectId("_id").toHexString());
        linea.setNombreProducto(producto.getString("nombre"));
        linea.setCantidad(cantidad);
        linea.setPrecioUnitario(precio);
        linea.setTotalLinea(cantidad * precio);
        return linea;
    }

    /**
     * Crea una línea de presupuesto manual para gastos de instalación.
     *
     * @param idProducto identificador asociado
     * @param nombre nombre visible de la línea
     * @param cantidad cantidad de unidades
     * @param precioUnitario precio unitario
     * @return línea de presupuesto creada
     */
    private LineaPresupuesto crearLineaManual(String idProducto, String nombre, int cantidad, double precioUnitario) {
        LineaPresupuesto linea = new LineaPresupuesto();
        linea.setIdProducto(idProducto);
        linea.setNombreProducto(nombre);
        linea.setCantidad(cantidad);
        linea.setPrecioUnitario(precioUnitario);
        linea.setTotalLinea(cantidad * precioUnitario);
        return linea;
    }

    /**
     * Calcula el subtotal del presupuesto a partir de sus líneas.
     *
     * @param lineas lista de líneas del presupuesto
     * @return subtotal calculado
     */
    private double calcularSubtotal(List<LineaPresupuesto> lineas) {
        double subtotal = 0.0;

        for (LineaPresupuesto linea : lineas) {
            subtotal += linea.getTotalLinea();
        }

        return subtotal;
    }

    /**
     * Busca un producto por su tipo en la colección Productos.
     *
     * @param coleccionProductos colección de productos
     * @param tipo tipo de producto
     * @return documento encontrado o null
     */
    private Document buscarProductoPorTipo(MongoCollection<Document> coleccionProductos, String tipo) {
        Document doc = coleccionProductos.find(new Document("tipoProducto", tipo)).first();
        if (doc == null) {
            doc = coleccionProductos.find(new Document("tipo", tipo)).first();
        }
        return doc;
    }

    /**
     * Obtiene el precio de un producto.
     *
     * @param doc documento del producto
     * @return precio del producto
     */
    private double obtenerPrecio(Document doc) {
        if (doc == null) {
            return 0.0;
        }

        Number precio = doc.get("precio", Number.class);
        return precio != null ? precio.doubleValue() : 0.0;
    }

    /**
     * Obtiene un campo numérico como double.
     *
     * @param doc documento de MongoDB
     * @param campo nombre del campo
     * @return valor del campo
     */
    private double obtenerDouble(Document doc, String campo) {
        if (doc == null) {
            return 0.0;
        }

        Number numero = doc.get(campo, Number.class);
        return numero != null ? numero.doubleValue() : 0.0;
    }

    /**
     * Busca un documento por su identificador en una colección.
     *
     * @param coleccion colección de MongoDB
     * @param id identificador del documento
     * @return documento encontrado o null
     */
    private Document buscarPorId(MongoCollection<Document> coleccion, String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        try {
            return coleccion.find(new Document("_id", new ObjectId(id))).first();
        } catch (Exception e) {
            return coleccion.find(new Document("_id", id)).first();
        }
    }

    /**
     * Formatea una dirección en una única línea de texto.
     *
     * @param d dirección a formatear
     * @return dirección completa
     */
    private String formatearDireccion(Direccion d) {
        if (d == null) {
            return "";
        }

        String calle = d.getCalle() != null ? d.getCalle() : "";
        String numero = d.getNumero() != null ? d.getNumero() : "";
        String cp = d.getCodigoPostal() != null ? d.getCodigoPostal() : "";
        String municipio = d.getMunicipio() != null ? d.getMunicipio() : "";
        String provincia = d.getProvincia() != null ? d.getProvincia() : "";

        return (calle + " " + numero + ", " + cp + " " + municipio + ", " + provincia).trim();
    }

    /**
     * Limpia los campos visuales del bloque de cliente.
     */
    private void limpiarCamposCliente() {
        txtNombreApellidos.setText("");
        txtDocumento.setText("");
        txtTipoCliente.setText("");
        txtDireccionCompleta.setText("");
    }

    /**
     * Formatea un importe numérico como texto.
     *
     * @param valor valor a formatear
     * @return importe formateado
     */
    private String formatearImporte(double valor) {
        return String.format("%.2f", valor);
    }

    /**
     * Guarda el presupuesto calculado en la colección Presupuestos.
     *
     * @param event evento del botón
     */
    @FXML
    private void guardarPresupuesto(ActionEvent event) {

        if (instalacionSeleccionada == null) {
            AlertasSolarManager.warning("Aviso", "No hay instalación seleccionada.");
            return;
        }

        try {
            calculoPresupuesto();

            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccionPresupuestos = db.getCollection("Presupuestos");

            Document presupuestoDoc = new Document();
            presupuestoDoc.append("idCliente", idCliente);
            presupuestoDoc.append("idComercial", "");
            presupuestoDoc.append("fechaCreacion", LocalDate.now().toString());
            presupuestoDoc.append("estado", comboEstado.getValue() != null ? comboEstado.getValue() : Presupuesto.EstadoPresupuesto.BORRADOR.name());
            presupuestoDoc.append("instalacion", convertirInstalacionADocument(instalacionSeleccionada));
            presupuestoDoc.append("lineas", convertirLineasADocuments(lineasCalculadas));
            presupuestoDoc.append("subtotal", subtotalCalculado);
            presupuestoDoc.append("iva", ivaCalculado);
            presupuestoDoc.append("total", totalCalculado);

            coleccionPresupuestos.insertOne(presupuestoDoc);

            AlertasSolarManager.operacionCorrecta();

            Stage stage = (Stage) txtNombreApellidos.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo guardar el presupuesto.");
        }
    }

    /**
     * Convierte la instalación seleccionada en un documento de MongoDB.
     *
     * @param instalacion instalación a convertir
     * @return documento con los datos de la instalación
     */
    private Document convertirInstalacionADocument(InstalacionFotovoltaica instalacion) {
        Document doc = new Document();

        doc.append("id", instalacion.getId());
        doc.append("idCliente", instalacion.getIdCliente());
        doc.append("potenciaInstalada", instalacion.getPotenciaInstalada());
        doc.append("numeroPaneles", instalacion.getNumeroPaneles());
        doc.append("produccionEstimada", instalacion.getProduccionEstimada());
        doc.append("ahorroEstimado", instalacion.getAhorroEstimado());
        doc.append("inversor", instalacion.getInversor());
        doc.append("bateria", instalacion.getBateria());

        if (instalacion.getDireccion() != null) {
            Document direccionDoc = new Document();
            direccionDoc.append("calle", instalacion.getDireccion().getCalle());
            direccionDoc.append("numero", instalacion.getDireccion().getNumero());
            direccionDoc.append("codigoPostal", instalacion.getDireccion().getCodigoPostal());
            direccionDoc.append("municipio", instalacion.getDireccion().getMunicipio());
            direccionDoc.append("provincia", instalacion.getDireccion().getProvincia());
            doc.append("direccion", direccionDoc);
        } else {
            doc.append("direccion", null);
        }

        return doc;
    }

    /**
     * Convierte la lista de líneas de presupuesto en documentos de MongoDB.
     *
     * @param lineas líneas del presupuesto
     * @return lista de documentos
     */
    private List<Document> convertirLineasADocuments(List<LineaPresupuesto> lineas) {
        List<Document> lista = new ArrayList<>();

        if (lineas == null) {
            return lista;
        }

        for (LineaPresupuesto linea : lineas) {
            Document doc = new Document();
            doc.append("idProducto", linea.getIdProducto());
            doc.append("nombreProducto", linea.getNombreProducto());
            doc.append("cantidad", linea.getCantidad());
            doc.append("precioUnitario", linea.getPrecioUnitario());
            doc.append("totalLinea", linea.getTotalLinea());
            lista.add(doc);
        }

        return lista;
    }

    /**
     * Cierra la ventana actual.
     *
     * @param event evento del botón
     */
    @FXML
    private void cancelar(ActionEvent e) {
        if (!AlertasSolarManager.confirmar(
                "Salir sin guardar",
                "¿Desea salir sin guardar los cambios?"
        )) {
            return;
        }

        volver(e);
    }
    @FXML
    private void volver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (IOException e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("Error al volver a la pantalla de comerciales");
        }
    }
}