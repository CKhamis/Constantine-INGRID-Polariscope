package com.constantine.polariscope.Util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

@Component
public class EncryptUtil {
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;
    private static final int ITERATION_COUNT = 5;

    public static String encryptString(String data, UUID id, String password) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(password, id));
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    public static String decryptString(String data, UUID id, String password) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, generateKey(password, id));
            byte[] decodedBytes = Base64.getDecoder().decode(data);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    public static byte[] encryptBytes(byte[] data, UUID id, String password) {
        if(data != null){
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, generateKey(password, id));
                return cipher.doFinal(data);
            } catch (Exception e) {
                throw new RuntimeException("Error encrypting data", e);
            }
        }
        return null;
    }

    public static byte[] decryptBytes(byte[] data, UUID id, String password) {
        if(data != null){
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, generateKey(password, id));
                byte[] decodedBytes = Base64.getDecoder().decode(data); //todo: this line currently does not work when adding a profile image.
                return cipher.doFinal(decodedBytes);
            } catch (Exception e) {
                throw new RuntimeException("Error decrypting data", e);
            }
        }
        return null;
    }

    public static Integer encryptInteger(Integer num, UUID id, String password){
        if(num == null){
            return null;
        }
        return num + generateIntegerKey(password, id);
    }

    public static Integer decryptInteger(Integer num, UUID id, String password){
        if(num == null){
            return null;
        }
        return num - generateIntegerKey(password, id);
    }

    private static SecretKey generateKey(String password, UUID uuid) {
        try {
            // Combine password and UUID to create a unique input
            String combinedInput = password + uuid.toString();

            // Generate a deterministic salt (could be based on the UUID or any other consistent data)
            byte[] salt = uuid.toString().getBytes(StandardCharsets.UTF_8);

            // Use PBKDF2 with HMAC SHA-256 to derive the key
            PBEKeySpec spec = new PBEKeySpec(combinedInput.toCharArray(), salt, ITERATION_COUNT, KEY_SIZE);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();

            // Create and return the AES key
            return new SecretKeySpec(keyBytes, ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Error generating secret key", e);
        }
    }

    private static int generateIntegerKey(String password, UUID uuid){
        try {
            // Combine UUID and input string
            String combinedInput = uuid.toString() + password;

            // Create a SHA-256 hash of the combined input
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combinedInput.getBytes(StandardCharsets.UTF_8));

            // Convert the first 4 bytes of the hash into an int
            int deterministicInt = 0;
            for (int i = 0; i < 4; i++) {
                deterministicInt = (deterministicInt << 8) | (hashBytes[i] & 0xFF);
            }

            return deterministicInt;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating deterministic number", e);
        }
    }

}
