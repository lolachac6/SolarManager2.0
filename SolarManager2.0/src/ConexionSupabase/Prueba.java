package ConexionSupabase;

public class Prueba {
    
    //Al desarrollar la clase principal o la interfaz JavaFX, esta clase pasarla a una carpeta de pruebas y no incluirla en la app final

    public static void main(String[] args) throws Exception {

        UsuarioService service = new UsuarioService();

        System.out.println("=== PRUEBAS SUPABASE ===");

        // 1. REGISTRO
        System.out.println("\n--- REGISTRAR USUARIO ---");
        boolean registrado = service.registrar("test@test.com", "12345678", "Juan Pérez");
        System.out.println("Registrado: " + registrado);

        // 2. LOGIN
        System.out.println("\n--- LOGIN ---");
        boolean loginCorrecto = service.login("test@test.com", "12345678");
        System.out.println("Login correcto: " + loginCorrecto);

        // 3. LISTAR USUARIOS
        System.out.println("\n--- LISTAR USUARIOS ---");
        String lista = service.listar();
        System.out.println(lista);

        // 4. ACTUALIZAR USUARIO
        System.out.println("\n--- ACTUALIZAR USUARIO ---");
        String idUsuario = "6e7358d8-7d4e-4622-87d8-996b24127f7a"; // <-- PON AQUÍ TU ID REAL
        boolean actualizado = service.actualizar(idUsuario, "nuevaPassword123", "Juan Actualizado");
        System.out.println("Actualizado: " + actualizado);

        // 5. RESET PASSWORD
        System.out.println("\n--- RESET PASSWORD ---");
        boolean reset = service.resetPassword("test@test.com", "reset12345");
        System.out.println("Password reseteado: " + reset);

        // 6. ELIMINAR USUARIO
        System.out.println("\n--- ELIMINAR USUARIO ---");
        boolean eliminado = service.eliminar(idUsuario);
        System.out.println("Eliminado: " + eliminado);

        System.out.println("\n=== FIN DE PRUEBAS ===");
    }
}