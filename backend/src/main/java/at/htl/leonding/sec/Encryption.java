package at.htl.leonding.sec;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Encryption {

    // Länge des Salts (in Bytes)
    private static final int SALT_LENGTH = 16;

    // Länge des Hashes (in Bytes)
    private static final int HASH_LENGTH = 64;

    // Anzahl der Iterationen
    private static final int ITERATIONS = 10000;

    // PBKDF2 Algorithmus
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    // Generiere ein Salt
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    // Erzeuge einen gehashten Wert des Passworts mit Salt
    public static byte[] hashPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, HASH_LENGTH * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    // Konvertiere das Passwort zu einem Salted Hash (zur Speicherung)
    public static String generateSaltedHash(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = generateSalt();
        byte[] hash = hashPassword(password, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    // Verifiziere das Passwort mit dem gespeicherten Hash und Salt
    public static boolean verifyPassword(char[] password, String storedHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedHash.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] hash = Base64.getDecoder().decode(parts[1]);
        byte[] hashToVerify = hashPassword(password, salt);
        return slowEquals(hash, hashToVerify);
    }

    // Eine Methode, um sicher Vergleiche durchzuführen (gegen Timing-Angriffe)
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "mySecurePassword";
        String saltedHash = generateSaltedHash(password.toCharArray());

        System.out.println("Salted Hash: " + saltedHash.substring(25));

        boolean isPasswordCorrect = verifyPassword(password.toCharArray(), saltedHash);
        System.out.println("Password Verification: " + isPasswordCorrect);
    }
}
