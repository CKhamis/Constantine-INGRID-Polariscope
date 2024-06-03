package com.constantine.polariscope.Controller;

import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontEndController {
    @GetMapping("/")
    public String getHome() {
        return "pages/main/home";
    }
    @GetMapping("/login")
    public String getLogin() {
        return "pages/main/login";
    }

    @GetMapping("/interpersonal")
    public String getInterpersonalHome() {
        return "pages/interpersonal/interpersonalHome";
    }

    @GetMapping("/interpersonal/new")
    public String getNewMember(Model model) {
        model.addAttribute("relationshipTypes", Member.RelationshipType.values());
        model.addAttribute("sexualityTypes", Member.Sexuality.values());
        model.addAttribute("sexTypes", Member.Sex.values());
        return "pages/interpersonal/newMember";
    }
}
