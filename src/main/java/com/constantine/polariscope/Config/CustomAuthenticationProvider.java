package com.constantine.polariscope.Config;

import com.constantine.polariscope.Util.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserSession userSession;

    private static final String SALT = "polariscope_authentication";
    private static final int ITERATIONS = 10;
    private static final int KEY_LENGTH = 256;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String plaintextPassword = authentication.getCredentials().toString();

        // PBKDF2 hash from the plaintext password
        String hashedPassword = generatePBKDF2Hash(plaintextPassword);
        userSession.setHashedPassword(hashedPassword);

        // Proceed with authentication process
        Authentication auth = super.authenticate(authentication);

        return auth;
    }

    private String generatePBKDF2Hash(String plaintextPassword) {
        try {
            KeySpec spec = new PBEKeySpec(plaintextPassword.toCharArray(), SALT.getBytes(), ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating PBKDF2 hash", e);
        }
    }
}