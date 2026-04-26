package components.pantallas.erp.pantallaInformes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.bson.Document;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import modelo.Comercial;
import org.bson.types.ObjectId;
import utils.AlertasSolarManager;

/**
 * Controlador principal de la pantalla de informes del ERP SolarManager.
 *
 * <p>
 * Gestiona la navegación entre pantallas, la carga dinámica de comerciales, la
 * compilación de informes JasperReports y la generación de vistas previas
 * dentro del panel JavaFX.</p>
 *
 * <p>
 * Incluye informes de:</p>
 * <ul>
 * <li>Clientes por comercial por mes</li>
 * <li>Presupuestos generados/aprobados</li>
 * <li>Ventas del mes actual</li>
 * <li>Relación ventas del comercial vs instalaciones totales</li>
 * </ul>
 */
public class PantallaInformesController implements Initializable {

    @FXML
    private Button btnPresupuestos;
    @FXML
    private StackPane panelPreview;
    @FXML
    private ComboBox<String> comboComerciales;
    @FXML
    private Button btnInformeComercial;
    @FXML
    private Button btnVentasMes;
    @FXML
    private Button btnRatioVentas;

    private MongoDatabase database;

    private Map<String, String> mapaComerciales = new HashMap<>();

    /**
     * Inicializa la pantalla cargando la conexión a MongoDB, los comerciales
     * disponibles y compilando los informes Jasper.
     *
     * @param url ubicación del FXML
     * @param rb recursos internacionales
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            database = DB.MongoConnection.conectar(); 
            cargarComerciales();
        } catch (Exception e) {
            e.printStackTrace();
        }

        compilarInformes();
    }

    /**
     * Muestra una alerta genérica utilizando el sistema de alertas del ERP.
     *
     * @param msg mensaje a mostrar
     * @param tipo tipo de alerta (información, error, advertencia)
     */
    private void mostrarAlerta(String msg, Alert.AlertType tipo) {
        AlertasSolarManager.mostrar(tipo, null, msg);
    }

    /**
     * Cambia la pantalla actual por otra indicada mediante su ruta FXML.
     *
     * <p>
     * Cierra la ventana actual y abre una nueva maximizada.</p>
     *
     * @param nodo nodo que dispara el evento (para obtener el Stage actual)
     * @param rutaFXML ruta del archivo FXML a cargar
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

    /**
     * Navega a la pantalla de inicio del ERP.
     *
     * @param e evento de acción
     */
    @FXML
    private void volverInicio(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }

    /**
     * Navega a la pantalla de clientes.
     *
     * @param e evento de acción
     */
    @FXML
    private void irClientes(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }

    /**
     * Navega a la pantalla de comerciales.
     *
     * @param e evento de acción
     */
    @FXML
    private void irComerciales(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }

    /**
     * Navega a la pantalla de proveedores.
     *
     * @param e evento de acción
     */
    @FXML
    private void irProveedores(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaProveedor/PantallaProveedor.fxml");
    }

    /**
     * Navega a la pantalla de stock.
     *
     * @param e evento de acción
     */
    @FXML
    private void irStock(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }

    /**
     * Navega a la pantalla de presupuestos.
     *
     * @param e evento de acción
     */
    @FXML
    private void irPresupuestos(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }

    /**
     * Navega a la pantalla de instalaciones.
     *
     * @param e evento de acción
     */
    @FXML
    private void irInstalaciones(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml");
    }

    /**
     * Recarga la pantalla de informes.
     *
     * @param e evento de acción
     */
    @FXML
    private void irInformes(ActionEvent e) {
        cambiarPantalla((Node) e.getSource(), "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }

    /**
     * Genera un informe Jasper de forma asíncrona y muestra su vista previa.
     *
     * <p>
     * El informe se carga en segundo plano para evitar bloquear la interfaz.
     * Una vez generado, se convierte en imagen y se muestra en el panel
     * central.</p>
     *
     * @param rutaJasper ruta del archivo .jasper a cargar
     * @param supplier proveedor del JRDataSource necesario para el informe
     */
    private interface DataSourceSupplier {

        JRDataSource get() throws Exception;
    }

    private void generarInformeAsync(String rutaJasper, DataSourceSupplier supplier) {

        Platform.runLater(() -> {
            panelPreview.getChildren().clear();
        });

        new Thread(() -> {
            try {
                JRDataSource ds = supplier.get();

                if (ds == null) {
                    Platform.runLater(() -> {
                        AlertasSolarManager.errorGenerico(" No hay datos para generar el informe.");
                    });
                    return;
                }

                String base = getClass()
                        .getResource("/components/pantallas/erp/pantallaInformes/")
                        .getPath();

                String nombre = rutaJasper.substring(rutaJasper.lastIndexOf("/") + 1);
                String pathJasper = base + nombre;

                JasperReport reporte = (JasperReport) JRLoader.loadObject(new java.io.File(pathJasper));

                JasperPrint print = JasperFillManager.fillReport(reporte, null, ds);

                if (print == null || print.getPages().isEmpty()) {
                    Platform.runLater(() -> {
                        AlertasSolarManager.errorGenerico("El informe no contiene páginas.");
                    });
                    return;
                }

                Platform.runLater(() -> generarImagenAjustada(print));

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlertasSolarManager.errorGenerico("Error al generar el informe: ");
                });
            }
        }).start();
    }

