/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDate;

/**
 * Representa un usuario del sistema Solar Manager.
 *
 * <p>
 * Los usuarios son las personas que utilizan la aplicación para gestionar
 * clientes, presupuestos, productos y facturas.
 * </p>
 *
 * <p>
 * La clase hereda de {@link Persona}, ya que un usuario también tiene
 * información de contacto básica como nombre, teléfono, email y dirección.
 * </p>
 *
 * <p>
 * Cada usuario tiene asociado un {@link Rol} que define sus permisos dentro
 * de la aplicación.
 * </p>
 *
 * <ul>
 * <li>ADMIN → acceso completo al sistema</li>
 * <li>COMERCIAL → gestión de clientes y presupuestos</li>
 * </ul>
 *
 * @author ivang
 */
public class Usuario extends Persona {

    /** Identificador único del usuario */
    private String id;

    /** Contraseña del usuario (en un sistema real debería almacenarse cifrada) */
    private String password;

    /** Rol del usuario dentro del sistema */
    private Rol rol;

    /** Indica si el usuario está activo en el sistema */
    private boolean activo;

    /** Fecha en la que se creó el usuario */
    private LocalDate fechaAlta;

    /**
     * Constructor vacío.
     * <p>
     * Necesario para frameworks de persistencia o inicialización posterior.
     * </p>
     */
    public Usuario() {
    }

    /**
     * Constructor completo para inicializar todos los atributos del usuario.
     *
     * @param id identificador del usuario
     * @param nombre nombre del usuario
     * @param apellidos apellidos del usuario
     * @param telefono teléfono de contacto
     * @param email correo electrónico
     * @param direccion dirección del usuario
     * @param password contraseña del usuario
     * @param rol rol dentro del sistema
     * @param activo indica si el usuario está activo
     * @param fechaAlta fecha de creación del usuario
     */
    public Usuario(String id, String nombre, String apellidos, String telefono,
                   String email, Direccion direccion,
                   String password, Rol rol, boolean activo, LocalDate fechaAlta) {

        super(nombre, apellidos, telefono, email, direccion);

        this.id = id;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
        this.fechaAlta = fechaAlta;
    }

    /**
     * Obtiene el identificador del usuario.
     *
     * @return id del usuario
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador del usuario.
     *
     * @param id identificador del usuario
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene la contraseña del usuario.
     *
     * @return contraseña
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del usuario.
     *
     * @param password contraseña
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtiene el rol del usuario.
     *
     * @return rol del usuario
     */
    public Rol getRol() {
        return rol;
    }

    /**
     * Establece el rol del usuario.
     *
     * @param rol rol del usuario
     */
    public void setRol(Rol rol) {
        this.rol = rol;
    }

    /**
     * Indica si el usuario está activo.
     *
     * @return true si está activo, false en caso contrario
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Establece el estado de actividad del usuario.
     *
     * @param activo estado del usuario
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Obtiene la fecha de alta del usuario.
     *
     * @return fecha de alta
     */
    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    /**
     * Establece la fecha de alta del usuario.
     *
     * @param fechaAlta fecha de alta
     */
    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    /**
     * Devuelve una representación textual del usuario.
     *
     * <p>
     * Útil para depuración, logs o visualización rápida en listados.
     * </p>
     *
     * @return descripción del usuario
     */
    @Override
    public String toString() {
        return getNombre() + " " + getApellidos() +
               " | Rol: " + rol +
               " | Activo: " + activo;
    }

    /**
     * Enumeración que define los roles disponibles dentro del sistema.
     */
    public enum Rol {

        /** Usuario administrador con acceso completo */
        ADMIN,

        /** Usuario comercial encargado de clientes y presupuestos */
        COMERCIAL
    }

}