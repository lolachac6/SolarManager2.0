package modelo;

/**
 * Clase que representa el resultado del cálculo solar.
 */
public class ResultadoSolar {

    private double horasSol;
    private double area;
    private int maxPaneles;
    private int panelesNecesarios;
    private double energiaPorPanel;
    private double presupuesto;
    private boolean autosuficiente;
    private double potenciaInstalada;

    public double getPotenciaInstalada() {
        return potenciaInstalada;
    }

    public void setPotenciaInstalada(double potenciaInstalada) {
        this.potenciaInstalada = potenciaInstalada;
    }

    public double getHorasSol() { return horasSol; }
    public void setHorasSol(double horasSol) { this.horasSol = horasSol; }

    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }

    public int getMaxPaneles() { return maxPaneles; }
    public void setMaxPaneles(int maxPaneles) { this.maxPaneles = maxPaneles; }

    public int getPanelesNecesarios() { return panelesNecesarios; }
    public void setPanelesNecesarios(int panelesNecesarios) { this.panelesNecesarios = panelesNecesarios; }

    public double getEnergiaPorPanel() { return energiaPorPanel; }
    public void setEnergiaPorPanel(double energiaPorPanel) { this.energiaPorPanel = energiaPorPanel; }

    public double getPresupuesto() { return presupuesto; }
    public void setPresupuesto(double presupuesto) { this.presupuesto = presupuesto; }

    public boolean isAutosuficiente() { return autosuficiente; }
    public void setAutosuficiente(boolean autosuficiente) { this.autosuficiente = autosuficiente; }
}