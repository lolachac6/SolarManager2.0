package components.pantallas.pantallaDetallePresupuesto;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Presupuesto;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.AlertasSolarManager;

/**
 * Controlador de la pantalla de detalle de presupuesto.
 *
 * <p>
 * Muestra en modo solo lectura los datos generales del presupuesto, el cliente
 * asociado y el detalle económico de las líneas guardadas en MongoDB.
 * </p>
 *
 * @author Iván
 */
public class pantallaDetallePresupuestoController implements Initializable {

    /**
     * Campo de texto para el identificador del presupuesto.
     */
    @FXML
    private TextField txtId;

    /**
     * Campo de texto para el nombre completo del cliente.
     */
    @FXML
    private TextField txtCliente;

    /**
     * Campo de texto para la línea de paneles.
     */
    @FXML
    private TextField txtPaneles;

    /**
     * Campo de texto para la línea de estructura.
     */
    @FXML
    private TextField txtEstructura;

    /**
     * Campo de texto para la línea de inversor.
     */
    @FXML
    private TextField txtInversor;

    /**
     * Campo de texto para el valor de batería.
     */
    @FXML
    private TextField txtBateria;

    /**
     * Campo de texto para la línea de material eléctrico.
     */
    @FXML
    private TextField txtMaterialElectrico;

    /**
     * Campo de texto para la línea de mano de obra.
     */
    @FXML
    private TextField txtManoObra;

    /**
     * Campo de texto para la línea de tramitación.
     */
    @FXML
    private TextField txtTramitacion;

    /**
     * Campo de texto para el subtotal.
     */
    @FXML
    private TextField txtSubtotal;

    /**
     * Campo de texto para el IVA.
     */
    @FXML
    private TextField txtIva;

    /**
     * Campo de texto para el total.
     */
    @FXML
    private TextField txtTotal;

    /**
     * Campo de texto para el estado del presupuesto.
     */
    @FXML
    private TextField txtEstado;

    /**
     * Combo para el estado del presupuesto.
     */
    @FXML
    private ComboBox<String> comboEstado;

    /**
     * Botón de guardar cambios.
     */
    @FXML
    private Button guardar;

    /**
     * Documento del presupuesto actualmente cargado.
     */
    private Document presupuestoActual;

    /**
     * Inicializa el controlador.
     *
     * @param url ubicación del recurso
     * @param rb recursos internacionales
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarEstados();
    }

    /**
     * Carga en pantalla los datos del presupuesto recibido.
     *
     * @param presupuesto documento del presupuesto seleccionado
     */
    public void cargarPresupuesto(Document presupuesto) {
        if (presupuesto == null) {
            return;
        }

        presupuestoActual = presupuesto;

        txtId.setText(valorTexto(presupuesto.get("_id")));
        txtCliente.setText(obtenerNombreCliente(presupuesto));
        txtSubtotal.setText(valorNumericoComoTexto(presupuesto.get("subtotal")) + " €");
        txtIva.setText(valorNumericoComoTexto(presupuesto.get("iva")) + " €");
        txtTotal.setText(valorNumericoComoTexto(presupuesto.get("total")) + " €");

        String estado = valorTexto(presupuesto.get("estado"));
        txtEstado.setText(estado);

        if (estado != null && !estado.trim().isEmpty()) {
            comboEstado.setValue(estado);
        }

        cargarDatosInstalacion(presupuesto);
    }

    /**
     * Carga en pantalla los datos de instalación y líneas del presupuesto.
     *
     * @param presupuesto documento del presupuesto
     */
    private void cargarDatosInstalacion(Document presupuesto) {
        Document instalacion = presupuesto.get("instalacion", Document.class);
        List<Document> lineas = obtenerLineasPresupuesto(presupuesto);

        txtPaneles.setText(obtenerLineaProductoPorTipo(lineas, "PANEL_SOLAR"));
        txtEstructura.setText(obtenerLineaProductoPorTipo(lineas, "ESTRUCTURA"));
        txtInversor.setText(obtenerLineaProductoPorTipo(lineas, "INVERSOR"));
        txtMaterialElectrico.setText(obtenerLineaManualPorNombre(lineas, "Material eléctrico") + " €");
        txtManoObra.setText(obtenerLineaManualPorNombre(lineas, "Mano de obra"));
        txtTramitacion.setText("300 €");

        if (txtPaneles.getText().trim().isEmpty() && instalacion != null) {
            Object numeroPanelesObj = instalacion.get("numeroPaneles");
            if (numeroPanelesObj != null) {
                txtPaneles.setText(valorTexto(numeroPanelesObj));
            }
        }

        if (txtInversor.getText().trim().isEmpty() && instalacion != null) {
            txtInversor.setText(valorTexto(instalacion.get("inversor")));
        }

        if (instalacion != null) {
            Object bateriaObj = instalacion.get("bateria");
            boolean bateria = false;

            if (bateriaObj instanceof Boolean) {
                bateria = (Boolean) bateriaObj;
            } else if (bateriaObj != null) {
                bateria = Boolean.parseBoolean(String.valueOf(bateriaObj));
            }

            txtBateria.setText(bateria ? "Sí" : "No");
        } else {
            txtBateria.setText("No");
        }
    }

