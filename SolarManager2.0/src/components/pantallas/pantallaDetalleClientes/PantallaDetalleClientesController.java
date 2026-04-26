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
 * Controlador de la ventana modal que muestra el detalle completo de un cliente.
 * 
 * Esta pantalla se utiliza únicamente para visualización, sin permitir edición.
 * El controlador carga los datos del cliente desde MongoDB a partir de su ID,
 * incluyendo información personal, datos de contacto, dirección y el comercial
 * asignado (mostrando su nombre completo en lugar del ID).
 *
 * Los campos sensibles como teléfono, DNI o número de cuenta se descifran
 * automáticamente mediante {@link CifradoDatos#descifrarSiEsPosible(String)}.
 *
 * La ventana se cierra mediante el botón asociado al método {@code cerrar()}.
 * 
 * Implementa {@link Initializable}, aunque no requiere inicialización adicional.
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
    
    /**
     * Carga en la interfaz todos los datos del cliente cuyo identificador
     * coincide con el proporcionado.
     *
     * El método consulta la colección "Clientes" en MongoDB, obtiene el documento
     * correspondiente y rellena los campos de la vista con la información
     * disponible. Los valores nulos se sustituyen por cadenas vacías mediante
     * {@code valorTexto()}.
     *
     * También obtiene el comercial asignado al cliente consultando la colección
     * "Comerciales", mostrando su nombre y apellidos en lugar del ID.
     *
     * Si el cliente no existe o ocurre un error durante la consulta, el método
     * simplemente no modifica la interfaz.
     *
     * @param idCliente identificador del cliente en formato hexadecimal de ObjectId
     */
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
            String idComercial = doc.getString("idComercialAsignado");

            if (idComercial != null && !idComercial.isEmpty()) {

                MongoCollection<Document> colComerciales = db.getCollection("Comerciales");

                Document comercialDoc = colComerciales.find(
                        new Document("_id", new ObjectId(idComercial))
                ).first();

                if (comercialDoc != null) {
                    String nombre = comercialDoc.getString("nombre");
                    String apellidos = comercialDoc.getString("apellidos");

                    txtIdComercial.setText(
                            (apellidos != null && !apellidos.isEmpty())
                            ? nombre + " " + apellidos
                            : nombre
                    );
                } else {
                    txtIdComercial.setText("");
                }

            } else {
                txtIdComercial.setText("");
            }

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
    
     /**
     * Devuelve una cadena segura para mostrar en la interfaz.
     *
     * Si el valor recibido es {@code null}, devuelve una cadena vacía.
     * En caso contrario, devuelve el valor original.
     *
     * @param valor texto que puede ser nulo
     * @return el valor recibido o una cadena vacía si era {@code null}
     */
    private String valorTexto(String valor) {
        return valor == null ? "" : valor;
    }
    
    /**
     * Cierra la ventana modal de detalle del cliente.
     *
     * Obtiene la ventana actual a partir de cualquier nodo de la escena
     * (en este caso, el campo {@code txtId}) y ejecuta {@code close()}.
     */
    @FXML
    private void cerrar() {
        Stage stage = (Stage) txtId.getScene().getWindow();
        stage.close();
    }
}