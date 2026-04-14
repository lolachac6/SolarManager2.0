package components.pantallas.erp.pantallaInformes;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JRException;

import java.io.InputStream;

/**
 * Utilidad encargada de compilar los informes JasperReports (.jrxml → .jasper)
 * ubicados dentro del classpath del proyecto.
 *
 * <p>Esta clase:
 * <ul>
 *   <li>Define una lista fija de informes JRXML que deben compilarse.</li>
 *   <li>Accede a los recursos dentro del classpath mediante {@code getResourceAsStream()}.</li>
 *   <li>Genera los archivos .jasper en la misma carpeta donde se encuentran los JRXML.</li>
 *   <li>Proporciona métodos para compilar todos los informes o uno individual.</li>
 * </ul>
 *
 * <p>Se utiliza normalmente al iniciar la aplicación para asegurar que los informes
 * estén compilados y listos para ser utilizados por JasperReports.
 */
public class ReportCompiler {

     /**
     * Ruta base dentro del classpath donde se encuentran los archivos JRXML.
     */
    private static final String BASE_PATH =
            "/components/pantallas/erp/pantallaInformes";

    /**
     * Lista fija de informes JRXML que deben compilarse automáticamente.
     */
    private static final String[] INFORMES = {
            "clientesPorComercial.jrxml",
            "ventasPorComercial.jrxml",
            "instalacionesPorComercial.jrxml",
            "presupuestosGeneradosAprobados.jrxml",
            "stockMaterial.jrxml"
    };

    /**
     * Compila todos los informes definidos en {@link #INFORMES}.
     *
     * <p>Para cada archivo JRXML:
     * <ul>
     *   <li>Verifica que exista dentro del classpath.</li>
     *   <li>Lo compila a formato .jasper.</li>
     *   <li>Guarda el archivo compilado en la misma carpeta del recurso.</li>
     * </ul>
     *
     * <p>Este método es seguro de ejecutar múltiples veces, ya que simplemente
     * sobrescribe los .jasper existentes.
     */
    
    public static void compileReports() {
        System.out.println("🔍 Compilando informes Jasper...");

        for (String fileName : INFORMES) {
            compileSingleReport(fileName);
        }

        System.out.println("✅ Compilación finalizada.");
    }

    /**
     * Compila un único archivo JRXML ubicado dentro del classpath.
     *
     * <p>El método:
     * <ul>
     *   <li>Localiza el archivo JRXML mediante {@code getResourceAsStream()}.</li>
     *   <li>Si no existe, muestra un mensaje de error y no intenta compilarlo.</li>
     *   <li>Genera el archivo .jasper correspondiente usando {@link JasperCompileManager}.</li>
     *   <li>Guarda el archivo compilado en la misma carpeta del recurso original.</li>
     * </ul>
     *
     * @param fileName Nombre del archivo JRXML a compilar (por ejemplo: "ventasPorComercial.jrxml").
     */
    
    private static void compileSingleReport(String fileName) {
        try {
            String jrxmlPath = BASE_PATH + fileName;

            InputStream input = ReportCompiler.class.getResourceAsStream(jrxmlPath);

            if (input == null) {
                System.err.println("❌ No se encontró el JRXML en el classpath: " + jrxmlPath);
                return;
            }

            // Compilar a .jasper en la misma carpeta del classpath
            String jasperOutputPath =
                    ReportCompiler.class.getResource(BASE_PATH).getPath()
                            + fileName.replace(".jrxml", ".jasper");

            JasperCompileManager.compileReportToFile(
                    ReportCompiler.class.getResource(jrxmlPath).getPath(),
                    jasperOutputPath
            );

            System.out.println("✔ Compilado: " + fileName);

        } catch (JRException e) {
            System.err.println("❌ Error compilando " + fileName + ": " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