    /**
     * Compila todos los informes JRXML de la pantalla y genera sus
     * correspondientes archivos .jasper en tiempo de ejecución.
     *
     * <p>
     * Si algún archivo JRXML no se encuentra, se muestra un aviso en
     * consola.</p>
     */
    public void compilarInformes() {

        String base = "/components/pantallas/erp/pantallaInformes/";

        String[] informes = {
            "ventasMesActual.jrxml",
            "presupuestosGeneradosAprobados.jrxml",
            "clientesPorComercial_Mensual_Barras_Ordenado.jrxml",
            "ventasComercialVsEmpresa.jrxml"
        };

        for (String inf : informes) {
            try {
                InputStream input = getClass().getResourceAsStream(base + inf);
                if (input == null) {
                    System.err.println("❌ No se encontró: " + base + inf);
                    continue;
                }

                JasperReport reporte = JasperCompileManager.compileReport(input);

                String jasperPath = getClass().getResource(base).getPath()
                        + inf.replace(".jrxml", ".jasper");

                JasperCompileManager.compileReportToFile(
                        getClass().getResource(base + inf).getPath(),
                        jasperPath
                );

                System.out.println("✔ Compilado y guardado: " + jasperPath);

            } catch (Exception e) {
                System.err.println("❌ Error compilando " + inf + ": " + e.getMessage());
            }
        }
    }

    /**
     * Carga la lista de comerciales desde MongoDB y la asigna al ComboBox.
     *
     * <p>
     * Los comerciales se identifican por su nombre real.</p>
     */
    private void cargarComerciales() {

        MongoCollection<Document> col = database.getCollection("Comerciales");

        List<Comercial> lista = new ArrayList<>();

        for (Document doc : col.find()) {
            Comercial c = new Comercial();
            c.setId(doc.getObjectId("_id").toString());
            c.setNombre(doc.getString("nombre"));
            c.setApellidos(doc.getString("apellidos"));
            lista.add(c);
        }

        mapaComerciales.clear();
        comboComerciales.getItems().clear();

        for (Comercial c : lista) {
            String nombreCompleto = c.getNombre() + " " + c.getApellidos();
            mapaComerciales.put(nombreCompleto, c.getId());
            comboComerciales.getItems().add(nombreCompleto);
        }
    }

