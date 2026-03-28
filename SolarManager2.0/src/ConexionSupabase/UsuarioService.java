package ConexionSupabase;

import org.json.JSONArray;
import org.json.JSONObject;

public class UsuarioService {

    // -------------------------
    // LOGIN
    // -------------------------
    public boolean login(String email, String password) throws Exception {

        String respuesta = UsuarioDAO.buscarPorEmail(email);

        JSONArray arr = new JSONArray(respuesta);

        if (arr.length() == 0) {
            return false; // No existe el usuario
        }

        JSONObject usuario = arr.getJSONObject(0);

        String hash = usuario.getString("password_hash");
        

        return PasswordUtil.checkPassword(password, hash);
        
        
    }
    // -------------------------
    // DEVOLVER EL ROL
    // -------------------------
    
    public JSONObject obtenerUsuarioPorEmail(String email) throws Exception {
    String respuesta = UsuarioDAO.buscarPorEmail(email);
    JSONArray arr = new JSONArray(respuesta);

    if (arr.length() == 0) return null;

    return arr.getJSONObject(0);
}
    // -------------------------
    // REGISTRAR
    // -------------------------
    public boolean registrar(String email, String password, String nombre) throws Exception {

        if (email == null || email.trim().isEmpty()) return false;
        if (!email.contains("@")) return false;
        if (password == null || password.length() < 8) return false;
        if (nombre == null || nombre.trim().isEmpty()) return false;

        String hashed = PasswordUtil.hashPassword(password);

        String respuesta = UsuarioDAO.crearUsuario(email, hashed, nombre);

        return !respuesta.contains("duplicate key");
    }

    // -------------------------
    // RESET PASSWORD
    // -------------------------
    public boolean resetPassword(String email, String nuevaPassword) throws Exception {

        String hash = PasswordUtil.hashPassword(nuevaPassword);

        String respuesta = UsuarioDAO.resetPassword(email, hash);

        return !respuesta.contains("error");
    }

    // -------------------------
    // ACTUALIZAR
    // -------------------------
    public boolean actualizar(String id, String password, String nombre) throws Exception {

        String hash = PasswordUtil.hashPassword(password);

        String respuesta = UsuarioDAO.actualizarUsuario(id, hash, nombre);

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
}