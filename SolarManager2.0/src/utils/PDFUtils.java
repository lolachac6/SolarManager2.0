package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Utilidad para abrir PDFs almacenados en memoria.
 */
public class PDFUtils {

    /**
     * Abre un PDF desde un array de bytes.
     *
     * @param pdf contenido binario del PDF
     * @throws Exception si ocurre un error
     */
    public static void abrirPDF(byte[] pdf) throws Exception {
        File archivoTemp = File.createTempFile("factura_", ".pdf");

        try (FileOutputStream fos = new FileOutputStream(archivoTemp)) {
            fos.write(pdf);
        }

        Desktop.getDesktop().open(archivoTemp);
    }
}