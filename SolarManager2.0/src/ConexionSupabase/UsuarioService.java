package ConexionSupabase;

//Es la lógica: decide si un login es correcto, etc.
// Clase que aplica la lógica de negocio y valida datos antes de llamar al DAO.

import okhttp3.HttpUrl;
import okhttp3.Request;

public class UsuarioService {

    // -------------------------
    // LOGIN
    // -------------------------
    public boolean login(String email, String password) throws Exception {

        String respuesta = UsuarioDAO.buscarPorEmail(email);

        if (respuesta.equals("[]")) {
            System.out.println("Usuario no encontrado");
            return false;
        }

        // Extraer el hash del JSON
        String hashedPassword = extraerPasswordDeJson(respuesta);

        boolean ok = PasswordUtil.checkPassword(password, hashedPassword);

        if (!ok) {
            System.out.println("Contraseña incorrecta");
        }

        return ok;
    }


    // -------------------------
    // REGISTRAR
    // -------------------------
    public boolean registrar(String email, String password, String nombre) throws Exception {

        // VALIDACIONES PRIMERO
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Email vacío");
            return false;
        }

        if (!email.contains("@")) {
            System.out.println("Email no válido");
            return false;
        }

        if (password == null || password.length() < 8) {
            System.out.println("Password demasiado corta (mínimo 8 caracteres)");
            return false;
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            System.out.println("Nombre vacío");
            return false;
        }

        // Hash de la contraseña
        String hashedPassword = PasswordUtil.hashPassword(password);

        // Llamada al DAO
        String respuesta = UsuarioDAO.crearUsuario(email, hashedPassword, nombre);

        if (respuesta.contains("duplicate key")) {
            System.out.println("El email ya está registrado");
            return false;
        }

        return true;
    }


    // -------------------------
    // RESET PASSWORD
    // -------------------------
    public boolean resetPassword(String email, String nuevaPassword) throws Exception {
        String hashed = PasswordUtil.hashPassword(nuevaPassword);
        String respuesta = UsuarioDAO.resetPassword(email, hashed);
        return !respuesta.contains("error");
    }


    // -------------------------
    // ACTUALIZAR
    // -------------------------
    public boolean actualizar(String id, String password, String nombre) throws Exception {

        String hashed = PasswordUtil.hashPassword(password);

        String respuesta = UsuarioDAO.actualizarUsuario(id, hashed, nombre);
        return !respuesta.contains("error");
    }


    // -------------------------
    // ELIMINAR
    // -------------------------
    public boolean eliminar(String id) throws Exception {
        String respuesta = UsuarioDAO.eliminarUsuario(id);
        return !respuesta.contains("error");
    }


    // -------------------------
    // LISTAR
    // -------------------------
    public String listar() throws Exception {
        return UsuarioDAO.listarUsuarios();
    }


    // -------------------------
    // EXTRAER PASSWORD DEL JSON
    // -------------------------
    private String extraerPasswordDeJson(String json) {
        // MUY SIMPLE: busca el campo "password":"..."
        int start = json.indexOf("\"password\":\"") + 12;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}