package pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import modelo.Cliente;
import modelo.Direccion;
import modelo.Factura;
import modelo.InstalacionFotovoltaica;
import modelo.LineaPresupuesto;
import modelo.Presupuesto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * Genera el documento PDF de una factura a partir de los datos de la factura,
 * el presupuesto y el cliente asociado.
 *
 * @author ivang
 */
public class GeneradorFacturaPDF {

    private static final float ANCHO_PAGINA = PDRectangle.A4.getWidth();
    private static final float ALTO_PAGINA = PDRectangle.A4.getHeight();

    private static final float MARGEN_IZQUIERDO = 50f;
    private static final float MARGEN_DERECHO = 50f;
    private static final float ANCHO_UTIL = ANCHO_PAGINA - MARGEN_IZQUIERDO - MARGEN_DERECHO;
    private static final float SEPARACION_CUADROS = 1f;
    private static final float PADDING_X = 8f;
    private static final float PADDING_Y = 8f;
    private static final float GROSOR_BORDE = 0.6f;

    private static final PDType1Font FUENTE_TITULO =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    private static final PDType1Font FUENTE_NORMAL =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    private static final PDType1Font FUENTE_NEGRITA =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    private static final PDType1Font FUENTE_CURSIVA =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

    /**
     * Genera el PDF binario de una factura.
     *
     * @param factura factura a representar
     * @param presupuesto presupuesto origen
     * @param cliente cliente asociado al presupuesto
     * @return contenido PDF en formato binario
     * @throws IOException si ocurre un error al generar el documento
     */
    public byte[] generarPDF(Factura factura, Presupuesto presupuesto, Cliente cliente) throws IOException {
        PDDocument documento = new PDDocument();
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        try {
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);

            PDPageContentStream contenido = new PDPageContentStream(documento, pagina);
            contenido.setLineWidth(GROSOR_BORDE);

            escribirCabeceraSuperior(contenido, documento);
            escribirTituloPrincipal(contenido);

            float yFactura = 615f;
            float altoFactura = 72f;

            float yCuadrosMedios = 450f;
            float altoCliente = 150f;
            float altoInstalacion = 150f;

            float yConceptos = 185f;
            float altoConceptos = 264f;

            float yTotales = 110f;
            float altoTotales = 74f;

            escribirSeccionFactura(contenido, factura, MARGEN_IZQUIERDO, yFactura, ANCHO_UTIL, altoFactura);
            escribirSeccionCliente(contenido, cliente, MARGEN_IZQUIERDO, yCuadrosMedios, (ANCHO_UTIL - SEPARACION_CUADROS) / 2f, altoCliente);
            escribirSeccionInstalacion(contenido, presupuesto.getInstalacion(),
                    MARGEN_IZQUIERDO + (ANCHO_UTIL - SEPARACION_CUADROS) / 2f + SEPARACION_CUADROS,
                    yCuadrosMedios,
                    (ANCHO_UTIL - SEPARACION_CUADROS) / 2f,
                    altoInstalacion);
            escribirSeccionConceptos(contenido, presupuesto.getLineas(), MARGEN_IZQUIERDO, yConceptos, ANCHO_UTIL, altoConceptos);
            escribirSeccionTotales(contenido, factura, MARGEN_IZQUIERDO, yTotales, ANCHO_UTIL, altoTotales);
            escribirPiePagina(contenido);

            contenido.close();

            documento.save(salida);
            return salida.toByteArray();
        } finally {
            documento.close();
            salida.close();
        }
    }

    /**
     * Escribe la cabecera superior con el logo y los datos de la empresa.
     *
     * @param contenido flujo de contenido del PDF
     * @param documento documento PDF
     * @throws IOException si ocurre un error de escritura
     */
    private void escribirCabeceraSuperior(PDPageContentStream contenido, PDDocument documento) throws IOException {
        dibujarLogo(contenido, documento);

        String textoEmpresa = "DATOS DE LA EMPRESA FOTOVOLTAICA";
        float tamano = 11f;
        float anchoTexto = FUENTE_NEGRITA.getStringWidth(textoEmpresa) / 1000f * tamano;
        float x = ANCHO_PAGINA - MARGEN_DERECHO - 2f - anchoTexto;

        escribirLinea(contenido, textoEmpresa, x, 795f, FUENTE_NEGRITA, tamano);
    }

    /**
     * Escribe el título principal del documento.
     *
     * @param contenido flujo de contenido del PDF
     * @throws IOException si ocurre un error de escritura
     */
    private void escribirTituloPrincipal(PDPageContentStream contenido) throws IOException {
        escribirTextoCentrado(
                contenido,
                "FACTURA INSTALACIÓN FOTOVOLTAICA",
                FUENTE_TITULO,
                18f,
                720f
        );
    }

    /**
     * Dibuja el logo de Solar Manager en la parte superior izquierda del documento.
     *
     * @param contenido flujo de contenido del PDF
     * @param documento documento PDF
     * @throws IOException si ocurre un error al cargar o dibujar la imagen
     */
    private void dibujarLogo(PDPageContentStream contenido, PDDocument documento) throws IOException {
        InputStream is = getClass().getResourceAsStream("/assets/iconos/solar_manager_logo.jpg");

        if (is == null) {
            return;
        }

        byte[] imagenBytes = leerBytes(is);
        PDImageXObject imagen = PDImageXObject.createFromByteArray(documento, imagenBytes, "solar_manager_logo");

        float anchoLogo = 80f;
        float altoLogo = 55f;
        float x = 50f;
        float y = ALTO_PAGINA - 72f;

        contenido.drawImage(imagen, x, y, anchoLogo, altoLogo);
    }

    /**
     * Escribe la sección de datos de la factura dentro de un cuadro.
     *
     * @param contenido flujo de contenido del PDF
     * @param factura factura a representar
     * @param x posición horizontal del cuadro
     * @param y posición inferior del cuadro
     * @param ancho ancho del cuadro
     * @param alto alto del cuadro
     * @throws IOException si ocurre un error de escritura
     */
    private void escribirSeccionFactura(PDPageContentStream contenido, Factura factura,
                                        float x, float y, float ancho, float alto) throws IOException {
        dibujarCuadro(contenido, x, y, ancho, alto);
        escribirTituloSeccion(contenido, "Datos de la factura", x, y, alto);

        float yTexto = y + alto - 34f;
        float xEtiqueta = x + PADDING_X;
        float xValor = x + 130f;

        escribirLinea(contenido, "Número de factura:", xEtiqueta, yTexto, FUENTE_NEGRITA, 10f);
        escribirLinea(contenido, valorSeguro(factura.getNumeroFactura()), xValor, yTexto, FUENTE_NORMAL, 10f);

        yTexto -= 16f;
        escribirLinea(contenido, "Fecha de emisión:", xEtiqueta, yTexto, FUENTE_NEGRITA, 10f);
        escribirLinea(contenido, formatearFecha(factura.getFechaEmision()), xValor, yTexto, FUENTE_NORMAL, 10f);

        yTexto -= 16f;
        escribirLinea(contenido, "ID presupuesto:", xEtiqueta, yTexto, FUENTE_NEGRITA, 10f);
        escribirLinea(contenido, recortarTexto(valorSeguro(factura.getIdPresupuesto()), 48), xValor, yTexto, FUENTE_NORMAL, 10f);
    }

    /**
     * Escribe la sección de datos del cliente dentro de un cuadro.
     *
     * @param contenido flujo de contenido del PDF
     * @param cliente cliente asociado
     * @param x posición horizontal del cuadro
     * @param y posición inferior del cuadro
     * @param ancho ancho del cuadro
     * @param alto alto del cuadro
     * @throws IOException si ocurre un error de escritura
     */
    private void escribirSeccionCliente(PDPageContentStream contenido, Cliente cliente,
                                        float x, float y, float ancho, float alto) throws IOException {
        dibujarCuadro(contenido, x, y, ancho, alto);
        escribirTituloSeccion(contenido, "Datos del cliente", x, y, alto);

        float yTexto = y + alto - 34f;
        float salto = 15f;
        float tamano = 9f;
        float xTexto = x + PADDING_X;
        int max = 34;

        escribirLinea(contenido, recortarTexto("Nombre: " + construirNombreCliente(cliente), max), xTexto, yTexto, FUENTE_NORMAL, tamano);
        yTexto -= salto;
        escribirLinea(contenido, recortarTexto("Tipo cliente: " + valorSeguro(cliente != null && cliente.getTipoCliente() != null ? cliente.getTipoCliente().name() : ""), max), xTexto, yTexto, FUENTE_NORMAL, tamano);
        yTexto -= salto;
        escribirLinea(contenido, recortarTexto("DNI: " + valorSeguro(cliente != null ? cliente.getDni() : ""), max), xTexto, yTexto, FUENTE_NORMAL, tamano);
        yTexto -= salto;
        escribirLinea(contenido, recortarTexto("CIF: " + valorSeguro(cliente != null ? cliente.getCif() : ""), max), xTexto, yTexto, FUENTE_NORMAL, tamano);
        yTexto -= salto;
        escribirLinea(contenido, recortarTexto("Teléfono: " + valorSeguro(cliente != null ? cliente.getTelefono() : ""), max), xTexto, yTexto, FUENTE_NORMAL, tamano);
        yTexto -= salto;
        escribirLinea(contenido, recortarTexto("Email: " + valorSeguro(cliente != null ? cliente.getEmail() : ""), max), xTexto, yTexto, FUENTE_NORMAL, tamano);
        yTexto -= salto;
        escribirLinea(contenido, recortarTexto("Dirección: " + construirDireccion(cliente != null ? cliente.getDireccion() : null), max), xTexto, yTexto, FUENTE_NORMAL, tamano);
    }

    /**
     * Escribe la sección de datos de la instalación dentro de un cuadro.
     *
     * @param contenido flujo de contenido del PDF
     * @param instalacion instalación asociada al presupuesto
     * @param x posición horizontal del cuadro
     * @param y posición inferior del cuadro
     * @param ancho ancho del cuadro
     * @param alto alto del cuadro
     * @throws IOException si ocurre un error de escritura
     */
    private void escribirSeccionInstalacion(PDPageContentStream contenido,
                                            InstalacionFotovoltaica instalacion,
                                            float x, float y, float ancho, float alto) throws IOException {
        dibujarCuadro(contenido, x, y, ancho, alto);
        escribirTituloSeccion(contenido, "Datos de la instalación", x, y, alto);

        float yTexto = y + alto - 34f;
        float salto = 15f;
        float tamano = 9f;
        float xTexto = x + PADDING_X;
        int max = 34;

        if (instalacion == null) {
            escribirLinea(contenido, "No hay datos de instalación.", xTexto, yTexto, FUENTE_NORMAL, tamano);
            return;
        }

        escribirLinea(contenido, recortarTexto("Potencia: " + formatearNumero(instalacion.getPotenciaInstalada()) + " kW", max), xTexto, yTexto, FUENTE_NORMAL, tamano);
        yTexto -= salto;
        escribirLinea(contenido, recortarTexto("Paneles: " + instalacion.getNumeroPaneles(), max), xTexto, yTexto, FUENTE_NORMAL, tamano);
        yTexto -= salto;
        escribirLinea(contenido, recortarTexto("Producción: " + formatearNumero(instalacion.getProduccionEstimada()) + " kWh", max), xTexto, yTexto, FUENTE_NORMAL, tamano);
        yTexto -= salto;
        escribirLinea(contenido, recortarTexto("Ahorro: " + formatearNumero(instalacion.getAhorroEstimado()) + " €", max), xTexto, yTexto, FUENTE_NORMAL, tamano);
        yTexto -= salto;
        escribirLinea(contenido, recortarTexto("Inversor: " + valorSeguro(instalacion.getInversor()), max), xTexto, yTexto, FUENTE_NORMAL, tamano);
        yTexto -= salto;
        escribirLinea(contenido, recortarTexto("Batería: " + (instalacion.getBateria() ? "Sí" : "No"), max), xTexto, yTexto, FUENTE_NORMAL, tamano);
        yTexto -= salto;
        escribirLinea(contenido, recortarTexto("Dirección: " + construirDireccion(instalacion.getDireccion()), max), xTexto, yTexto, FUENTE_NORMAL, tamano);
    }

    /**
     * Escribe la sección de conceptos facturados dentro de un cuadro.
     *
     * @param contenido flujo de contenido del PDF
     * @param lineas líneas del presupuesto
     * @param x posición horizontal del cuadro
     * @param y posición inferior del cuadro
     * @param ancho ancho del cuadro
     * @param alto alto del cuadro
     * @throws IOException si ocurre un error de escritura
     */
    private void escribirSeccionConceptos(PDPageContentStream contenido, List<LineaPresupuesto> lineas,
                                          float x, float y, float ancho, float alto) throws IOException {
        dibujarCuadro(contenido, x, y, ancho, alto);
        escribirTituloSeccion(contenido, "Conceptos facturados", x, y, alto);

        float xConcepto = x + PADDING_X;
        float xCantidad = x + 300f;
        float xPrecio = x + 370f;
        float xTotal = x + 452f;
        float yCabecera = y + alto - 36f;
        float tamanoCabecera = 9f;
        float tamanoFila = 9f;
        float altoFila = 18f;

        contenido.moveTo(x + PADDING_X, yCabecera - 6f);
        contenido.lineTo(x + ancho - PADDING_X, yCabecera - 6f);
        contenido.stroke();

        escribirLinea(contenido, "Concepto", xConcepto, yCabecera, FUENTE_NEGRITA, tamanoCabecera);
        escribirLinea(contenido, "Cantidad", xCantidad, yCabecera, FUENTE_NEGRITA, tamanoCabecera);
        escribirLinea(contenido, "P. Unitario", xPrecio, yCabecera, FUENTE_NEGRITA, tamanoCabecera);
        escribirLinea(contenido, "Total", xTotal, yCabecera, FUENTE_NEGRITA, tamanoCabecera);

        float yFila = yCabecera - 22f;

        if (lineas == null || lineas.isEmpty()) {
            escribirLinea(contenido, "No hay líneas de presupuesto.", xConcepto, yFila, FUENTE_NORMAL, tamanoFila);
            return;
        }

        int maxFilas = 9;
        int totalFilas = Math.min(lineas.size(), maxFilas);

        for (int i = 0; i < totalFilas; i++) {
            LineaPresupuesto linea = lineas.get(i);

            contenido.moveTo(x + PADDING_X, yFila - 5f);
            contenido.lineTo(x + ancho - PADDING_X, yFila - 5f);
            contenido.stroke();

            escribirLinea(contenido, recortarTexto(valorSeguro(linea.getNombreProducto()), 33), xConcepto, yFila, FUENTE_NORMAL, tamanoFila);
            escribirLinea(contenido, String.valueOf(linea.getCantidad()), xCantidad, yFila, FUENTE_NORMAL, tamanoFila);
            escribirLinea(contenido, formatearNumero(linea.getPrecioUnitario()) + " €", xPrecio, yFila, FUENTE_NORMAL, tamanoFila);
            escribirLinea(contenido, formatearNumero(linea.getTotalLinea()) + " €", xTotal, yFila, FUENTE_NORMAL, tamanoFila);

            yFila -= altoFila;
        }
    }

    /**
     * Escribe la sección de totales dentro de un cuadro.
     *
     * @param contenido flujo de contenido del PDF
     * @param factura factura a representar
     * @param x posición horizontal del cuadro
     * @param y posición inferior del cuadro
     * @param ancho ancho del cuadro
     * @param alto alto del cuadro
     * @throws IOException si ocurre un error de escritura
     */
    private void escribirSeccionTotales(PDPageContentStream contenido, Factura factura,
                                        float x, float y, float ancho, float alto) throws IOException {
        dibujarCuadro(contenido, x, y, ancho, alto);
        escribirTituloSeccion(contenido, "Importes", x, y, alto);

        float xEtiqueta = x + 320f;
        float xValor = x + 425f;
        float yTexto = y + alto - 34f;

        escribirLinea(contenido, "Base imponible:", xEtiqueta, yTexto, FUENTE_NORMAL, 10f);
        escribirLinea(contenido, formatearNumero(factura.getBaseImponible()) + " €", xValor, yTexto, FUENTE_NEGRITA, 10f);

        yTexto -= 16f;
        escribirLinea(contenido, "IVA:", xEtiqueta, yTexto, FUENTE_NORMAL, 10f);
        escribirLinea(contenido, formatearNumero(factura.getIva()) + " €", xValor, yTexto, FUENTE_NEGRITA, 10f);

        yTexto -= 16f;
        escribirLinea(contenido, "TOTAL:", xEtiqueta, yTexto, FUENTE_NEGRITA, 11f);
        escribirLinea(contenido, formatearNumero(factura.getTotal()) + " €", xValor, yTexto, FUENTE_NEGRITA, 11f);
    }

    /**
     * Escribe el pie de página centrado en la parte inferior del documento.
     *
     * @param contenido flujo de contenido del PDF
     * @throws IOException si ocurre un error de escritura
     */
    private void escribirPiePagina(PDPageContentStream contenido) throws IOException {
        escribirTextoCentrado(
                contenido,
                "Estudio fotovoltaico y factura generada por Solar Manager, por un futuro sostenible",
                FUENTE_CURSIVA,
                9f,
                35f
        );
    }

    /**
     * Dibuja el cuadro de una sección.
     *
     * @param contenido flujo de contenido del PDF
     * @param x posición horizontal
     * @param y posición inferior
     * @param ancho ancho del cuadro
     * @param alto alto del cuadro
     * @throws IOException si ocurre un error de escritura
     */
    private void dibujarCuadro(PDPageContentStream contenido, float x, float y,
                               float ancho, float alto) throws IOException {
        contenido.addRect(x, y, ancho, alto);
        contenido.stroke();
    }

    /**
     * Escribe el título de una sección dentro de su cuadro.
     *
     * @param contenido flujo de contenido del PDF
     * @param titulo título de la sección
     * @param x posición horizontal del cuadro
     * @param y posición inferior del cuadro
     * @param alto alto del cuadro
     * @throws IOException si ocurre un error de escritura
     */
    private void escribirTituloSeccion(PDPageContentStream contenido, String titulo,
                                       float x, float y, float alto) throws IOException {
        escribirLinea(contenido, titulo, x + PADDING_X, y + alto - 18f, FUENTE_NEGRITA, 12f);
    }

    /**
     * Escribe una única línea de texto.
     *
     * @param contenido flujo de contenido del PDF
     * @param texto texto a escribir
     * @param x posición horizontal
     * @param y posición vertical
     * @param fuente fuente a utilizar
     * @param tamano tamaño de fuente
     * @throws IOException si ocurre un error de escritura
     */
    private void escribirLinea(PDPageContentStream contenido, String texto, float x, float y,
                               PDType1Font fuente, float tamano) throws IOException {
        contenido.beginText();
        contenido.setFont(fuente, tamano);
        contenido.newLineAtOffset(x, y);
        contenido.showText(texto);
        contenido.endText();
    }

    /**
     * Escribe un texto centrado horizontalmente.
     *
     * @param contenido flujo de contenido del PDF
     * @param texto texto a escribir
     * @param fuente fuente a utilizar
     * @param tamano tamaño de fuente
     * @param y posición vertical
     * @throws IOException si ocurre un error de escritura
     */
    private void escribirTextoCentrado(PDPageContentStream contenido, String texto,
                                       PDType1Font fuente, float tamano, float y) throws IOException {
        float anchoTexto = fuente.getStringWidth(texto) / 1000f * tamano;
        float x = (ANCHO_PAGINA - anchoTexto) / 2f;
        escribirLinea(contenido, texto, x, y, fuente, tamano);
    }

    /**
     * Lee todos los bytes de un InputStream.
     *
     * @param is flujo de entrada
     * @return contenido en bytes
     * @throws IOException si ocurre un error de lectura
     */
    private byte[] leerBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] datos = new byte[4096];
        int leidos;

        try {
            while ((leidos = is.read(datos)) != -1) {
                buffer.write(datos, 0, leidos);
            }
            return buffer.toByteArray();
        } finally {
            is.close();
            buffer.close();
        }
    }

    /**
     * Devuelve una cadena segura evitando valores nulos.
     *
     * @param valor valor de entrada
     * @return texto seguro
     */
    private String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }

    /**
     * Formatea un número decimal para mostrarlo en el PDF.
     *
     * @param numero número a formatear
     * @return texto con formato decimal
     */
    private String formatearNumero(double numero) {
        DecimalFormat formato = new DecimalFormat("#,##0.00");
        return formato.format(numero);
    }

    /**
     * Formatea una fecha en formato legible.
     *
     * @param fecha fecha a formatear
     * @return fecha formateada
     */
    private String formatearFecha(java.time.LocalDate fecha) {
        if (fecha == null) {
            return "";
        }
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Construye el nombre completo del cliente.
     *
     * @param cliente cliente
     * @return nombre completo
     */
    private String construirNombreCliente(Cliente cliente) {
        String nombre = cliente != null ? valorSeguro(cliente.getNombre()) : "";
        String apellidos = cliente != null ? valorSeguro(cliente.getApellidos()) : "";
        return (nombre + " " + apellidos).trim();
    }

    /**
     * Construye la dirección completa en formato texto.
     *
     * @param direccion dirección a convertir
     * @return dirección completa
     */
    private String construirDireccion(Direccion direccion) {
        return direccion == null ? "" : direccion.toString();
    }

    /**
     * Recorta un texto si supera la longitud máxima indicada.
     *
     * @param texto texto de entrada
     * @param longitudMaxima longitud máxima permitida
     * @return texto recortado
     */
    private String recortarTexto(String texto, int longitudMaxima) {
        if (texto == null) {
            return "";
        }

        if (texto.length() <= longitudMaxima) {
            return texto;
        }

        return texto.substring(0, longitudMaxima - 3) + "...";
    }
}