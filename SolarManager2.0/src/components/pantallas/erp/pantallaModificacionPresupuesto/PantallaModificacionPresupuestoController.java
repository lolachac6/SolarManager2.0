package components.pantallas.erp.pantallaModificacionPresupuesto;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import java.net.URL;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Presupuesto;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.AlertasSolarManager;

public class PantallaModificacionPresupuestoController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtTipoCliente;

    @FXML private TextField txtCalle;
    @FXML private TextField txtNumero;
    @FXML private TextField txtLocalidad;
    @FXML private TextField txtCodigoPostal;
    @FXML private TextField txtProvincia;

    @FXML private TextField txtPanelesSolares;
    @FXML private TextField txtEstructura;
    @FXML private TextField txtInversor;
    @FXML private TextField txtBateria;
    @FXML private TextField txtMaterialElectrico;
    @FXML private TextField txtManoObra;
    @FXML private TextField txtTramitacion;

    @FXML private ComboBox<Presupuesto.EstadoPresupuesto> comboEstado;

    private Document presupuestoOriginal;
    private String idPresupuesto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboEstado.getItems().setAll(Presupuesto.EstadoPresupuesto.values());
    }

    public void cargarPresupuesto(Document presupuesto) {
        this.presupuestoOriginal = presupuesto;
        this.idPresupuesto = presupuesto.getObjectId("_id").toHexString();

        Document cliente = presupuesto.get("cliente", Document.class);
        Document direccionCliente = cliente != null ? cliente.get("direccion", Document.class) : null;
        Document instalacion = presupuesto.get("instalacion", Document.class);
        List<Document> lineas = obtenerLineasPresupuesto(presupuesto);

        if (cliente != null) {
            txtNombre.setText(valorTexto(cliente.getString("nombre")));
            txtApellidos.setText(valorTexto(cliente.getString("apellidos")));

            String dni = valorTexto(cliente.getString("dni"));
            String cif = valorTexto(cliente.getString("cif"));
            txtDocumento.setText(!dni.isEmpty() ? dni : cif);

            txtTipoCliente.setText(valorTexto(cliente.getString("tipoCliente")));
        }

        if (direccionCliente != null) {
            txtCalle.setText(valorTexto(direccionCliente.getString("calle")));
            txtNumero.setText(valorTexto(direccionCliente.getString("numero")));
            txtLocalidad.setText(valorTexto(direccionCliente.getString("municipio")));
            txtCodigoPostal.setText(valorTexto(direccionCliente.getString("codigoPostal")));
            txtProvincia.setText(valorTexto(direccionCliente.getString("provincia")));
        }

        if (instalacion != null) {
            txtPanelesSolares.setText(valorNumericoComoTexto(instalacion.get("numeroPaneles")));
            txtInversor.setText(valorTexto(instalacion.getString("inversor")));

            // 🔧 CORRECCIÓN: leer boolean correctamente
            Boolean bateria = instalacion.getBoolean("bateria");
            txtBateria.setText(bateria != null && bateria ? "true" : "false");
        }

        txtEstructura.setText(obtenerNombreLinea(lineas, "ESTRUCTURA"));
        txtMaterialElectrico.setText(valorNumericoComoTexto(obtenerTotalLinea(lineas, "MATERIAL_ELECTRICO")));
        txtManoObra.setText(valorNumericoComoTexto(obtenerTotalLinea(lineas, "MANO_OBRA")));
        txtTramitacion.setText(valorNumericoComoTexto(obtenerTotalLinea(lineas, "TRAMITACION")));

        String estado = valorTexto(presupuesto.getString("estado"));
        if (!estado.isEmpty()) {
            comboEstado.setValue(Presupuesto.EstadoPresupuesto.valueOf(estado));
        }
    }

    @FXML
    private void guardarCambios() {
        if (comboEstado.getValue() == null) {
            AlertasSolarManager.info(null, "Debe elegir un estado para el presupuesto");
            return;
        }

        boolean confirmar = AlertasSolarManager.confirmar(
                "Confirmar guardado",
                "¿Desea guardar los cambios del presupuesto?",
                "Sí",
                "No"
        );

        if (!confirmar) {
            return;
        }

        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccionPresupuestos = db.getCollection("Presupuestos");

            Document presupuestoActualizado = new Document(presupuestoOriginal);

            Document cliente = presupuestoActualizado.get("cliente", Document.class);
            if (cliente == null) {
                cliente = new Document();
            }

            Document direccionCliente = cliente.get("direccion", Document.class);
            if (direccionCliente == null) {
                direccionCliente = new Document();
            }

            cliente.put("nombre", txtNombre.getText().trim());
            cliente.put("apellidos", txtApellidos.getText().trim());
            cliente.put("tipoCliente", txtTipoCliente.getText().trim());

            if ("PARTICULAR".equalsIgnoreCase(txtTipoCliente.getText().trim())) {
                cliente.put("dni", txtDocumento.getText().trim());
                cliente.put("cif", "");
            } else {
                cliente.put("dni", "");
                cliente.put("cif", txtDocumento.getText().trim());
            }

            direccionCliente.put("calle", txtCalle.getText().trim());
            direccionCliente.put("numero", txtNumero.getText().trim());
            direccionCliente.put("municipio", txtLocalidad.getText().trim());
            direccionCliente.put("codigoPostal", txtCodigoPostal.getText().trim());
            direccionCliente.put("provincia", txtProvincia.getText().trim());

            cliente.put("direccion", direccionCliente);
            presupuestoActualizado.put("cliente", cliente);

            Document instalacion = presupuestoActualizado.get("instalacion", Document.class);
            if (instalacion == null) {
                instalacion = new Document();
            }

            int numeroPaneles = parseEntero(txtPanelesSolares.getText());
            double materialElectrico = parseDouble(txtMaterialElectrico.getText());
            double manoObra = parseDouble(txtManoObra.getText());
            double tramitacion = parseDouble(txtTramitacion.getText());

            instalacion.put("numeroPaneles", numeroPaneles);
            instalacion.put("inversor", txtInversor.getText().trim());
            instalacion.put("bateria", txtBateria.getText().trim());
            presupuestoActualizado.put("instalacion", instalacion);

            List<Document> lineas = obtenerLineasPresupuesto(presupuestoActualizado);

            actualizarONuevaLinea(lineas, "ESTRUCTURA", txtEstructura.getText().trim());
            actualizarLineaManual(lineas, "MATERIAL_ELECTRICO", "Material eléctrico", materialElectrico);
            actualizarLineaManual(lineas, "MANO_OBRA", "Mano de obra", manoObra);
            actualizarLineaManual(lineas, "TRAMITACION", "Tramitación de subvenciones y gestiones administrativas", tramitacion);

            presupuestoActualizado.put("lineas", lineas);

            double subtotal = recalcularSubtotal(lineas);
            double iva = subtotal * 0.21;
            double total = subtotal + iva;

            presupuestoActualizado.put("subtotal", subtotal);
            presupuestoActualizado.put("iva", iva);
            presupuestoActualizado.put("total", total);
            presupuestoActualizado.put("estado", comboEstado.getValue().name());

            coleccionPresupuestos.replaceOne(
                    new Document("_id", new ObjectId(idPresupuesto)),
                    presupuestoActualizado,
                    new ReplaceOptions().upsert(false)
            );

            AlertasSolarManager.operacionCorrecta();
            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo actualizar el presupuesto.");
        }
    }

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

    private List<Document> obtenerLineasPresupuesto(Document presupuesto) {
        List<Document> lineas = presupuesto.getList("lineas", Document.class);
        if (lineas == null) {
            return new ArrayList<Document>();
        }
        return new ArrayList<Document>(lineas);
    }

    private String obtenerNombreLinea(List<Document> lineas, String idProducto) {
        for (Document linea : lineas) {
            if (idProducto.equals(valorTexto(linea.getString("idProducto")))) {
                return valorTexto(linea.getString("nombreProducto"));
            }
        }
        return "";
    }

    private double obtenerTotalLinea(List<Document> lineas, String idProducto) {
        for (Document linea : lineas) {
            if (idProducto.equals(valorTexto(linea.getString("idProducto")))) {
                Number n = linea.get("totalLinea", Number.class);
                return n != null ? n.doubleValue() : 0.0;
            }
        }
        return 0.0;
    }

    private void actualizarONuevaLinea(List<Document> lineas, String idProducto, String nombre) {
        for (Document linea : lineas) {
            if (idProducto.equals(valorTexto(linea.getString("idProducto")))) {
                linea.put("nombreProducto", nombre);
                return;
            }
        }

        lineas.add(new Document()
                .append("idProducto", idProducto)
                .append("nombreProducto", nombre)
                .append("cantidad", 1)
                .append("precioUnitario", 0.0)
                .append("totalLinea", 0.0));
    }

    private void actualizarLineaManual(List<Document> lineas, String idProducto, String nombre, double total) {
        for (Document linea : lineas) {
            if (idProducto.equals(valorTexto(linea.getString("idProducto")))) {
                linea.put("nombreProducto", nombre);
                linea.put("cantidad", 1);
                linea.put("precioUnitario", total);
                linea.put("totalLinea", total);
                return;
            }
        }

        lineas.add(new Document()
                .append("idProducto", idProducto)
                .append("nombreProducto", nombre)
                .append("cantidad", 1)
                .append("precioUnitario", total)
                .append("totalLinea", total));
    }

    private double recalcularSubtotal(List<Document> lineas) {
        double subtotal = 0.0;
        for (Document linea : lineas) {
            Number totalLinea = linea.get("totalLinea", Number.class);
            if (totalLinea != null) {
                subtotal += totalLinea.doubleValue();
            }
        }
        return subtotal;
    }

    private int parseEntero(String valor) {
        try {
            return Integer.parseInt(valor.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(String valor) {
        try {
            String limpio = valor.trim().replace(".", "").replace(",", ".");
            return Double.parseDouble(limpio);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String valorTexto(Object valor) {
        return valor == null ? "" : valor.toString();
    }

    private String valorNumericoComoTexto(Object valor) {
        if (valor == null) return "";
        if (valor instanceof Number) {
            Number n = (Number) valor;
            return String.valueOf(n.doubleValue()).replace(".", ",");
        }
        return valor.toString();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}