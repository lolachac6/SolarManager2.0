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
 * <p>Gestiona la navegación entre pantallas, la carga dinámica de comerciales,
 * la compilación de informes JasperReports y la generación de vistas previas
 * dentro del panel JavaFX.</p>
 *
 * <p>Incluye informes de:</p>
 * <ul>
 *   <li>Clientes por comercial por mes</li>
 *   <li>Presupuestos generados/aprobados</li>
 *   <li>Ventas del mes actual</li>
 *   <li>Relación ventas del comercial vs instalaciones totales</li>
 * </ul>
 */
public class PantallaInformesController implements Initializable {

    @FXML private Button btnPresupuestos;
    @FXML private StackPane panelPreview;   
    @FXML private ComboBox<String> comboComerciales;
    @FXML private Button btnInformeComercial;
    @FXML private Button btnVentasMes;
    @FXML private Button btnRatioVentas;

    private MongoDatabase database;
    

    private Map<String, String> mapaComerciales = new HashMap<>();
    
    /**
    * Inicializa la pantalla cargando la conexión a MongoDB,
    * los comerciales disponibles y compilando los informes Jasper.
    *
    * @param url ubicación del FXML
    * @param rb recursos internacionales
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            database = DB.MongoConnection.conectar();  // ← ESTA ES LA CORRECTA
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
    * <p>Cierra la ventana actual y abre una nueva maximizada.</p>
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
    @FXML private void volverInicio(ActionEvent e) {
        cambiarPantalla((Node)e.getSource(), "/components/pantallas/erp/plantillaGeneral/PlantillaGeneral.fxml");
    }
    
    /**
    * Navega a la pantalla de clientes.
    *
    * @param e evento de acción
    */
    @FXML private void irClientes(ActionEvent e) {
        cambiarPantalla((Node)e.getSource(), "/components/pantallas/erp/pantallaClientes/PantallaClientes.fxml");
    }
    
    /**
    * Navega a la pantalla de comerciales.
    *
    * @param e evento de acción
    */
    @FXML private void irComerciales(ActionEvent e) {
        cambiarPantalla((Node)e.getSource(), "/components/pantallas/erp/pantallaComerciales/PantallaComerciales.fxml");
    }
    
    /**
    * Navega a la pantalla de proveedores.
    *
    * @param e evento de acción
    */
    @FXML private void irProveedores(ActionEvent e) {
        cambiarPantalla((Node)e.getSource(), "/components/pantallas/erp/pantallaProveedor/PantallaProveedor.fxml");
    }
    
    /**
    * Navega a la pantalla de stock.
    *
    * @param e evento de acción
    */
    @FXML private void irStock(ActionEvent e) {
        cambiarPantalla((Node)e.getSource(), "/components/pantallas/erp/pantallaStock/PantallaStock.fxml");
    }
    
     /**
     * Navega a la pantalla de presupuestos.
     *
     * @param e evento de acción
     */
    @FXML private void irPresupuestos(ActionEvent e) {
        cambiarPantalla((Node)e.getSource(), "/components/pantallas/erp/pantallaPresupuesto/PantallaPresupuesto.fxml");
    }
    
    /**
    * Navega a la pantalla de instalaciones.
    *
    * @param e evento de acción
    */
    @FXML private void irInstalaciones(javafx.event.ActionEvent e) {
        cambiarPantalla((Node) e.getSource(),"/components/pantallas/erp/pantallaInstalaciones/PantallaInstalaciones.fxml");
    }
    
    /**
    * Recarga la pantalla de informes.
    *
    * @param e evento de acción
    */
    @FXML private void irInformes(ActionEvent e) {
        cambiarPantalla((Node)e.getSource(), "/components/pantallas/erp/pantallaInformes/PantallaInformes.fxml");
    }

