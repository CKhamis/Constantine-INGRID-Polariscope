package com.constantine.polariscope.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontEndController {
    @GetMapping("/login")
    public String getLogin() {
        return "pages/main/login";
    }
}
