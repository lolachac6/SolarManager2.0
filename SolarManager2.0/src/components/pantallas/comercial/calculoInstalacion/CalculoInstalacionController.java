package components.pantallas.comercial.calculoInstalacion;

import DB.MongoConnection;
import Integration.google.client.GeocodingClient;
import Integration.google.exception.DatosEntradaInvalidosException;
import Integration.google.exception.DireccionNoCoincideException;
import Integration.google.model.ResultadoGeocoding;
import Integration.google.service.SolarService;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import modelo.Cliente;
import modelo.Direccion;
import modelo.ResultadoSolar;
import org.bson.Document;

import java.io.IOException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import utils.AlertasSolarManager;

/**
 * Controlador de la pantalla de cálculo de instalación fotovoltaica.
 *
 * <p>Gestiona la validación de la dirección, el cálculo de la instalación,
 * la persistencia de datos y la navegación de la pantalla.</p>
 *
 * @author Iván
 */
public class CalculoInstalacionController implements Initializable {

    @FXML private TextField txtCalle;
    @FXML private TextField txtNumero;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtProvincia;
    @FXML private TextField txtCodigoPostal;
    @FXML private TextField txtConsumoAnual;
    @FXML private TextField txtHorasSol;
    @FXML private TextField txtArea;
    @FXML private TextField txtMaxPaneles;
    @FXML private TextField txtPanelesNecesarios;
    @FXML private TextField txtEnergiaPanel;
    @FXML private TextField txtPresupuesto;
    @FXML private TextField txtIdCliente;
    @FXML private CheckBox chkBateria;

    private Cliente clienteSeleccionado;

    /**
     * Inicializa el controlador.
     *
     * @param url URL de inicialización
     * @param rb ResourceBundle asociado
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * Carga en el formulario los datos del cliente seleccionado.
     *
     * @param cliente cliente recibido desde la pantalla de clientes
     */
    public void setCliente(Cliente cliente) {
        this.clienteSeleccionado = cliente;

        if (cliente == null) {
            return;
        }

        txtIdCliente.setText(cliente.getId());

        Direccion direccion = cliente.getDireccion();
        if (direccion != null) {
            txtCalle.setText(direccion.getCalle());
            txtNumero.setText(direccion.getNumero());
            txtCiudad.setText(direccion.getMunicipio());
            txtProvincia.setText(direccion.getProvincia());
            txtCodigoPostal.setText(direccion.getCodigoPostal());
        }
    }

