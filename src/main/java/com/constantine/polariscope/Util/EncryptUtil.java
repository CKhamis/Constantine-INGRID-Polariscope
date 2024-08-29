package com.constantine.polariscope.Util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

@Component
@SessionScope
public class EncryptUtil {
    private static String hashedPassword;

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;
    private static final int ITERATION_COUNT = 5;

    public static LocalDate encryptDate(LocalDate date, UUID id){
        if(date == null){
            return null;
        }else{
            int key = Math.abs(generateIntegerKey(hashedPassword, id));
            if(key > 10000000){
                key = key % 100000;
            }
            //System.out.println("Original: " + date + " + " + key + " days ->\t" + date.plusDays(key));
            return date.plusDays(key);
        }
    }

    public static LocalDate decryptDate(LocalDate date, UUID id){
        if(date == null){
            return null;
        }else{
            int key = Math.abs(generateIntegerKey(hashedPassword, id));
            if(key > 10000000){
                key = key % 100000;
            }
            //System.out.println("Cypher: " + date + " - " + key + " days ->\t" + date.minusDays(key));
            return date.minusDays(key);
        }
    }

    public static LocalDateTime encryptDateTime(LocalDateTime date, UUID id){
        if(date == null){
            return null;
        }else{
            int key = Math.abs(generateIntegerKey(hashedPassword, id));
            //System.out.println("Original: " + date + " + " + key + " days ->\t" + date.plusSeconds(key));
            return date.plusSeconds(key);
        }
    }

    public static LocalDateTime decryptDateTime(LocalDateTime date, UUID id){
        if(date == null){
            return null;
        }else{
            int key = Math.abs(generateIntegerKey(hashedPassword, id));
            //System.out.println("Cypher: " + date + " - " + key + " days ->\t" + date.minusSeconds(key));
            return date.minusSeconds(key);
        }
    }

    public static String encryptString(String data, UUID id) {
        if(data == null){
            data = "";
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(hashedPassword, id));
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    public static String decryptString(String data, UUID id) {
        if(data == null){
            data = "";
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, generateKey(hashedPassword, id));
            byte[] decodedBytes = Base64.getDecoder().decode(data);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    public static byte[] encryptBytes(byte[] data, UUID id) {
        if(data != null){
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, generateKey(hashedPassword, id));
                byte[] encryptedBytes = cipher.doFinal(data);
                return Base64.getEncoder().encode(encryptedBytes);
            } catch (Exception e) {
                throw new RuntimeException("Error encrypting data", e);
            }
        }
        return null;
    }

    public static byte[] decryptBytes(byte[] data, UUID id) {
        if(data != null){
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, generateKey(hashedPassword, id));
                byte[] decodedBytes = Base64.getDecoder().decode(data);
                byte[] decryptedBytes = cipher.doFinal(decodedBytes);
                return decryptedBytes;
            } catch (Exception e) {
                throw new RuntimeException("Error decrypting data", e);
            }
        }
        return null;
    }

    public static Integer encryptInteger(Integer num, UUID id){
        if(num == null){
            return null;
        }
        return num + generateIntegerKey(hashedPassword, id);
    }

    public static Integer decryptInteger(Integer num, UUID id){
        if(num == null){
            return null;
        }
        return num - generateIntegerKey(hashedPassword, id);
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

    public void setHashedPassword(String password){
        hashedPassword = password;
    }
}
