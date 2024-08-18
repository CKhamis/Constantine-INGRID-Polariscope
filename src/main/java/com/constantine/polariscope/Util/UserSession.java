package com.constantine.polariscope.Util;

import com.constantine.polariscope.Model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
@Component
@Getter
@Setter
public class UserSession {
    private String hashedPassword;
}
