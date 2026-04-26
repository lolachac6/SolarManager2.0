package components.pantallas.erp.pantallaAltaProveedor;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Direccion;
import modelo.Proveedor;
import static okhttp3.Cache.key;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.AlertasSolarManager;
import utils.CifradoDatos;

/**
 * Controlador de la pantalla de alta y edición de proveedores.
 *
 * <p>Gestiona el formulario de proveedor, la validación de campos,
 * el guardado en MongoDB y la navegación asociada a la pantalla.</p>
 *
 * @author Iván
 */
public class PantallaAltaProveedorController {

    private ObjectId proveedorId;
    private boolean modoEdicion = false;

    @FXML private TextField txtNombreEmpresa;
    @FXML private TextField txtRazonSocial;
    @FXML private TextField txtCif;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtNumero;
    @FXML private TextField txtMunicipio;
    @FXML private TextField txtProvincia;
    @FXML private TextField txtCodigoPostal;
    @FXML private TextArea txtObservaciones;

    /**
     * Inicializa la pantalla bloqueando inicialmente el formulario.
     */
    @FXML
    public void initialize() {
        bloquearFormulario(false);
    }

    /**
     * Bloquea o desbloquea los campos del formulario.
     *
     * @param bloquear true para bloquear, false para desbloquear
     */
    private void bloquearFormulario(boolean bloquear) {
        txtNombreEmpresa.setDisable(bloquear);
        txtRazonSocial.setDisable(bloquear);
        txtTelefono.setDisable(bloquear);
        txtEmail.setDisable(bloquear);
        txtDireccion.setDisable(bloquear);
        txtObservaciones.setDisable(bloquear);
    }

    /**
     * Limpia el formulario y lo devuelve a su estado inicial.
     */
    @FXML
    private void nuevoProveedor() {
        txtNombreEmpresa.clear();
        txtRazonSocial.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        txtObservaciones.clear();
        proveedorId = null;
        modoEdicion = false;
        bloquearFormulario(false);
    }

    /**
     * Guarda o actualiza un proveedor.
     */
    @FXML
    private void guardarProveedor(ActionEvent e) {

        if (!validarCampos()) {
            return;
        }

        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar guardado");
        confirmacion.setHeaderText("Guardar proveedor");
        confirmacion.setContentText("¿Deseas guardar los cambios del proveedor?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (!resultado.isPresent() || resultado.get() != ButtonType.OK) {
            return;
        }

        try {
            Proveedor nuevoProveedor = new Proveedor();
            nuevoProveedor.setNombreEmpresa(txtNombreEmpresa.getText());
            nuevoProveedor.setTelefono(CifradoDatos.cifrar(txtTelefono.getText()));
            nuevoProveedor.setEmail(txtEmail.getText());
            nuevoProveedor.setDireccion(new Direccion());
            nuevoProveedor.setRazonSocial(txtRazonSocial.getText());
            nuevoProveedor.setObservaciones(txtObservaciones.getText());
            nuevoProveedor.setCif(CifradoDatos.cifrar(txtCif.getText()));

            Document direccionDoc = new Document()
                    .append("calle", txtDireccion.getText())
                    .append("numero", txtNumero.getText())
                    .append("codigoPostal", txtCodigoPostal.getText())
                    .append("municipio", txtMunicipio.getText())
                    .append("provincia", txtProvincia.getText());

            Document doc = new Document()
                    .append("nombreEmpresa", nuevoProveedor.getNombreEmpresa())
                    .append("razonSocial", nuevoProveedor.getRazonSocial())
                    .append("cif", nuevoProveedor.getCif())
                    .append("telefono", nuevoProveedor.getTelefono())
                    .append("email", nuevoProveedor.getEmail())
                    .append("direccion", direccionDoc)
                    .append("observaciones", nuevoProveedor.getObservaciones());

            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Proveedor");

            if (proveedorId != null) {
                    coleccion.updateOne(
                            new Document("_id", proveedorId),
                            new Document("$set", doc)
                    );

                } else {

                    coleccion.insertOne(doc);
                }

                    AlertasSolarManager.proveedorGuardadoCorrectamente();

            cambiarPantalla(
                    (Node) e.getSource(),
                    "/components/pantallas/erp/pantallaProveedor/pantallaProveedor.fxml"
            );

        } catch (IOException ex) {
            AlertasSolarManager.errorGuardarProveedor(ex.getMessage());
        }
    }

