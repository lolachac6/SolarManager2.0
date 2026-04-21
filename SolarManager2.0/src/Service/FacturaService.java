package service;

import dao.FacturaDAO;
import java.time.LocalDate;
import modelo.Cliente;
import modelo.Factura;
import modelo.Presupuesto;
import pdf.GeneradorFacturaPDF;

/**
 * Servicio encargado de generar y guardar facturas a partir de presupuestos.
 *
 * @author ivang
 */
public class FacturaService {

    private final FacturaDAO facturaDAO;
    private final GeneradorFacturaPDF generadorFacturaPDF;

    /**
     * Constructor por defecto.
     */
    public FacturaService() {
        this.facturaDAO = new FacturaDAO();
        this.generadorFacturaPDF = new GeneradorFacturaPDF();
    }

    /**
     * Genera una factura a partir de un presupuesto y un cliente, crea su PDF
     * binario y la guarda en MongoDB.
     *
     * @param presupuesto presupuesto origen
     * @param cliente cliente asociado
     * @param numeroFactura número de factura a asignar
     * @return factura generada y guardada
     * @throws Exception si ocurre un error durante el proceso
     */
    public Factura generarYGuardarFactura(Presupuesto presupuesto, Cliente cliente,
                                          String numeroFactura) throws Exception {
        Factura factura = new Factura();
        factura.setNumeroFactura(numeroFactura);
        factura.setIdPresupuesto(presupuesto.getId());
        factura.setFechaEmision(LocalDate.now());
        factura.setBaseImponible(presupuesto.getSubtotal());
        factura.setIva(presupuesto.getIva());
        factura.setTotal(presupuesto.getTotal());
        factura.setNombreArchivo(numeroFactura + ".pdf");

        byte[] pdfBinario = generadorFacturaPDF.generarPDF(factura, presupuesto, cliente);
        factura.setPdfFactura(pdfBinario);

        facturaDAO.guardarFactura(factura);

        return factura;
    }
}