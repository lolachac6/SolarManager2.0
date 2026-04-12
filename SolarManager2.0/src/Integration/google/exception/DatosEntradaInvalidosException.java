/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Integration.google.exception;

/**
 * Excepción lanzada cuando los datos de entrada no son válidos
 * para realizar el cálculo de la instalación solar.
 * 
 * @author ivang
 */
public class DatosEntradaInvalidosException extends Exception {

    /**
     * Crea una excepción con el mensaje indicado.
     *
     * @param message Mensaje descriptivo
     */
    public DatosEntradaInvalidosException(String message) {
        super(message);
    }
}