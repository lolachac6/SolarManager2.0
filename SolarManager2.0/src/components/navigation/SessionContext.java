package components.navigation;

public class SessionContext {

    public enum Rol {
        ADMIN,
        COMERCIAL
    }

    private static Rol rol;

    public static void setRol(Rol r) {
        rol = r;
    }

    public static Rol getRol() {
        return rol;
    }

    public static boolean isAdmin() {
        return rol == Rol.ADMIN;
    }

    public static boolean isComercial() {
        return rol == Rol.COMERCIAL;
    }
    
    private static String pantallaOrigen;

    public static void setPantallaOrigen(String ruta) {
        pantallaOrigen = ruta;
    }

    public static String getPantallaOrigen() {
        return pantallaOrigen;
    }
}