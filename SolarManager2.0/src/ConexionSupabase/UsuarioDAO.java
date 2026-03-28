package ConexionSupabase;

import okhttp3.*;

public class UsuarioDAO {

    // Crear usuario
    public static String crearUsuario(String email, String hashedPassword, String nombre) throws Exception {

        String json = "{"
                + "\"email\": \"" + email + "\","
                + "\"password_hash\": \"" + hashedPassword + "\","
                + "\"nombre\": \"" + nombre + "\""
                + "}";

        RequestBody body = RequestBody.create(SupabaseClient.JSON, json);

        Request request = SupabaseClient.baseRequest("/rest/v1/usuarios")
                .post(body)
                .build();

        return SupabaseClient.execute(request);
    }

    // Buscar usuario por email
    public static String buscarPorEmail(String email) throws Exception {

        HttpUrl url = HttpUrl.parse(SupabaseClient.BASE_URL + "/rest/v1/usuarios")
                .newBuilder()
                .addQueryParameter("email", "eq." + email)
                .addQueryParameter("select", "*")
                .build();

        Request request = SupabaseClient.baseRequest(url.encodedPath() + "?" + url.encodedQuery())
                .get()
                .build();

        return SupabaseClient.execute(request);
    }

    // Actualizar usuario
    public static String actualizarUsuario(String id, String hashedPassword, String nombre) throws Exception {

        String json = "{"
                + "\"password_hash\": \"" + hashedPassword + "\","
                + "\"nombre\": \"" + nombre + "\""
                + "}";

        RequestBody body = RequestBody.create(SupabaseClient.JSON, json);

        Request request = SupabaseClient.baseRequest("/rest/v1/usuarios?id=eq." + id)
                .patch(body)
                .build();

        return SupabaseClient.execute(request);
    }

    // Eliminar usuario
    public static String eliminarUsuario(String id) throws Exception {

        Request request = SupabaseClient.baseRequest("/rest/v1/usuarios?id=eq." + id)
                .delete()
                .build();

        return SupabaseClient.execute(request);
    }

    // Reset password
    public static String resetPassword(String email, String hashedPassword) throws Exception {

        String json = "{ \"password_hash\": \"" + hashedPassword + "\" }";

        RequestBody body = RequestBody.create(SupabaseClient.JSON, json);

        Request request = SupabaseClient.baseRequest("/rest/v1/usuarios?email=eq." + email)
                .patch(body)
                .build();

        return SupabaseClient.execute(request);
    }

    // Listar usuarios
    public static String listarUsuarios() throws Exception {

        Request request = SupabaseClient.baseRequest("/rest/v1/usuarios?select=*")
                .get()
                .build();

        return SupabaseClient.execute(request);
    }
}