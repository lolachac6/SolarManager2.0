package utils;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 * Clase utilitaria centralizada para gestionar las alertas y confirmaciones
 * de la aplicación Solar Manager.
 *
 * <p>Permite mantener una presentación coherente de los mensajes mostrados
 * al usuario desde cualquier controlador de la aplicación.</p>
 *
 * <p>Incluye alertas de información, advertencia, error y confirmaciones
 * genéricas y específicas para los distintos módulos del sistema.</p>
 *
 * @author Iván
 */
public final class AlertasSolarManager {

    /**
     * Título general utilizado en todas las ventanas de alerta.
     */
    private static final String TITULO_APP = "Solar Manager";

    /**
     * Constructor privado para evitar la instanciación de la clase.
     */
    private AlertasSolarManager() {
    }

    /**
     * Muestra una alerta de información.
     *
     * @param cabecera cabecera de la alerta
     * @param mensaje mensaje principal de la alerta
     */
    public static void info(String cabecera, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, cabecera, mensaje);
    }

    /**
     * Muestra una alerta de advertencia.
     *
     * @param cabecera cabecera de la alerta
     * @param mensaje mensaje principal de la alerta
     */
    public static void warning(String cabecera, String mensaje) {
        mostrar(Alert.AlertType.WARNING, cabecera, mensaje);
    }

    /**
     * Muestra una alerta de error.
     *
     * @param cabecera cabecera de la alerta
     * @param mensaje mensaje principal de la alerta
     */
    public static void error(String cabecera, String mensaje) {
        mostrar(Alert.AlertType.ERROR, cabecera, mensaje);
    }

    /**
     * Muestra una alerta del tipo indicado.
     *
     * @param tipo tipo de alerta JavaFX
     * @param cabecera cabecera de la ventana
     * @param mensaje mensaje principal
     */
    public static void mostrar(Alert.AlertType tipo, String cabecera, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(TITULO_APP);
        alert.setHeaderText(cabecera);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra una confirmación estándar.
     *
     * @param cabecera cabecera de la confirmación
     * @param mensaje mensaje de confirmación
     * @return true si el usuario pulsa aceptar, false en caso contrario
     */
    public static boolean confirmar(String cabecera, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(TITULO_APP);
        alert.setHeaderText(cabecera);
        alert.setContentText(mensaje);

        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    /**
     * Muestra una confirmación con textos de botones personalizados.
     *
     * @param cabecera cabecera de la confirmación
     * @param mensaje mensaje de confirmación
     * @param textoAceptar texto del botón de aceptación
     * @param textoCancelar texto del botón de cancelación
     * @return true si el usuario pulsa el botón de aceptación, false en caso contrario
     */
    public static boolean confirmar(String cabecera, String mensaje,
                                    String textoAceptar, String textoCancelar) {

        ButtonType aceptar = new ButtonType(textoAceptar, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType(textoCancelar, ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(TITULO_APP);
        alert.setHeaderText(cabecera);
        alert.setContentText(mensaje);
        alert.getButtonTypes().setAll(aceptar, cancelar);

        return alert.showAndWait().orElse(cancelar) == aceptar;
    }

    /**
     * Muestra una alerta indicando que debe seleccionarse un cliente para editar.
     */
    public static void seleccionarClienteParaEditar() {
        warning("Cliente no seleccionado", "Debe seleccionar un cliente de la tabla para editar.");
    }

    /**
     * Muestra una alerta indicando que debe seleccionarse un cliente para eliminar.
     */
    public static void seleccionarClienteParaEliminar() {
        warning("Cliente no seleccionado", "Debe seleccionar un cliente de la tabla para eliminar.");
    }

    /**
     * Muestra una alerta indicando que el cliente ha sido eliminado correctamente.
     */
    public static void clienteEliminadoCorrectamente() {
        info("Cliente eliminado", "El cliente se ha eliminado correctamente.");
    }

    /**
     * Muestra una alerta indicando un error al guardar un cliente.
     */
    public static void errorGuardarCliente() {
        error("Error al guardar cliente", "No se ha podido guardar el cliente en la base de datos.");
    }

    /**
     * Muestra una alerta indicando un error al abrir la pantalla de edición de cliente.
     */
    public static void errorAbrirEdicionCliente() {
        error("Error al abrir edición", "No se ha podido abrir la pantalla de edición del cliente.");
    }

    /**
     * Muestra una alerta indicando un error al cambiar de pantalla.
     */
    public static void errorCambioPantalla() {
        error("Error de navegación", "No se ha podido cambiar de pantalla.");
    }

    /**
     * Muestra una alerta indicando que la pantalla está en desarrollo.
     */
    public static void pantallaEnDesarrollo() {
        info("Pantalla en desarrollo", "Esta funcionalidad todavía no está implementada.");
    }

    /**
     * Muestra una alerta indicando que un comercial ha sido creado correctamente.
     */
    public static void comercialCreadoCorrectamente() {
        info("Comercial creado", "El comercial se ha creado correctamente.");
    }

    /**
     * Muestra una alerta indicando que un comercial ha sido actualizado correctamente.
     */
    public static void comercialActualizadoCorrectamente() {
        info("Comercial actualizado", "El comercial se ha actualizado correctamente.");
    }

    /**
     * Muestra una alerta indicando que no ha podido actualizarse un comercial
     * porque el email ya está en uso.
     */
    public static void emailComercialDuplicadoEnActualizacion() {
        error("No se puede actualizar", "El email ya está en uso por otro comercial.");
    }

    /**
     * Muestra una alerta indicando que ya existe un comercial con el email indicado.
     */
    public static void emailComercialDuplicado() {
        warning("Email ya registrado", "Ya existe un comercial con este email.");
    }

    /**
     * Muestra una alerta indicando un error genérico.
     *
     * @param detalle detalle del error
     */
    public static void errorGenerico(String detalle) {
        error("Error", detalle);
    }

    /**
     * Muestra una alerta indicando que no se pudo guardar el comercial
     * por posible duplicidad o error en Supabase.
     */
    public static void errorSupabaseDuplicadoOGeneral() {
        error("Error en Supabase",
                "No se pudo guardar el comercial.\nPosible email duplicado o error en Supabase.");
    }

    /**
     * Muestra una alerta indicando un fallo al actualizar usuario en Supabase.
     *
     * @param detalle detalle del error
     */
    public static void falloActualizarUsuarioSupabase(String detalle) {
        error("Error en Supabase", "Fallo al actualizar usuario en Supabase:\n" + detalle);
    }

    /**
     * Muestra una alerta indicando que no pudo actualizarse la tabla de usuarios de Supabase.
     *
     * @param codigoHttp código HTTP devuelto
     */
    public static void errorActualizarTablaUsuariosSupabase(int codigoHttp) {
        error("Error en Supabase",
                "No se pudo actualizar la tabla de usuarios.\nCódigo HTTP: " + codigoHttp);
    }

    /**
     * Muestra una alerta indicando un fallo al actualizar un usuario.
     *
     * @param detalle detalle del error
     */
    public static void falloActualizarUsuario(String detalle) {
        error("Error en Supabase", "Fallo al actualizar usuario:\n" + detalle);
    }

    /**
     * Muestra una alerta indicando que el nombre es obligatorio.
     */
    public static void nombreObligatorio() {
        warning("Campo obligatorio", "El nombre es obligatorio.");
    }

    /**
     * Muestra una alerta indicando que el nombre no es válido.
     */
    public static void nombreInvalido() {
        warning("Dato no válido", "El nombre no tiene un formato válido.");
    }

    /**
     * Muestra una alerta indicando que los apellidos son obligatorios.
     */
    public static void apellidosObligatorios() {
        warning("Campo obligatorio", "Los apellidos son obligatorios.");
    }

    /**
     * Muestra una alerta indicando que los apellidos no son válidos.
     */
    public static void apellidosInvalidos() {
        warning("Dato no válido", "Los apellidos no tienen un formato válido.");
    }

    /**
     * Muestra una alerta indicando que el email es obligatorio.
     */
    public static void emailObligatorio() {
        warning("Campo obligatorio", "El email es obligatorio.");
    }

    /**
     * Muestra una alerta indicando que el email no es válido.
     */
    public static void emailInvalido() {
        warning("Dato no válido", "El email no tiene un formato válido.");
    }

    /**
     * Muestra una alerta indicando que el teléfono no es válido.
     */
    public static void telefonoInvalido() {
        warning("Dato no válido", "El teléfono debe tener 9 dígitos.");
    }

    /**
     * Muestra una alerta indicando que el DNI no es válido.
     */
    public static void dniInvalido() {
        warning("Dato no válido", "El DNI no tiene un formato válido.");
    }

    /**
     * Muestra una alerta indicando que el código postal no es válido.
     */
    public static void codigoPostalInvalido() {
        warning("Dato no válido", "El código postal debe tener 5 dígitos.");
    }

    /**
     * Muestra una alerta indicando que la cuenta bancaria no es válida.
     */
    public static void ibanInvalido() {
        warning("Dato no válido", "La cuenta bancaria no tiene un formato IBAN válido.");
    }

    /**
     * Muestra una alerta indicando que la contraseña es obligatoria.
     */
    public static void passwordObligatoria() {
        warning("Campo obligatorio", "La contraseña es obligatoria.");
    }

    /**
     * Muestra una alerta indicando que la contraseña es débil.
     */
    public static void passwordDebil() {
        warning("Dato no válido",
                "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número.");
    }

    /**
     * Muestra una alerta indicando un error al volver de pantalla.
     *
     * @param detalle detalle del error
     */
    public static void errorAlVolver(String detalle) {
        error("Error al volver", detalle);
    }

    /**
     * Muestra una alerta indicando que un producto se ha guardado correctamente.
     */
    public static void productoGuardadoCorrectamente() {
        info("Producto guardado", "El producto se ha guardado correctamente.");
    }

    /**
     * Muestra una alerta indicando que debe seleccionarse un producto para modificar.
     */
    public static void seleccionarProductoModificar() {
        warning("Producto no seleccionado", "Selecciona un producto de la tabla para modificar.");
    }

    /**
     * Muestra una alerta indicando que debe seleccionarse un producto para eliminar.
     */
    public static void seleccionarProductoEliminar() {
        warning("Producto no seleccionado", "Selecciona un producto de la tabla para eliminar.");
    }

    /**
     * Muestra una alerta indicando que el nombre del producto es obligatorio.
     */
    public static void nombreProductoObligatorio() {
        warning("Campo obligatorio", "El nombre es obligatorio.");
    }

    /**
     * Muestra una alerta indicando que debe seleccionarse un tipo de producto.
     */
    public static void tipoProductoObligatorio() {
        warning("Campo obligatorio", "Seleccione un tipo de producto.");
    }

    /**
     * Muestra una alerta indicando que el precio no es válido.
     */
    public static void precioNoValido() {
        warning("Dato no válido", "El precio debe ser un número válido y positivo.");
    }

    /**
     * Muestra una alerta indicando que el stock no es válido.
     */
    public static void stockNoValido() {
        warning("Dato no válido", "El stock debe ser un número entero positivo.");
    }

    /**
     * Muestra una alerta indicando que debe seleccionarse un proveedor.
     */
    public static void proveedorObligatorio() {
        warning("Campo obligatorio", "Seleccione un proveedor.");
    }

    /**
     * Muestra una alerta indicando que la descripción es obligatoria.
     */
    public static void descripcionObligatoria() {
        warning("Campo obligatorio", "La descripción es obligatoria.");
    }

    /**
     * Muestra una alerta indicando que un producto ha sido eliminado correctamente.
     */
    public static void productoEliminadoCorrectamente() {
        info("Producto eliminado", "El producto se ha eliminado correctamente.");
    }

    /**
     * Muestra una alerta indicando un error al eliminar un producto.
     *
     * @param detalle detalle del error
     */
    public static void errorEliminarProducto(String detalle) {
        error("Error al eliminar producto", detalle);
    }

    /**
     * Muestra una confirmación para cancelar la edición de un producto.
     *
     * @return true si el usuario acepta, false en caso contrario
     */
    public static boolean confirmarCancelarProducto() {
        return confirmar(
                "Cancelar sin guardar",
                "Los cambios no guardados se perderán.",
                "Salir sin guardar",
                "Volver"
        );
    }

    /**
     * Muestra una alerta indicando que debe pulsarse primero el botón editar.
     */
    public static void pulsarPrimeroEditar() {
        warning("Edición no habilitada", "Pulsa primero el botón EDITAR.");
    }

    /**
     * Muestra una alerta indicando que un proveedor ha sido guardado correctamente.
     */
    public static void proveedorGuardadoCorrectamente() {
        info("Proveedor guardado", "El proveedor se ha guardado correctamente.");
    }

    /**
     * Muestra una alerta indicando un error al guardar un proveedor.
     *
     * @param detalle detalle del error
     */
    public static void errorGuardarProveedor(String detalle) {
        error("Error al guardar proveedor", detalle);
    }

    /**
     * Muestra una alerta indicando que el nombre es obligatorio.
     */
    public static void nombreProveedorObligatorio() {
        warning("Campo obligatorio", "El nombre es obligatorio.");
    }

    /**
     * Muestra una alerta indicando que el teléfono es obligatorio.
     */
    public static void telefonoObligatorio() {
        warning("Campo obligatorio", "El teléfono es obligatorio.");
    }

    /**
     * Muestra una alerta indicando que el email es obligatorio.
     */
    public static void emailProveedorObligatorio() {
        warning("Campo obligatorio", "El email es obligatorio.");
    }

    /**
     * Muestra una alerta indicando que el email del proveedor no es válido.
     */
    public static void emailProveedorInvalido() {
        warning("Dato no válido", "El email no es válido.");
    }

    /**
     * Muestra una alerta indicando que la dirección es obligatoria.
     */
    public static void direccionObligatoria() {
        warning("Campo obligatorio", "La calle es obligatoria.");
    }

    /**
     * Muestra una alerta indicando que la empresa es obligatoria.
     */
    public static void empresaObligatoria() {
        warning("Campo obligatorio", "La empresa es obligatoria.");
    }

    /**
     * Muestra una alerta indicando que el modo edición ha sido activado.
     */
    public static void modoEdicionActivado() {
        info("Modo edición activado", "Modo edición activado.");
    }

    /**
     * Muestra una confirmación para cancelar cambios de proveedor.
     *
     * @return true si el usuario confirma, false en caso contrario
     */
    public static boolean confirmarCancelarProveedor() {
        return confirmar(
                "Salir sin guardar",
                "¿Deseas cancelar y volver a la lista de proveedores?\n\nLos cambios no guardados se perderán."
        );
    }

    /**
     * Muestra una alerta indicando que debe seleccionarse una instalación para editar.
     */
    public static void seleccionarInstalacionParaEditar() {
        warning("Instalación no seleccionada", "Debe seleccionar una instalación para editar.");
    }

    /**
     * Muestra una alerta indicando que una instalación ha sido guardada correctamente.
     */
    public static void instalacionGuardadaCorrectamente() {
        info("Instalación guardada", "La instalación se ha guardado correctamente.");
    }

    /**
     * Muestra una alerta indicando que la instalación calculada es autosuficiente.
     */
    public static void instalacionAutosuficiente() {
        info("Resultado del cálculo", "Instalación autosuficiente.");
    }

    /**
     * Muestra una alerta indicando que no hay suficiente espacio en el tejado.
     */
    public static void instalacionSinEspacioSuficiente() {
        info("Resultado del cálculo", "No hay suficiente espacio en el tejado.");
    }

    /**
     * Muestra una alerta indicando un error al guardar una instalación.
     */
    public static void errorGuardarInstalacion() {
        error("Error al guardar instalación", "No se ha podido guardar la instalación.");
    }

    /**
     * Muestra una alerta indicando un error durante el cálculo de una instalación.
     *
     * @param detalle detalle del error
     */
    public static void errorCalculoInstalacion(String detalle) {
        error("Error en el cálculo", "Error en el cálculo: " + detalle);
    }

    /**
     * Muestra una confirmación para aplicar las correcciones de dirección propuestas.
     *
     * @param mensaje mensaje con las diferencias detectadas
     * @return true si el usuario acepta las correcciones, false en caso contrario
     */
    public static boolean confirmarCorreccionDireccion(String mensaje) {
        return confirmar("Se han encontrado diferencias", mensaje);
    }

    /**
     * Muestra una alerta indicando que se ha creado correctamente un presupuesto.
     */
    public static void presupuestoCreadoCorrectamente() {
        info("Presupuesto creado", "Presupuesto creado correctamente.");
    }

    /**
     * Muestra una alerta indicando que debe seleccionarse un cliente.
     */
    public static void seleccionarClientePresupuesto() {
        warning("Campo obligatorio", "Seleccione un cliente.");
    }

    /**
     * Muestra una alerta indicando que debe seleccionarse un comercial.
     */
    public static void seleccionarComercialPresupuesto() {
        warning("Campo obligatorio", "Seleccione un comercial.");
    }

    /**
     * Muestra una alerta indicando que debe seleccionarse una fecha.
     */
    public static void seleccionarFechaPresupuesto() {
        warning("Campo obligatorio", "Seleccione una fecha.");
    }

    /**
     * Muestra una alerta indicando que la potencia no es válida.
     */
    public static void potenciaNoValida() {
        warning("Dato no válido", "Potencia no válida.");
    }

    /**
     * Muestra una alerta indicando que el número de paneles no es válido.
     */
    public static void numeroPanelesNoValido() {
        warning("Dato no válido", "Número de paneles no válido.");
    }

    /**
     * Muestra una alerta indicando que la producción no es válida.
     */
    public static void produccionNoValida() {
        warning("Dato no válido", "Producción no válida.");
    }

    /**
     * Muestra una alerta indicando que el ahorro no es válido.
     */
    public static void ahorroNoValido() {
        warning("Dato no válido", "Ahorro no válido.");
    }

    /**
     * Muestra una alerta indicando que debe seleccionarse un comercial.
     */
    public static void seleccionarComercial() {
        warning("Comercial no seleccionado", "Debe seleccionar un comercial de la tabla para continuar.");
    }

    /**
     * Muestra una confirmación para eliminar un comercial.
     *
     * @return true si el usuario confirma, false en caso contrario
     */
    public static boolean confirmarEliminarComercial() {
        return confirmar("Eliminar comercial", "¿Estás seguro que deseas eliminar el comercial?");
    }

    /**
     * Muestra una confirmación para reactivar un comercial.
     *
     * @return true si el usuario confirma, false en caso contrario
     */
    public static boolean confirmarReactivarComercial() {
        return confirmar("Reactivar comercial", "¿Estás seguro que deseas reactivar el comercial?");
    }

    /**
     * Muestra una alerta indicando que la operación se ha realizado correctamente.
     */
    public static void operacionCorrecta() {
        info("Correcto", "La operación se ha realizado correctamente.");
    }
}