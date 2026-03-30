package ConexionSupabase;

import okhttp3.*;

public class SupabaseClient {

    public static final String SUPABASE_URL = "https://yhwsvqefbaefaxdfekzo.supabase.co";
    public static final String BASE_URL = SUPABASE_URL;
    public static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inlod3N2cWVmYmFlZmF4ZGZla3pvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI4MTY3NjAsImV4cCI6MjA4ODM5Mjc2MH0.hI0yyY1sQw93kkM7OyHmiIDebl0BwHaf6qM7SHLn0SA";

    public static final MediaType JSON = MediaType.parse("application/json");

    private static final OkHttpClient client = new OkHttpClient();

    public static Request.Builder baseRequest(String endpoint) {
        return new Request.Builder()
                .url(SUPABASE_URL + endpoint)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation");
    }

    public static String execute(Request request) throws Exception {

        Response response = client.newCall(request).execute();
        String body = response.body().string();

        System.out.println("=== PETICIÓN SUPABASE ===");
        System.out.println("URL: " + request.url());
        System.out.println("Método: " + request.method());
        System.out.println("Código HTTP: " + response.code());
        System.out.println("Respuesta: " + body);
        System.out.println("==========================");

        return body;
    }
}
