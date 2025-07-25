package com.constantine.polariscope.Controller;

import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Service.MemberGroupService;
import com.constantine.polariscope.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

import static com.constantine.polariscope.Intercept.WebSpyInterceptor.spying;

@Controller
@AllArgsConstructor
public class FrontEndController {
    private final UserService userService;
    private final MemberGroupService memberGroupService;
    private static final String VERSION = "3.0.0";

    private User getCurrentUser(Principal principal){
        if(principal == null){
            return new User();
        }
        String username = principal.getName();
        return (User) userService.loadUserByUsername(username);
    }

    @ModelAttribute
    public void addCommonAttributes(Model model, Principal principal) {
        model.addAttribute("version", VERSION);
        model.addAttribute("spying", spying.get());
    }

    @GetMapping("/")
    public String getHome() {
        return "pages/main/home";
    }
    @GetMapping("/login")
    public String getLogin() {
        return "pages/main/login";
    }
    @GetMapping("/tutorial")
    public String getTutorial() {
        return "pages/main/tutorial";
    }
    @GetMapping("/interpersonal")
    public String getInterpersonalHome() {
        return "pages/interpersonal/interpersonalHome";
    }

    @GetMapping("/interpersonal/settings")
    public String getInterpersonalSettings() {
        return "pages/interpersonal/interpersonalSettings";
    }

    @GetMapping("/interpersonal/reports")
    public String getInterpersonalReports() {
        return "pages/interpersonal/interpersonalReports";
    }

    @GetMapping("/interpersonal/members")
    public String getInterpersonalMembers(Model model) {
        model.addAttribute("relationshipTypes", Member.RelationshipType.values());
        model.addAttribute("sexualityTypes", Member.Sexuality.values());
        model.addAttribute("sexTypes", Member.Sex.values());
        return "pages/interpersonal/interpersonalMembers";
    }

    @GetMapping("/interpersonal/new")
    public String getNewMember(Model model, Principal principal) {
        model.addAttribute("relationshipTypes", Member.RelationshipType.values());
        model.addAttribute("sexualityTypes", Member.Sexuality.values());
        model.addAttribute("sexTypes", Member.Sex.values());
        return "pages/interpersonal/newMember";
    }

    @GetMapping("/interpersonal/member/view/{id}")
    public String getMember(@PathVariable String id, Model model, Principal principal){
        model.addAttribute("id", id);
        model.addAttribute("relationshipTypes", Member.RelationshipType.values());
        model.addAttribute("sexualityTypes", Member.Sexuality.values());
        model.addAttribute("sexTypes", Member.Sex.values());
        return "pages/interpersonal/interpersonalView";
    }

    @GetMapping("/interpersonal/compare")
    public String getCompare(Model model, Principal principal){
        model.addAttribute("relationshipTypes", Member.RelationshipType.values());
        model.addAttribute("sexualityTypes", Member.Sexuality.values());
        model.addAttribute("sexTypes", Member.Sex.values());
        return "pages/interpersonal/interpersonalCompare";
    }

    @GetMapping("/interpersonal/batch-evaluate")
    public String getBatchEvaluate() {
        return "pages/interpersonal/BatchEvaluate";
    }

    @GetMapping("/interpersonal/groups")
    public String getGroups(Model model) {
        model.addAttribute("relationshipTypes", Member.RelationshipType.values());
        return "pages/interpersonal/interpersonalGroups";
    }

    @GetMapping("/interpersonal/relationship")
    public String getRelationships(Model model, Principal principal) {
        model.addAttribute("relationshipTypes", Member.RelationshipType.values());
        model.addAttribute("groups", memberGroupService.findAll(getCurrentUser(principal)));
        return "pages/interpersonal/interpersonalRelationships";
    }

    @GetMapping("/interpersonal/import")
    public String getTransferTool() {
        return "pages/interpersonal/Import";
    }
}