    private boolean validarCampos() {

        if (txtNombreEmpresa.getText().isEmpty()) {
            AlertasSolarManager.nombreProveedorObligatorio();
            return false;
        }
        
        if (txtRazonSocial.getText().isEmpty()) {
            AlertasSolarManager.razonSocialObligatorio();
            return false;
        }
        
        if (txtCif.getText().trim().isEmpty()) {
            AlertasSolarManager.cifObligatorio();
            return false;
        }

        if (!txtCif.getText().matches("(?i)^[A-Z]\\d{8}$")) {
            AlertasSolarManager.cifInvalido();
            return false;
        }

        if (txtTelefono.getText().isEmpty()) {
            AlertasSolarManager.telefonoObligatorio();
            return false;
        }
        
        if (!txtTelefono.getText().matches("\\d{9}")) {
            AlertasSolarManager.telefonoInvalido();
            return false;
        }

        if (txtEmail.getText().isEmpty()) {
            AlertasSolarManager.emailProveedorObligatorio();
            return false;
        }

        if (!txtEmail.getText().contains("@")) {
            AlertasSolarManager.emailProveedorInvalido();
            return false;
        }

        if (txtDireccion.getText().isEmpty()) {
            AlertasSolarManager.direccionObligatoria();
            return false;
        }
        
        
        if (txtMunicipio.getText().trim().isEmpty()) {
        AlertasSolarManager.municipioObligatorio();
        return false;
        }
        
        if (txtProvincia.getText().trim().isEmpty()) {
        AlertasSolarManager.provinciaObligatoria();
        return false;
        }
        
        
        
        String[] provinciasValidas = {
        "Álava", "Albacete", "Alicante", "Almería", "Asturias", "Ávila",
        "Badajoz", "Barcelona", "Burgos",
        "Cáceres", "Cádiz", "Cantabria", "Castellón", "Ciudad Real", "Córdoba", "Cuenca",
        "Girona", "Granada", "Guadalajara", "Guipúzcoa",
        "Huelva", "Huesca",
        "Illes Balears",
        "Jaén",
        "A Coruña",
        "La Rioja", "Las Palmas", "León", "Lleida", "Lugo",
        "Madrid", "Málaga", "Murcia",
        "Navarra",
        "Ourense",
        "Palencia", "Pontevedra",
        "Salamanca", "Santa Cruz de Tenerife", "Segovia", "Sevilla", "Soria",
        "Tarragona", "Teruel", "Toledo",
        "Valencia", "Valladolid", "Vizcaya",
        "Zamora", "Zaragoza",
        "Ceuta", "Melilla"
        };

        boolean provinciaValida = false;
        for (String prov : provinciasValidas) {
            if (prov.equalsIgnoreCase(txtProvincia.getText().trim())) {
                provinciaValida = true;
                break;
            }
        }

        if (!provinciaValida) {
            AlertasSolarManager.provinciaInvalida();
            return false;
        }
        
        if (txtCodigoPostal.getText().trim().isEmpty()) {
        AlertasSolarManager.codigoPostalObligatorio(); 
        return false;
        }

        if (!txtCodigoPostal.getText().matches("\\d{5}")) {
            AlertasSolarManager.codigoPostalInvalido(); 
            return false;
        }

        
        
        return true;
        
    }

    @FXML
    private void cancelar(ActionEvent e) {

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar cambios");
        confirmacion.setHeaderText("Salir sin guardar");
        confirmacion.setContentText(
                "¿Deseas cancelar y volver a la lista de proveedores?\n\n" +
                "Los cambios no guardados se perderán."
        );

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (!resultado.isPresent() || resultado.get() != ButtonType.OK) {
            return;
        }

        cambiarPantalla(
                (Node) e.getSource(),
                "/components/pantallas/erp/pantallaProveedor/pantallaProveedor.fxml"
        );
    }

    @FXML
    private void volver(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),
                "/components/pantallas/erp/pantallaProveedor/pantallaProveedor.fxml");
    }

    /**
     * Cambia la pantalla actual por otra indicada.
     *
     * @param nodo nodo origen
     * @param rutaFXML ruta del fichero FXML
     */
    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga en el formulario los datos del proveedor seleccionado.
     *
     * @param p proveedor a mostrar
     */
    public void cargarProveedor(Proveedor p) {
        
        this.proveedorId = new ObjectId(p.getId());
        this.modoEdicion = true;

        txtNombreEmpresa.setText(p.getNombreEmpresa());
        txtRazonSocial.setText(p.getRazonSocial());
        txtCif.setText(CifradoDatos.descifrarSiEsPosible(p.getCif()));
        txtTelefono.setText(CifradoDatos.descifrarSiEsPosible(p.getTelefono()));
        txtEmail.setText(p.getEmail());
        txtObservaciones.setText(p.getObservaciones());

        if (p.getDireccion() != null) {
            Direccion d = p.getDireccion();

                if (d != null) {
                    txtDireccion.setText(d.getCalle());
                    txtNumero.setText(d.getNumero());
                    txtCodigoPostal.setText(d.getCodigoPostal());
                    txtMunicipio.setText(d.getMunicipio());
                    txtProvincia.setText(d.getProvincia());
                }
        }

        bloquearFormulario(false);
        modoEdicion = false;
    }

    /**
     * Limpia el formulario y restablece su estado inicial.
     */
    private void limpiarFormulario() {
        txtNombreEmpresa.clear();
        txtRazonSocial.clear();
        txtCif.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        txtObservaciones.clear();
        proveedorId = null;
        modoEdicion = false;
        bloquearFormulario(true);
    }

   
}