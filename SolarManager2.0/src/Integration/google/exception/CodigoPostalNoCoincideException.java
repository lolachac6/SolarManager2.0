/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Integration.google.exception;

/**
 * Excepción lanzada cuando el código postal introducido no coincide
 * con el código postal real devuelto por la API de Geocoding.
 * 
 * @author ivang
 */
public class CodigoPostalNoCoincideException extends Exception {

    /**
     * Crea una excepción con el mensaje indicado.
     *
     * @param message Mensaje descriptivo
     */
    public CodigoPostalNoCoincideException(String message) {
        super(message);
    }
}