    /**
     * Cambia de pantalla cargando un nuevo FXML.
     *
     * @param nodo Nodo actual
     * @param rutaFXML Ruta del fichero FXML
     */
    private void cambiarPantalla(Node nodo, String rutaFXML) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));
            Stage stage = (Stage) nodo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Vuelve a la pantalla principal.
     *
     * @param e Evento de acción
     */
    @FXML
    private void volverInicio(ActionEvent e) {
        cambiarPantalla(
                (Node) e.getSource(),
                "/components/pantallas/comercial/pantallaGeneral/PantallaGeneral.fxml"
        );
    }

    /**
     * Llama al servicio solar para calcular la instalación y muestra los resultados.
     *
     * @param event Evento de acción
     */
    @FXML
    private void calcularInstalacion(ActionEvent event) {

        try {
            String direccion = construirDireccionCompleta();
            double consumo = obtenerConsumoAnualValidado();

            GeocodingClient geocodingClient = new GeocodingClient();
            ResultadoGeocoding geocoding = geocodingClient.obtenerResultadoGeocoding(direccion);

            String mensajeCorreccion = construirMensajeCorreccion(geocoding);

            if (!mensajeCorreccion.isEmpty()) {
                boolean aceptar = AlertasSolarManager.confirmarCorreccionDireccion(mensajeCorreccion);

                if (!aceptar) {
                    limpiarResultadosCalculados();
                    return;
                }

                aplicarCorrecciones(geocoding);
            }

            SolarService service = new SolarService();
            ResultadoSolar resultado = service.calcularInstalacion(
                    geocoding.getLatitud(),
                    geocoding.getLongitud(),
                    consumo
            );

            txtHorasSol.setText(String.valueOf(Math.round(resultado.getHorasSol())));
            txtArea.setText(String.valueOf(Math.round(resultado.getArea())));
            txtMaxPaneles.setText(String.valueOf(resultado.getMaxPaneles()));
            txtPanelesNecesarios.setText(String.valueOf(resultado.getPanelesNecesarios()));
            txtEnergiaPanel.setText(String.valueOf(Math.round(resultado.getEnergiaPorPanel())));
            txtPresupuesto.setText(String.valueOf(resultado.getPresupuesto()));

            if (resultado.isAutosuficiente()) {
                AlertasSolarManager.instalacionAutosuficiente();
            } else {
                AlertasSolarManager.instalacionSinEspacioSuficiente();
            }

        } catch (DireccionNoCoincideException e) {
            limpiarResultadosCalculados();
            AlertasSolarManager.warning("Dirección no válida", e.getMessage());

        } catch (DatosEntradaInvalidosException e) {
            limpiarResultadosCalculados();
            AlertasSolarManager.warning("Datos de entrada no válidos", e.getMessage());

        } catch (Exception e) {
            limpiarResultadosCalculados();
            AlertasSolarManager.errorCalculoInstalacion(e.getMessage());
        }
    }

    /**
     * Guarda la instalación en MongoDB.
     *
     * @param event Evento de acción
     */
    @FXML
    private void guardarInstalacion(ActionEvent event) {
        try {
            construirDireccionCompleta();
            obtenerConsumoAnualValidado();

            MongoDatabase db = MongoConnection.conectar();
            MongoCollection<Document> coleccionClientes = db.getCollection("Clientes");
            MongoCollection<Document> coleccionInstalaciones = db.getCollection("Instalaciones");

            String idCliente = obtenerTextoNormalizado(txtIdCliente);

            if (idCliente.isEmpty()) {
                throw new DatosEntradaInvalidosException("No se ha recibido el cliente seleccionado");
            }

            Document clienteDoc = coleccionClientes.find(
                    new Document("_id", new org.bson.types.ObjectId(idCliente))
            ).first();

            if (clienteDoc == null) {
                throw new DatosEntradaInvalidosException("No se ha encontrado el cliente seleccionado");
            }

            Document direccionCliente = (Document) clienteDoc.get("direccion");

            if (direccionCliente == null) {
                throw new DatosEntradaInvalidosException("El cliente no tiene dirección registrada");
            }

            Document direccionInstalacion = new Document()
                    .append("calle", direccionCliente.getString("calle"))
                    .append("numero", direccionCliente.getString("numero"))
                    .append("codigoPostal", direccionCliente.getString("codigoPostal"))
                    .append("municipio", direccionCliente.getString("municipio"))
                    .append("provincia", direccionCliente.getString("provincia"));

            Document instalacion = new Document()
                    .append("idCliente", idCliente)
                    .append("potenciaInstalada", convertirADouble(txtEnergiaPanel))
                    .append("numeroPaneles", convertirAInteger(txtPanelesNecesarios))
                    .append("produccionEstimada", convertirADouble(txtHorasSol))
                    .append("ahorroEstimado", convertirADouble(txtPresupuesto))
                    .append("direccion", direccionInstalacion);

            coleccionInstalaciones.insertOne(instalacion);

            AlertasSolarManager.instalacionGuardadaCorrectamente();

        } catch (DatosEntradaInvalidosException e) {
            AlertasSolarManager.warning("Datos de entrada no válidos", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGuardarInstalacion();
        }
    }

    /**
     * Limpia todos los campos del formulario.
     *
     * @param event Evento de acción
     */
    @FXML
    private void limpiarCampos(ActionEvent event) {
        txtCalle.clear();
        txtNumero.clear();
        txtCiudad.clear();
        txtProvincia.clear();
        txtCodigoPostal.clear();
        txtConsumoAnual.clear();

        limpiarResultadosCalculados();

        txtIdCliente.clear();
        chkBateria.setSelected(false);
        clienteSeleccionado = null;
    }

    /**
     * Cancela la operación y vuelve atrás previa confirmación.
     *
     * @param event Evento de acción
     */
    @FXML
    private void cancelar(ActionEvent event) {
        if (AlertasSolarManager.confirmar("Salir sin guardar", "¿Desea salir sin guardar?")) {
            cambiarPantalla(
                    (Node) event.getSource(),
                    "/components/pantallas/comercial/pantallaGeneral/PantallaGeneral.fxml"
            );
        }
    }

    /**
     * Construye la dirección completa a partir de los campos del formulario.
     *
     * @return Dirección completa
     * @throws DatosEntradaInvalidosException Si faltan datos obligatorios o tienen formato incorrecto
     */
    private String construirDireccionCompleta() throws DatosEntradaInvalidosException {

        String calle = obtenerTextoNormalizado(txtCalle);
        String numero = obtenerTextoNormalizado(txtNumero);
        String ciudad = obtenerTextoNormalizado(txtCiudad);
        String provincia = obtenerTextoNormalizado(txtProvincia);
        String codigoPostal = obtenerTextoNormalizado(txtCodigoPostal);

        if (calle.isEmpty()) {
            throw new DatosEntradaInvalidosException("Debe completar la calle");
        }

        if (ciudad.isEmpty()) {
            throw new DatosEntradaInvalidosException("Debe completar la población");
        }

        if (numero.isEmpty()) {
            throw new DatosEntradaInvalidosException("Debe completar el número");
        }

        if (codigoPostal.isEmpty()) {
            throw new DatosEntradaInvalidosException("Debe completar el código postal");
        }

        if (!esTextoGeograficoValido(ciudad)) {
            throw new DatosEntradaInvalidosException("La población no puede contener números ni caracteres no válidos");
        }

        if (!provincia.isEmpty() && !esTextoGeograficoValido(provincia)) {
            throw new DatosEntradaInvalidosException("La provincia no puede contener números ni caracteres no válidos");
        }

        if (!esNumeroDireccionValido(numero)) {
            throw new DatosEntradaInvalidosException("El número de la dirección no es válido");
        }

        if (!codigoPostal.matches("\\d{5}")) {
            throw new DatosEntradaInvalidosException("El código postal debe tener 5 dígitos");
        }

        StringBuilder direccion = new StringBuilder();
        direccion.append(calle).append(" ").append(numero).append(", ");
        direccion.append(ciudad);

        if (!provincia.isEmpty()) {
            direccion.append(", ").append(provincia);
        }

        direccion.append(", ").append(codigoPostal).append(", España");

        return direccion.toString();
    }

    /**
     * Obtiene y valida el consumo anual introducido por el usuario.
     *
     * @return Consumo anual validado
     * @throws DatosEntradaInvalidosException Si el valor no es válido
     */
    private double obtenerConsumoAnualValidado() throws DatosEntradaInvalidosException {

        String textoConsumo = obtenerTextoNormalizado(txtConsumoAnual);

        if (textoConsumo.isEmpty()) {
            throw new DatosEntradaInvalidosException("Debe indicar el consumo anual");
        }

        String valorNormalizado = textoConsumo.replace(",", ".");

        double consumo;

        try {
            consumo = Double.parseDouble(valorNormalizado);
        } catch (NumberFormatException e) {
            throw new DatosEntradaInvalidosException("El consumo debe ser un número válido");
        }

        if (Double.isNaN(consumo) || Double.isInfinite(consumo)) {
            throw new DatosEntradaInvalidosException("El consumo debe ser un número válido");
        }

        if (consumo <= 0) {
            throw new DatosEntradaInvalidosException("El consumo anual debe ser mayor que cero");
        }

        return consumo;
    }

    /**
     * Construye el mensaje de corrección comparando los datos introducidos
     * por el usuario con los devueltos por Geocoding.
     *
     * @param geocoding Resultado de geocodificación
     * @return Mensaje de corrección o cadena vacía si no hay diferencias
     */
    private String construirMensajeCorreccion(ResultadoGeocoding geocoding) {

        StringBuilder mensaje = new StringBuilder();

        agregarLineaCorreccion(
                mensaje,
                "calle",
                obtenerTextoNormalizado(txtCalle),
                geocoding.getCalle()
        );

        agregarLineaCorreccion(
                mensaje,
                "número",
                obtenerTextoNormalizado(txtNumero),
                geocoding.getNumero()
        );

        agregarLineaCorreccion(
                mensaje,
                "población",
                obtenerTextoNormalizado(txtCiudad),
                geocoding.getCiudad()
        );

        String provinciaUsuario = obtenerTextoNormalizado(txtProvincia);
        if (!provinciaUsuario.isEmpty()) {
            agregarLineaCorreccion(
                    mensaje,
                    "provincia",
                    provinciaUsuario,
                    geocoding.getProvincia()
            );
        }

        agregarLineaCorreccion(
                mensaje,
                "código postal",
                obtenerTextoNormalizado(txtCodigoPostal),
                geocoding.getCodigoPostal()
        );

        if (mensaje.length() == 0) {
            return "";
        }

        return "Se han detectado diferencias en la dirección:\n\n"
                + mensaje.toString()
                + "\n¿Está de acuerdo?";
    }

    /**
     * Añade una línea de corrección al mensaje si los valores no coinciden.
     *
     * @param mensaje Constructor del mensaje
     * @param nombreCampo Nombre del campo
     * @param valorUsuario Valor escrito por el usuario
     * @param valorCorrecto Valor corregido por Geocoding
     */
    private void agregarLineaCorreccion(StringBuilder mensaje, String nombreCampo,
                                        String valorUsuario, String valorCorrecto) {

        if (valorCorrecto == null || valorCorrecto.trim().isEmpty()) {
            return;
        }

        if (!coincidenTextos(valorUsuario, valorCorrecto)) {
            mensaje.append("La ").append(nombreCampo)
                    .append(" correcta es: \"")
                    .append(valorCorrecto)
                    .append("\".\n");
        }
    }

    /**
     * Aplica en la interfaz las correcciones devueltas por Geocoding.
     *
     * @param geocoding Resultado de geocodificación
     */
    private void aplicarCorrecciones(ResultadoGeocoding geocoding) {

        if (geocoding.getCalle() != null && !geocoding.getCalle().trim().isEmpty()) {
            txtCalle.setText(geocoding.getCalle());
        }

        if (geocoding.getNumero() != null && !geocoding.getNumero().trim().isEmpty()) {
            txtNumero.setText(geocoding.getNumero());
        }

        if (geocoding.getCiudad() != null && !geocoding.getCiudad().trim().isEmpty()) {
            txtCiudad.setText(geocoding.getCiudad());
        }

        if (geocoding.getProvincia() != null && !geocoding.getProvincia().trim().isEmpty()) {
            txtProvincia.setText(geocoding.getProvincia());
        }

        if (geocoding.getCodigoPostal() != null && !geocoding.getCodigoPostal().trim().isEmpty()) {
            txtCodigoPostal.setText(geocoding.getCodigoPostal());
        }
    }

    /**
     * Obtiene el texto normalizado de un TextField.
     *
     * @param textField Campo de texto
     * @return Texto recortado o cadena vacía
     */
    private String obtenerTextoNormalizado(TextField textField) {
        if (textField == null || textField.getText() == null) {
            return "";
        }
        return textField.getText().trim();
    }

    /**
     * Comprueba si dos textos coinciden tras normalizarlos.
     *
     * @param texto1 Primer texto
     * @param texto2 Segundo texto
     * @return true si coinciden
     */
    private boolean coincidenTextos(String texto1, String texto2) {
        return normalizarTexto(texto1).equals(normalizarTexto(texto2));
    }

    /**
     * Normaliza un texto para comparación.
     *
     * @param texto Texto a normalizar
     * @return Texto normalizado
     */
    private String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }

        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replace(".", "")
                .replace(",", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Valida textos geográficos como ciudad o provincia.
     *
     * @param texto Texto a validar
     * @return true si el texto es válido
     */
    private boolean esTextoGeograficoValido(String texto) {
        return texto.matches("[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s\\-]+");
    }

    /**
     * Valida el número de la dirección.
     *
     * @param numero Número a validar
     * @return true si el número es válido
     */
    private boolean esNumeroDireccionValido(String numero) {
        return numero.matches("\\d+[A-Za-z]?");
    }

    /**
     * Convierte el contenido de un TextField a double.
     *
     * @param campo campo de texto
     * @return valor convertido
     * @throws DatosEntradaInvalidosException si el campo no contiene un número válido
     */
    private double convertirADouble(TextField campo) throws DatosEntradaInvalidosException {
        String valor = obtenerTextoNormalizado(campo).replace(",", ".");
        if (valor.isEmpty()) {
            throw new DatosEntradaInvalidosException("Faltan datos calculados de la instalación");
        }

        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            throw new DatosEntradaInvalidosException("Los datos calculados de la instalación no son válidos");
        }
    }

    /**
     * Convierte el contenido de un TextField a integer.
     *
     * @param campo campo de texto
     * @return valor convertido
     * @throws DatosEntradaInvalidosException si el campo no contiene un entero válido
     */
    private int convertirAInteger(TextField campo) throws DatosEntradaInvalidosException {
        String valor = obtenerTextoNormalizado(campo);
        if (valor.isEmpty()) {
            throw new DatosEntradaInvalidosException("Faltan datos calculados de la instalación");
        }

        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new DatosEntradaInvalidosException("Los datos calculados de la instalación no son válidos");
        }
    }

    /**
     * Limpia los campos de resultados calculados.
     */
    private void limpiarResultadosCalculados() {
        txtHorasSol.clear();
        txtArea.clear();
        txtMaxPaneles.clear();
        txtPanelesNecesarios.clear();
        txtEnergiaPanel.clear();
        txtPresupuesto.clear();
    }
}