    /**
    * Genera un informe Jasper de forma asíncrona y muestra su vista previa.
    *
    * <p>El informe se carga en segundo plano para evitar bloquear la interfaz.
    * Una vez generado, se convierte en imagen y se muestra en el panel central.</p>
    *
    * @param rutaJasper ruta del archivo .jasper a cargar
    * @param supplier proveedor del JRDataSource necesario para el informe
    */
    private interface DataSourceSupplier { JRDataSource get() throws Exception; }

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
    * <p>Si algún archivo JRXML no se encuentra, se muestra un aviso en consola.</p>
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
    * <p>Los comerciales se identifican por su nombre real.</p>
    */
    private void cargarComerciales() {

        MongoCollection<Document> col = database.getCollection("Comerciales");

        List<Comercial> lista = new ArrayList<>();

        for (Document doc : col.find()) {
            Comercial c = new Comercial();
            c.setId(doc.getObjectId("_id").toString());
            c.setNombre(doc.getString("nombre"));
            lista.add(c);
        }

        mapaComerciales.clear();
        comboComerciales.getItems().clear();

        for (Comercial c : lista) {
            mapaComerciales.put(c.getNombre(), c.getId());
            comboComerciales.getItems().add(c.getNombre());
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

        // Obtener el ID real del comercial
        String idComercial = mapaComerciales.get(nombre);

        new Thread(() -> {
            try {
                List<Map<String, Object>> datos = obtenerClientesPorComercialPorMes(idComercial);

                boolean hayDatos = datos.stream()
                        .anyMatch(m -> ((Number)m.get("total")).doubleValue() > 0);

                if (!hayDatos) {
                    Platform.runLater(() -> mostrarAlerta("Este comercial aún no tiene clientes asignados", Alert.AlertType.INFORMATION));
                    return;
                }

                Map<String, Object> params = new HashMap<>();
                params.put("COMERCIAL", nombre); // mostramos el nombre en el informe

                JasperReport report = JasperCompileManager.compileReport(
                        getClass().getResourceAsStream("clientesPorComercial_Mensual_Barras_Ordenado.jrxml")
                );

                JRDataSource dataSource =
                    new JRMapCollectionDataSource((Collection<Map<String,?>>)(Collection<?>) datos);

                JasperPrint print = JasperFillManager.fillReport(report, params, dataSource);

                Platform.runLater(() -> generarImagenAjustada(print));

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> AlertasSolarManager.errorGenerico("Error generando informe: " + e.getMessage()));
            }
        }).start();
    }


    
    /**
    * Obtiene el número de clientes asignados a un comercial por cada mes del año.
    *
    * <p>La fecha se obtiene a partir del timestamp del ObjectId del documento.</p>
    *
    * @param comercial nombre del comercial
    * @return lista de mapas con mes, orden y total de clientes
    */
    public List<Map<String, Object>> obtenerClientesPorComercialPorMes(String idcomercial) {

        MongoCollection<Document> col = database.getCollection("Clientes");

        Map<Integer, Integer> conteo = new HashMap<>();
        for (int i = 1; i <= 12; i++) conteo.put(i, 0);

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

            if (total <= 0) continue;

            double porc = (total * 100.0) / sumaTotal;
            String porcentaje = String.format("%.0f%%", porc);

            Map<String, Object> fila = new HashMap<>();
            fila.put("estado", estado);
            fila.put("total", total);
            fila.put("porcentaje", porcentaje);

            lista.add(fila);
        }

        if (lista.isEmpty()) return null;

        return new JRMapCollectionDataSource(
                (Collection<Map<String,?>>)(Collection<?>) lista
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
            mostrarAlerta("Selecciona un comercial",Alert.AlertType.INFORMATION);
            return;
        }

