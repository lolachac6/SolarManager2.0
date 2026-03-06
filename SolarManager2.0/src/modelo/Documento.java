/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author ivang
 */
import java.time.LocalDate;

public class Documento {

    private String id;

    private String nombre;

    private TipoDocumento tipoDocumento;

    private String idReferencia;

    private TipoEntidad tipoEntidad;

    private LocalDate fechaSubida;

    public Documento() {
    }

    public Documento(String id, String nombre, TipoDocumento tipoDocumento,
                     String idReferencia, TipoEntidad tipoEntidad, LocalDate fechaSubida) {

        this.id = id;
        this.nombre = nombre;
        this.tipoDocumento = tipoDocumento;
        this.idReferencia = idReferencia;
        this.tipoEntidad = tipoEntidad;
        this.fechaSubida = fechaSubida;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getIdReferencia() {
        return idReferencia;
    }

    public void setIdReferencia(String idReferencia) {
        this.idReferencia = idReferencia;
    }

    public TipoEntidad getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(TipoEntidad tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public LocalDate getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDate fechaSubida) {
        this.fechaSubida = fechaSubida;
    }
    
    public enum TipoDocumento {

    PRESUPUESTO_PDF,
    FACTURA_PDF,
    CONTRATO,
    FOTO_INSTALACION,
    OTRO
    }
    
    public enum TipoEntidad {

    CLIENTE,
    PRESUPUESTO,
    FACTURA
    }
    
}
