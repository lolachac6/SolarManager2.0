/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 * Clase abstracta que representa una persona dentro del sistema.
 * <p>
 * Esta clase actúa como superclase para las entidades que representan
 * personas en la aplicación, como por ejemplo {@link Cliente} o {@link Usuario}.
 * </p>
 *
 * <p>
 * Contiene los datos comunes a cualquier persona:
 * </p>
 * <ul>
 * <li>Nombre</li>
 * <li>Apellidos</li>
 * <li>Teléfono</li>
 * <li>Email</li>
 * <li>Dirección</li>
 * </ul>
 *
 * <p>
 * Al ser una clase abstracta, no puede instanciarse directamente y debe
 * ser heredada por otras clases del sistema.
 * </p>
 *
 * @author ivang
 */
public abstract class Persona {

    /** Nombre de la persona */
    private String nombre;

    /** Apellidos de la persona */
    private String apellidos;

    /** Número de teléfono de contacto */
    private String telefono;

    /** Dirección de correo electrónico */
    private String email;

    /** Dirección postal de la persona */
    private Direccion direccion;

    /**
     * Constructor vacío.
     * <p>
     * Permite crear una persona sin inicializar sus atributos.
     * Es útil para frameworks de persistencia o inicialización posterior.
     * </p>
     */
    public Persona() {
    }

    /**
     * Constructor completo que inicializa todos los atributos comunes
     * de una persona.
     *
     * @param nombre nombre de la persona
     * @param apellidos apellidos de la persona
     * @param telefono teléfono de contacto
     * @param email correo electrónico
     * @param direccion dirección postal
     */
    public Persona(String nombre, String apellidos, String telefono, String email, Direccion direccion) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    /**
     * Obtiene el nombre de la persona.
     *
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la persona.
     *
     * @param nombre nombre de la persona
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene los apellidos de la persona.
     *
     * @return apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos de la persona.
     *
     * @param apellidos apellidos de la persona
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Obtiene el número de teléfono.
     *
     * @return teléfono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el número de teléfono.
     *
     * @param telefono teléfono de contacto
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene el correo electrónico.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico.
     *
     * @param email correo electrónico
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la dirección de la persona.
     *
     * @return dirección
     */
    public Direccion getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección de la persona.
     *
     * @param direccion dirección postal
     */
    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    /**
     * Devuelve una representación textual de la persona.
     * <p>
     * Útil para depuración, logs o visualización rápida de datos.
     * </p>
     *
     * @return representación textual de la persona
     */
    @Override
    public String toString() {
        return nombre + " " + apellidos +
               " | Tel: " + telefono +
               " | Email: " + email +
               " | Dirección: " + direccion;
    }
}