    /**
     * Recupera el nombre completo del cliente asociado al presupuesto.
     *
     * @param presupuesto documento del presupuesto
     * @return nombre y apellidos del cliente, o el id si no puede resolverse
     */
    private String obtenerNombreCliente(Document presupuesto) {
        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccionClientes = db.getCollection("Clientes");

            Object idClienteObj = presupuesto.get("idCliente");
            if (idClienteObj == null) {
                return "";
            }

            String idCliente = String.valueOf(idClienteObj);
            Document cliente;

            try {
                cliente = coleccionClientes.find(new Document("_id", new ObjectId(idCliente))).first();
            } catch (Exception e) {
                cliente = coleccionClientes.find(new Document("_id", idCliente)).first();
            }

            if (cliente != null) {
                String nombre = valorTexto(cliente.get("nombre"));
                String apellidos = valorTexto(cliente.get("apellidos"));
                return (nombre + " " + apellidos).trim();
            }

            return idCliente;

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Obtiene la lista de líneas almacenadas en el presupuesto.
     *
     * @param presupuesto documento del presupuesto
     * @return lista de líneas del presupuesto
     */
    private List<Document> obtenerLineasPresupuesto(Document presupuesto) {
        List<Document> lineas = presupuesto.getList("lineas", Document.class);
        if (lineas == null) {
            return new ArrayList<Document>();
        }
        return new ArrayList<Document>(lineas);
    }

    /**
     * Recupera una línea de producto a partir del tipo de producto almacenado
     * en la colección Productos.
     *
     * @param lineas líneas del presupuesto
     * @param tipoProducto tipo de producto a localizar
     * @return línea formateada o cadena vacía si no existe
     */
    private String obtenerLineaProductoPorTipo(List<Document> lineas, String tipoProducto) {
        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccionProductos = db.getCollection("Productos");

            for (Document linea : lineas) {
                String idProducto = valorTexto(linea.get("idProducto"));

                if (idProducto.trim().isEmpty()) {
                    continue;
                }

                Document producto;

                try {
                    producto = coleccionProductos.find(new Document("_id", new ObjectId(idProducto))).first();
                } catch (Exception e) {
                    producto = coleccionProductos.find(new Document("_id", idProducto)).first();
                }

                if (producto == null) {
                    continue;
                }

                String tipo = valorTexto(producto.get("tipoProducto"));
                if (tipo.trim().isEmpty()) {
                    tipo = valorTexto(producto.get("tipo"));
                }

                if (tipoProducto.equalsIgnoreCase(tipo)) {
                    Number cantidad = linea.get("cantidad", Number.class);
                    Number precioUnitario = linea.get("precioUnitario", Number.class);
                    Number totalLinea = linea.get("totalLinea", Number.class);

                    int cantidadValor = cantidad != null ? cantidad.intValue() : 0;
                    double precioValor = precioUnitario != null ? precioUnitario.doubleValue() : 0.0;
                    double totalValor = totalLinea != null ? totalLinea.doubleValue() : 0.0;

                    return formatearLinea(cantidadValor, precioValor, totalValor);
                }
            }
        } catch (Exception e) {
            return "";
        }

        return "";
    }

    /**
     * Recupera una línea manual buscando por nombreProducto.
     *
     * @param lineas líneas del presupuesto
     * @param nombreBuscado nombre visible de la línea
     * @return línea formateada o cadena vacía si no existe
     */
    private String obtenerLineaManualPorNombre(List<Document> lineas, String nombreBuscado) {
        String nombreNormalizado = normalizar(nombreBuscado);

        for (Document linea : lineas) {
            String nombreLinea = normalizar(valorTexto(linea.get("nombreProducto")));

            if (nombreLinea.equals(nombreNormalizado)) {
                Number cantidad = linea.get("cantidad", Number.class);
                Number precioUnitario = linea.get("precioUnitario", Number.class);
                Number totalLinea = linea.get("totalLinea", Number.class);

                int cantidadValor = cantidad != null ? cantidad.intValue() : 0;
                double precioValor = precioUnitario != null ? precioUnitario.doubleValue() : 0.0;
                double totalValor = totalLinea != null ? totalLinea.doubleValue() : 0.0;

                return formatearLinea(cantidadValor, precioValor, totalValor);
            }
        }

        return "";
    }