    /**
     * Genera el informe de clientes por comercial por mes.
     *
     * @param event evento de acción del botón
     */
    @FXML
    private void onInformeComercial(ActionEvent event) {

        String nombre = comboComerciales.getValue();

        if (nombre == null || nombre.isEmpty()) {
            mostrarAlerta("Selecciona un comercial", Alert.AlertType.INFORMATION);
            return;
        }

        String idComercial = mapaComerciales.get(nombre);

        new Thread(() -> {
            try {
                List<Map<String, Object>> datos = obtenerClientesPorComercialPorMes(idComercial);

                boolean hayDatos = datos.stream()
                        .anyMatch(m -> ((Number) m.get("total")).doubleValue() > 0);

                if (!hayDatos) {
                    Platform.runLater(() -> mostrarAlerta("Este comercial aún no tiene clientes asignados", Alert.AlertType.INFORMATION));
                    return;
                }

                Map<String, Object> params = new HashMap<>();
                params.put("COMERCIAL", nombre); 

                JasperReport report = JasperCompileManager.compileReport(
                        getClass().getResourceAsStream("clientesPorComercial_Mensual_Barras_Ordenado.jrxml")
                );

                JRDataSource dataSource
                        = new JRMapCollectionDataSource((Collection<Map<String, ?>>) (Collection<?>) datos);

                JasperPrint print = JasperFillManager.fillReport(report, params, dataSource);

                Platform.runLater(() -> generarImagenAjustada(print));

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> AlertasSolarManager.errorGenerico("Error generando informe: " + e.getMessage()));
            }
        }).start();
    }
    
    /**
    * Obtiene el número de clientes captados por un comercial agrupados por mes.
    *
    * Este método consulta la colección "Clientes" de MongoDB filtrando por el
    * identificador del comercial indicado. Para cada documento encontrado, se
    * obtiene la fecha de creación a partir del {@link ObjectId} del cliente,
    * utilizando su timestamp interno como fecha de alta.
    *
    * A partir de esa fecha se determina el mes correspondiente y se incrementa
    * el contador mensual. Finalmente, se construye una lista de mapas con 12
    * entradas (una por cada mes del año), incluyendo:
    *
    * <ul>
    *   <li><b>mes</b>: nombre del mes en español</li>
    *   <li><b>ordenMes</b>: número del mes (1–12)</li>
    *   <li><b>total</b>: total de clientes captados en ese mes</li>
    * </ul>
    *
    * Esta estructura es compatible con el uso como datasource en JasperReports.
    *
    * @param idcomercial identificador del comercial asignado a los clientes
    * @return una lista de 12 mapas con el total de clientes por mes
    */
    public List<Map<String, Object>> obtenerClientesPorComercialPorMes(String idcomercial) {

        MongoCollection<Document> col = database.getCollection("Clientes");

        Map<Integer, Integer> conteo = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            conteo.put(i, 0);
        }

        List<Document> docs = col.find(
                Filters.eq("idComercialAsignado", idcomercial)
        ).into(new ArrayList<>());

        for (Document doc : docs) {

            ObjectId oid = doc.getObjectId("_id");
            Date fecha = new Date(oid.getTimestamp() * 1000L);

            Calendar cal = Calendar.getInstance();
            cal.setTime(fecha);

            int mes = cal.get(Calendar.MONTH) + 1;

            conteo.put(mes, conteo.get(mes) + 1);
        }

        List<Map<String, Object>> lista = new ArrayList<>();

        String[] meses = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        for (int i = 1; i <= 12; i++) {
            Map<String, Object> fila = new HashMap<>();
            fila.put("mes", meses[i - 1]);
            fila.put("ordenMes", i);
            fila.put("total", conteo.get(i).doubleValue());
            lista.add(fila);
        }

        return lista;
    }

    /**
     * Genera el informe de presupuestos generados y aprobados.
     *
     * @param e evento de acción
     */
    @FXML
    private void onPresupuestos(ActionEvent e) {
        generarInformeAsync(
                "/components/pantallas/erp/pantallaInformes/presupuestosGeneradosAprobados.jasper",
                this::crearDataSourcePresupuestos
        );
    }

    /**
     * Crea el datasource para el informe de presupuestos.
     *
     * @return JRDataSource con los datos agregados por estado
     * @throws Exception si ocurre un error al consultar MongoDB
     */
    public JRDataSource crearDataSourcePresupuestos() throws Exception {

        MongoCollection<Document> col = database.getCollection("Presupuestos");

        Map<String, Integer> conteo = new HashMap<>();

        for (Document doc : col.find()) {

            String estado = doc.getString("estado");

            if (estado == null || estado.trim().isEmpty()) {
                estado = "SIN ESTADO";
            } else {
                estado = estado.trim().toUpperCase();
            }

            conteo.merge(estado, 1, Integer::sum);
        }

        int sumaTotal = conteo.values().stream().mapToInt(i -> i).sum();

        List<Map<String, Object>> lista = new ArrayList<>();

        for (String estado : conteo.keySet()) {

            int total = conteo.get(estado);

            if (total <= 0) {
                continue;
            }

            double porc = (total * 100.0) / sumaTotal;
            String porcentaje = String.format("%.0f%%", porc);

            Map<String, Object> fila = new HashMap<>();
            fila.put("estado", estado);
            fila.put("total", total);
            fila.put("porcentaje", porcentaje);

            lista.add(fila);
        }

        if (lista.isEmpty()) {
            return null;
        }

        return new JRMapCollectionDataSource(
                (Collection<Map<String, ?>>) (Collection<?>) lista
        );
    }

    /**
     * Genera el informe de ventas del mes actual para un comercial.
     *
     * @param event evento de acción
     */
    @FXML
    private void onVentasMes(ActionEvent event) {

        String comercial = comboComerciales.getValue();

        if (comercial == null || comercial.isEmpty()) {
            mostrarAlerta("Selecciona un comercial", Alert.AlertType.INFORMATION);
            return;
        }

        String nombre = comboComerciales.getValue();
        String idComercial = mapaComerciales.get(nombre);
        
        List<Map<String, Object>> datos = obtenerVentasPorMes(idComercial);
        boolean hayVentas = datos.stream()
            .anyMatch(fila -> ((Number) fila.get("total")).intValue() > 0);

        if (!hayVentas) {
            mostrarAlerta("Este comercial no tiene ventas registradas este año", Alert.AlertType.INFORMATION);
            return;
        }


        generarInformeVentasMes(idComercial, nombre);
    }

    /**
    * Genera el informe de ventas mensuales para un comercial específico.
    *
    * Este método obtiene los datos agregados de ventas por mes mediante
    * {@code obtenerVentasPorMes(idComercial)}, prepara los parámetros necesarios
    * para el informe (incluyendo el nombre del comercial) y compila el archivo
    * JRXML {@code ventasMesActual.jrxml}.
    *
    * Una vez compilado, se rellena el informe con los datos proporcionados
    * utilizando un {@link JRMapCollectionDataSource} y se genera un
    * {@link JasperPrint}, que posteriormente se envía al método
    * {@code generarImagenAjustada(print)} para su visualización o exportación.
    *
    * Si ocurre cualquier error durante la compilación o generación del informe,
    * se captura la excepción y se muestra un mensaje de error genérico.
    *
    * @param idComercial      identificador del comercial cuyas ventas se desean consultar
    * @param nombreComercial  nombre del comercial que se mostrará en el informe
    */
    private void generarInformeVentasMes(String idComercial, String nombreComercial) {
        try {
            List<Map<String, Object>> datos = obtenerVentasPorMes(idComercial);
         
            Map<String, Object> params = new HashMap<>();
            params.put("COMERCIAL", nombreComercial);

            JasperReport report = JasperCompileManager.compileReport(
                    getClass().getResourceAsStream("ventasMesActual.jrxml")
            );

            JasperPrint print = JasperFillManager.fillReport(
                    report,
                    params,
                    new JRMapCollectionDataSource((Collection<Map<String, ?>>) (Collection<?>) datos)
            );

            Platform.runLater(() -> generarImagenAjustada(print));

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("Error generando informe: " + e.getMessage());
        }
    }

    /**
     * Obtiene el número de ventas facturadas por mes para un comercial.
     *
     * @param comercial nombre del comercial
     * @return lista de mapas con mes, orden y total de ventas
     */
    public List<Map<String, Object>> obtenerVentasPorMes(String comercial) {

        MongoCollection<Document> col = database.getCollection("Presupuestos");

        Map<Integer, Integer> conteo = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            conteo.put(i, 0);
        }

        List<Document> docs = col.find(
                Filters.and(
                        Filters.eq("idComercial", comercial.trim()),
                        Filters.regex("estado", "^facturado$", "i")
                )
        ).into(new ArrayList<>());

        for (Document doc : docs) {

            Date fecha = doc.getDate("fechaFactura");

            if (fecha == null) {
                String fechaStr = doc.getString("fechaCreacion");
                if (fechaStr != null) {
                    try {
                        fecha = javax.xml.bind.DatatypeConverter.parseDateTime(fechaStr).getTime();
                    } catch (Exception e) {
                        fecha = null;
                    }
                }
            }

            if (fecha == null) {
                continue;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(fecha);

            int mes = cal.get(Calendar.MONTH) + 1;
            conteo.put(mes, conteo.get(mes) + 1);
        }

        List<Map<String, Object>> lista = new ArrayList<>();

        String[] meses = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        for (int i = 1; i <= 12; i++) {
            Map<String, Object> fila = new HashMap<>();
            fila.put("mes", meses[i - 1]);
            fila.put("ordenMes", i);
            fila.put("total", conteo.get(i));
            lista.add(fila);
        }

        return lista;
    }

    /**
     * Genera el informe comparativo entre ventas del comercial y ventas totales
     * de la empresa.
     *
     * @param event evento de acción
     */
    @FXML
    private void onVentasVsEmpresa(ActionEvent event) throws Exception {

        String nombre = comboComerciales.getValue();       
        String idComercial = mapaComerciales.get(nombre);

        if (nombre == null || nombre.isEmpty()) {
            mostrarAlerta("Selecciona un comercial", Alert.AlertType.INFORMATION);
            return;
        }

        JasperPrint print = generarInformeRelacionVentas(idComercial, nombre);

        generarImagenAjustada(print);
    }

    /**
    * Genera el informe comparativo entre las ventas realizadas por un comercial
    * y el total de instalaciones facturadas por la empresa.
    *
    * El método consulta la colección "Presupuestos" de MongoDB para obtener:
    * <ul>
    *   <li>El número de instalaciones facturadas asignadas al comercial.</li>
    *   <li>El total de instalaciones facturadas por la empresa.</li>
    * </ul>
    *
    * A partir de estos valores se calculan los porcentajes correspondientes y se
    * construye una lista de mapas compatible con {@link JRMapCollectionDataSource},
    * que alimentará el informe JasperReports.
    *
    * También se preparan los parámetros del informe, incluyendo el nombre del
    * comercial y un indicador booleano que determina si existen datos suficientes
    * para mostrar el gráfico.
    *
    * Finalmente, el método carga el archivo compilado
    * {@code ventasComercialVsEmpresa.jasper}, rellena el informe y devuelve el
    * {@link JasperPrint} resultante. En caso de error, se captura la excepción y
    * se devuelve {@code null}.
    *
    * @param idComercial      identificador del comercial cuyas ventas se analizarán
    * @param nombreComercial  nombre del comercial que se mostrará en el informe
    * @return un objeto {@link JasperPrint} listo para visualizar o exportar,
    *         o {@code null} si ocurre un error durante el proceso
    */
    private JasperPrint generarInformeRelacionVentas(String idComercial, String nombreComercial) {
    try {

        MongoCollection<Document> col = database.getCollection("Presupuestos");
       
        long ventasComercial = col.countDocuments(
                Filters.and(
                        Filters.eq("idComercial", idComercial.trim()),
                        Filters.regex("estado", "^facturado$", "i")
                )
        );

            long instalacionesTotales = col.countDocuments(
                    Filters.regex("estado", "^facturado$", "i")
            );

            boolean hayDatos = instalacionesTotales > 0;

        long total = instalacionesTotales == 0 ? 1 : instalacionesTotales;

            int pctComercial = (int) Math.round(ventasComercial * 100.0 / total);
            int pctInstalaciones = 100 - pctComercial;

        List<Map<String, Object>> lista = new ArrayList<>();

        {
            Map<String, Object> fila = new HashMap<>();
            fila.put("label", "Ventas del Comercial");
            fila.put("valor", ventasComercial);
            fila.put("porcentaje", pctComercial + "%");
            lista.add(fila);
        }

        {
            Map<String, Object> fila = new HashMap<>();
            fila.put("label", "Instalaciones Totales");
            fila.put("valor", instalacionesTotales - ventasComercial);
            fila.put("porcentaje", "");
            lista.add(fila);
        }

        Map<String, Object> params = new HashMap<>();
        params.put("COMERCIAL", nombreComercial);
        params.put("HAY_DATOS", hayDatos);

        InputStream jasperStream = getClass().getResourceAsStream(
                "/components/pantallas/erp/pantallaInformes/ventasComercialVsEmpresa.jasper"
        );

            JRDataSource dataSource = new JRMapCollectionDataSource((Collection) lista);

            return JasperFillManager.fillReport(jasperStream, params, dataSource);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convierte la primera página del informe JasperPrint en una imagen y la
     * ajusta automáticamente al tamaño del panel de vista previa.
     *
     * @param print informe Jasper ya generado
     */
    private void generarImagenAjustada(JasperPrint print) {
        try {
            if (print == null || print.getPages().isEmpty()) {
                AlertasSolarManager.warning(
                        "Informe vacío",
                        "No hay páginas para mostrar en la vista previa."
                );
                return;
            }

            panelPreview.getChildren().clear();

            BufferedImage pageImage = (BufferedImage) JasperPrintManager.printPageToImage(print, 0, 2f);
            Image fxImage = SwingFXUtils.toFXImage(pageImage, null);

            ImageView imageView = new ImageView(fxImage);

            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(panelPreview.widthProperty());
            imageView.fitHeightProperty().bind(panelPreview.heightProperty());

            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(panelPreview.widthProperty());
            imageView.fitHeightProperty().bind(panelPreview.heightProperty());

            imageView.fitWidthProperty().bind(panelPreview.widthProperty().subtract(20));
            imageView.fitHeightProperty().bind(panelPreview.heightProperty().subtract(20));

            panelPreview.getChildren().add(imageView);

        } catch (Exception e) {
            e.printStackTrace();
            AlertasSolarManager.errorGenerico("No se pudo mostrar la vista previa del informe.");
        }
    }
}
