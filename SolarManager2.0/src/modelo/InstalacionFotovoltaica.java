package modelo;

import java.io.Serializable;

/**
 * Modelo de Instalación Fotovoltaica.
 *
 * Representa los datos de una instalación incluyendo su identificación,
 * cliente asociado, parámetros técnicos y dirección.
 *
 * @author Iván
 */
public class InstalacionFotovoltaica{

    private String id;
    private String idCliente;
    private String nombreCliente;
    private double potenciaInstalada;
    private int numeroPaneles;
    private double produccionEstimada;
    private double ahorroEstimado;
    private String inversor;
    private boolean bateria;
    private Direccion direccion;

    public InstalacionFotovoltaica() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public double getPotenciaInstalada() {
        return potenciaInstalada;
    }

    public void setPotenciaInstalada(double potenciaInstalada) {
        this.potenciaInstalada = potenciaInstalada;
    }

    public int getNumeroPaneles() {
        return numeroPaneles;
    }

    public void setNumeroPaneles(int numeroPaneles) {
        this.numeroPaneles = numeroPaneles;
    }

    public double getProduccionEstimada() {
        return produccionEstimada;
    }

    public void setProduccionEstimada(double produccionEstimada) {
        this.produccionEstimada = produccionEstimada;
    }

    public double getAhorroEstimado() {
        return ahorroEstimado;
    }

    public void setAhorroEstimado(double ahorroEstimado) {
        this.ahorroEstimado = ahorroEstimado;
    }

    public String getInversor() {
        return inversor;
    }

    public void setInversor(String inversor) {
        this.inversor = inversor;
    }

    public boolean getBateria() {
        return bateria;
    }

    public void setBateria(boolean bateria) {
        this.bateria = bateria;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }
}