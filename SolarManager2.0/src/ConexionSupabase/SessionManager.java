package ConexionSupabase;

import org.json.JSONObject;

public class SessionManager {

    private static JSONObject usuarioActual;

    public static void setUsuario(JSONObject usuario) {
        usuarioActual = usuario;
    }

    public static JSONObject getUsuario() {
        return usuarioActual;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}