    /**
     * Formatea una línea con cantidad, precio unitario y total.
     *
     * @param cantidad cantidad de unidades
     * @param precioUnitario precio unitario
     * @param totalLinea total de la línea
     * @return representación formateada de la línea
     */
    private String formatearLinea(int cantidad, double precioUnitario, double totalLinea) {
        return cantidad + " x " + formatearImporte(precioUnitario) + " = " + formatearImporte(totalLinea) + "€";
    }

    /**
     * Formatea un importe con dos decimales y coma decimal.
     *
     * @param valor importe a formatear
     * @return importe formateado
     */
    private String formatearImporte(double valor) {
        return String.format(Locale.US, "%.2f", valor).replace(".", ",");
    }

    /**
     * Normaliza un texto eliminando tildes, espacios y guiones bajos para
     * facilitar comparaciones.
     *
     * @param valor texto a normalizar
     * @return texto normalizado
     */
    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }

        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("_", "")
                .replace(" ", "")
                .trim()
                .toUpperCase();
    }

    /**
     * Convierte un valor cualquiera a texto controlando nulos.
     *
     * @param valor valor a convertir
     * @return valor en texto
     */
    private String valorTexto(Object valor) {
        return valor == null ? "" : valor.toString();
    }

    /**
     * Convierte un valor numérico a texto usando coma decimal.
     *
     * @param valor valor a convertir
     * @return valor numérico en texto
     */
    private String valorNumericoComoTexto(Object valor) {
        if (valor == null) {
            return "";
        }
        if (valor instanceof Number) {
            Number n = (Number) valor;
            return String.valueOf(n.doubleValue()).replace(".", ",");
        }
        return valor.toString();
    }

    /**
     * Carga los posibles estados del presupuesto.
     */
    private void cargarEstados() {
        if (comboEstado.getItems().isEmpty()) {
            comboEstado.getItems().addAll(
                    Presupuesto.EstadoPresupuesto.BORRADOR.name(),
                    Presupuesto.EstadoPresupuesto.ENVIADO.name(),
                    Presupuesto.EstadoPresupuesto.ACEPTADO.name(),
                    Presupuesto.EstadoPresupuesto.RECHAZADO.name(),
                    Presupuesto.EstadoPresupuesto.FACTURADO.name()
            );
        }

        if (comboEstado.getValue() == null) {
            comboEstado.setValue(Presupuesto.EstadoPresupuesto.BORRADOR.name());
        }
    }

    /**
     * Guarda el nuevo estado del presupuesto actual en la base de datos.
     */
    @FXML
    private void guardar() {
        try {
            if (presupuestoActual == null) {
                AlertasSolarManager.warning("Presupuesto no cargado", "No hay ningún presupuesto cargado.");
                return;
            }

            String idPresupuesto = valorTexto(presupuestoActual.get("_id"));
            String estadoActual = valorTexto(presupuestoActual.get("estado"));
            String nuevoEstado = comboEstado.getValue();

            if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                AlertasSolarManager.warning("Estado obligatorio", "Debe seleccionar un estado.");
                return;
            }

            if (nuevoEstado.equals(estadoActual)) {
                AlertasSolarManager.info("Sin cambios", "No se ha realizado ningún cambio en el estado.");
                return;
            }

            boolean confirmar = AlertasSolarManager.confirmar(
                    "Actualizar estado del presupuesto",
                    "¿Desea cambiar el estado de \"" + estadoActual + "\" a \"" + nuevoEstado + "\"?"
            );

            if (!confirmar) {
                return;
            }

            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccionPresupuestos = db.getCollection("Presupuestos");

            Document filtro;

            try {
                filtro = new Document("_id", new ObjectId(idPresupuesto));
            } catch (Exception e) {
                filtro = new Document("_id", idPresupuesto);
            }

            Document actualizacion = new Document("$set", new Document("estado", nuevoEstado));
            coleccionPresupuestos.updateOne(filtro, actualizacion);

            presupuestoActual.put("estado", nuevoEstado);
            txtEstado.setText(nuevoEstado);

            AlertasSolarManager.info("Estado actualizado", "El estado del presupuesto se ha actualizado correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.error("Error al guardar", "No se ha podido actualizar el estado del presupuesto.");
        }
    }

    /**
     * Cierra la ventana actual.
     */
    @FXML
    private void cerrar() {
        Stage stage = (Stage) txtId.getScene().getWindow();
        stage.close();
    }
}