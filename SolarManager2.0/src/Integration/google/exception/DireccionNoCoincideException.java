/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Integration.google.exception;

/**
 * Excepción lanzada cuando la dirección introducida no tiene una coincidencia
 * válida en la API de Geocoding de Google.
 * 
 * @author ivang
 */
public class DireccionNoCoincideException extends Exception {

    /**
     * Crea una excepción con el mensaje indicado.
     *
     * @param message Mensaje descriptivo
     */
    public DireccionNoCoincideException(String message) {
        super(message);
    }
}