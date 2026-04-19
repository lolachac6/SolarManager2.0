package components.pantallas.pantallaDetalleInstalaciones;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.InstalacionFotovoltaica;

public class PantallaDetalleInstalacionesController {

    @FXML private TextField txtId;
    @FXML private TextField txtCliente;
    @FXML private TextField txtPotencia;
    @FXML private TextField txtPaneles;
    @FXML private TextField txtBateria;

    public void cargarInstalacion(InstalacionFotovoltaica i) {

        txtId.setText(i.getId());
        txtCliente.setText(i.getIdCliente());
        txtPotencia.setText(String.valueOf(i.getPotenciaInstalada()));
        txtPaneles.setText(String.valueOf(i.getNumeroPaneles()));
        txtBateria.setText(i.getBateria() ? "Sí" : "No");
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) txtId.getScene().getWindow();
        stage.close();
    }

    public void cargarInstalacionPorId(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}