package ConexionSupabase;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    //CÓDIGO TEMPORAL PARA GENERAR EL HASH
    // public static void main(String[] args) {
    // String hash = hashPassword("comercial");
    // System.out.println(hash);
    //}

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}