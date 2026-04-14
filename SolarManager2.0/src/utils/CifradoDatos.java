package utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilidad para cifrar y descifrar datos sensibles con AES/GCM.
 *
 * <p>Formato almacenado:
 * Base64(IV + DATOS_CIFRADOS)</p>
 *
 * <p>IMPORTANTE:
 * Sustituye el valor de CLAVE_BASE64 por una clave AES real generada una sola vez.</p>
 *
 * @author Iván
 */
public class CifradoDatos {

    private static final String ALGORITMO = "AES/GCM/NoPadding";
    private static final int TAM_IV = 12;
    private static final int TAM_TAG = 128;

    /**
     * Sustituir por una clave Base64 real.
     */
    private static final String CLAVE_BASE64 = "wzWbM0m9bM0mK7B4Qp6x2v7H8bN3rL5tY1uC9dE4fG8=";

    private CifradoDatos() {
    }

    /**
     * Cifra un texto y devuelve el resultado en Base64.
     *
     * @param textoPlano texto en claro
     * @return texto cifrado en Base64
     */
    public static String cifrar(String textoPlano) {
        try {
            if (textoPlano == null || textoPlano.isEmpty()) {
                return textoPlano;
            }

            byte[] iv = new byte[TAM_IV];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, obtenerClave(), new GCMParameterSpec(TAM_TAG, iv));

            byte[] datosCifrados = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + datosCifrados.length);
            byteBuffer.put(iv);
            byteBuffer.put(datosCifrados);

            return Base64.getEncoder().encodeToString(byteBuffer.array());

        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar el dato.", e);
        }
    }

    /**
     * Descifra un texto cifrado en Base64.
     *
     * @param textoCifradoBase64 texto cifrado
     * @return texto descifrado
     */
    public static String descifrar(String textoCifradoBase64) {
        try {
            if (textoCifradoBase64 == null || textoCifradoBase64.isEmpty()) {
                return textoCifradoBase64;
            }

            byte[] datosCompletos = Base64.getDecoder().decode(textoCifradoBase64);

            ByteBuffer byteBuffer = ByteBuffer.wrap(datosCompletos);

            byte[] iv = new byte[TAM_IV];
            byteBuffer.get(iv);

            byte[] datosCifrados = new byte[byteBuffer.remaining()];
            byteBuffer.get(datosCifrados);

            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, obtenerClave(), new GCMParameterSpec(TAM_TAG, iv));

            byte[] datosDescifrados = cipher.doFinal(datosCifrados);

            return new String(datosDescifrados, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Error al descifrar el dato.", e);
        }
    }

    /**
     * Intenta descifrar el valor. Si falla, devuelve el mismo valor recibido.
     * Esto evita romper la carga si existen registros antiguos sin cifrar.
     *
     * @param valor valor cifrado o en claro
     * @return valor descifrado si era cifrado, o el original si no lo era
     */
    public static String descifrarSiEsPosible(String valor) {
        try {
            return descifrar(valor);
        } catch (Exception e) {
            return valor;
        }
    }

    /**
     * Obtiene la clave AES a partir del valor Base64.
     *
     * @return clave AES
     */
    private static SecretKey obtenerClave() {
        byte[] claveBytes = Base64.getDecoder().decode(CLAVE_BASE64);
        return new SecretKeySpec(claveBytes, "AES");
    }

    /**
     * Método auxiliar para generar una clave AES en Base64.
     * Úsalo una sola vez y copia el valor generado en CLAVE_BASE64.
     *
     * @return clave generada en Base64
     */
    public static String generarClaveBase64() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Error al generar la clave.", e);
        }
    }
}