        generarInformeVentasMes(comercial);
    }
    
    /**
    * Genera el informe Jasper de ventas del mes actual.
    *
    * @param comercial nombre del comercial
    */
    private void generarInformeVentasMes(String comercial) {
        try {
            List<Map<String, Object>> datos = obtenerVentasPorMes(comercial);

            if (datos.isEmpty()) {
                mostrarAlerta("No hay ventas este mes para este comercial", Alert.AlertType.INFORMATION);
                return;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("COMERCIAL", comercial);

            JasperReport report = JasperCompileManager.compileReport(
                    getClass().getResourceAsStream("ventasMesActual.jrxml")
            );

            JasperPrint print = JasperFillManager.fillReport(
                    report,
                    params,
                    new JRMapCollectionDataSource((Collection<Map<String,?>>)(Collection<?>) datos)
            );

            generarImagenAjustada(print);

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
        for (int i = 1; i <= 12; i++) conteo.put(i, 0);

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

            if (fecha == null)
                continue;

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
    * Genera el informe comparativo entre ventas del comercial
    * y ventas totales de la empresa.
    *
    * @param event evento de acción
    */
    @FXML
    private void onVentasVsEmpresa(ActionEvent event) throws Exception {

        String comercial = comboComerciales.getValue();

        if (comercial == null || comercial.isEmpty()) {
            mostrarAlerta("Selecciona un comercial",Alert.AlertType.INFORMATION);
        }

        JasperPrint print = generarInformeRelacionVentas(comercial);

        generarImagenAjustada(print);
    }
    
    /**
    * Construye el JasperPrint del informe de relación ventas comercial vs empresa.
    *
    * @param comercial nombre del comercial
    * @return JasperPrint generado
    */
    private JasperPrint generarInformeRelacionVentas(String comercial) {
    try {

        MongoCollection<Document> col = database.getCollection("Presupuestos");

        // 1. Datos
        long ventasComercial = col.countDocuments(
                Filters.and(
                        Filters.eq("idComercial", comercial.trim()),
                        Filters.regex("estado", "^facturado$", "i")
                )
        );

        long instalacionesTotales = col.countDocuments(
                Filters.regex("estado", "^facturado$", "i")
        );

        boolean hayDatos = instalacionesTotales > 0;

        // 2. Porcentajes
        long total = instalacionesTotales == 0 ? 1 : instalacionesTotales;

        int pctComercial = (int) Math.round(ventasComercial * 100.0 / total);
        int pctInstalaciones = 100 - pctComercial;

        // 3. Lista para Jasper
        List<Map<String, Object>> lista = new ArrayList<>();

        // Ventas del Comercial
        {
            Map<String, Object> fila = new HashMap<>();
            fila.put("label", "Ventas del Comercial");
            fila.put("valor", ventasComercial);
            fila.put("porcentaje", pctComercial + "%");
            lista.add(fila);
        }

        // Instalaciones
        {
            Map<String, Object> fila = new HashMap<>();
            fila.put("label", "Instalaciones Totales");
            fila.put("valor", instalacionesTotales - ventasComercial);
            fila.put("porcentaje", "");
            lista.add(fila);
        }

        // 4. Parámetros
        Map<String, Object> params = new HashMap<>();
        params.put("COMERCIAL", comercial);
        params.put("HAY_DATOS", hayDatos);

        // 5. Cargar informe
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
    * Convierte la primera página del informe JasperPrint en una imagen
    * y la ajusta automáticamente al tamaño del panel de vista previa.
    *
    * @param print informe Jasper ya generado
    */
    private void generarImagenAjustada(JasperPrint print) {
        try {
            panelPreview.getChildren().clear();

            BufferedImage pageImage = (BufferedImage) JasperPrintManager.printPageToImage(print, 0, 2f);
            Image fxImage = SwingFXUtils.toFXImage(pageImage, null);

            ImageView imageView = new ImageView(fxImage);

            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(panelPreview.widthProperty());
            imageView.fitHeightProperty().bind(panelPreview.heightProperty());
            // ⭐ AJUSTE AUTOMÁTICO AL PANEL
            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(panelPreview.widthProperty());
            imageView.fitHeightProperty().bind(panelPreview.heightProperty());

            panelPreview.getChildren().add(imageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}