package components.pantallas.pantallaDetalleClientes;

import DB.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.CifradoDatos;

/**
 * Controlador de la pantalla modal de detalle de cliente.
 *
 * @author Iván
 */
public class PantallaDetalleClientesController implements Initializable {

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTipoCliente;
    @FXML private TextField txtDni;
    @FXML private TextField txtCif;
    @FXML private TextField txtNumeroCuenta;
    @FXML private TextField txtObservaciones;
    @FXML private TextField txtIdComercial;
    @FXML private TextField txtCalle;
    @FXML private TextField txtNumero;
    @FXML private TextField txtCodigoPostal;
    @FXML private TextField txtMunicipio;
    @FXML private TextField txtProvincia;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void cargarClientePorId(String idCliente) {
        try {
            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccion = db.getCollection("Clientes");

            Document doc = coleccion.find(new Document("_id", new ObjectId(idCliente))).first();

            if (doc == null) {
                return;
            }

            txtId.setText(doc.getObjectId("_id").toString());
            txtNombre.setText(valorTexto(doc.getString("nombre")));
            txtApellidos.setText(valorTexto(doc.getString("apellidos")));
            txtTelefono.setText(valorTexto(CifradoDatos.descifrarSiEsPosible(doc.getString("telefono"))));
            txtEmail.setText(valorTexto(doc.getString("email")));
            txtTipoCliente.setText(valorTexto(doc.getString("tipoCliente")));
            txtDni.setText(valorTexto(CifradoDatos.descifrarSiEsPosible(doc.getString("dni"))));
            txtCif.setText(valorTexto(doc.getString("cif")));
            txtNumeroCuenta.setText(valorTexto(CifradoDatos.descifrarSiEsPosible(doc.getString("numeroCuenta"))));
            txtObservaciones.setText(valorTexto(doc.getString("observaciones")));
            txtIdComercial.setText(valorTexto(doc.getString("idComercialAsignado")));

            Document dir = (Document) doc.get("direccion");
            if (dir != null) {
                txtCalle.setText(valorTexto(dir.getString("calle")));
                txtNumero.setText(valorTexto(dir.getString("numero")));
                txtCodigoPostal.setText(valorTexto(dir.getString("codigoPostal")));
                txtMunicipio.setText(valorTexto(dir.getString("municipio")));
                txtProvincia.setText(valorTexto(dir.getString("provincia")));
            } else {
                txtCalle.setText("");
                txtNumero.setText("");
                txtCodigoPostal.setText("");
                txtMunicipio.setText("");
                txtProvincia.setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String valorTexto(String valor) {
        return valor == null ? "" : valor;
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) txtId.getScene().getWindow();
        stage.close();
    }
}