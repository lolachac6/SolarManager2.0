package ConexionSupabase;

// POJO: representa un usuario como un objeto 

public class Usuario_Supabase {

    private String id;
    private String email;
    private String password;
    private String nombre;

    public Usuario_Supabase() {}

    public Usuario_Supabase(String id, String email, String password, String nombre) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nombre = nombre;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}