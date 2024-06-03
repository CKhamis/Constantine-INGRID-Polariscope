package com.constantine.polariscope.Config;

import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@AllArgsConstructor
public class OwnerInitializer {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostConstruct
    public void init() {
        String defaultUsername = System.getenv("POLARI_USERNAME");
        String defaultPassword = System.getenv("POLARI_PASSWORD");

        if (defaultUsername == null || defaultUsername.isEmpty()) {
            defaultUsername = "admin";
        }
        if (defaultPassword == null || defaultPassword.isEmpty()) {
            defaultPassword = "password";
        }

        try {
            userService.loadUserByUsername(defaultUsername);
        } catch (Exception e) {
            User newUser = new User();
            newUser.setUsername(defaultUsername);
            newUser.setPassword(bCryptPasswordEncoder.encode(defaultPassword));
            newUser.setRole(User.Role.Owner);
            newUser.setEnabled(true);
            userService.save(newUser);
        }
    }
}
