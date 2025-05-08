package com.constantine.polariscope.Comprehension;

import com.constantine.polariscope.DTO.MemberListItem;
import com.constantine.polariscope.DTO.QuarterlyReport;
import com.constantine.polariscope.Model.Evaluation;
import com.constantine.polariscope.Model.Member;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Service.*;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Component
@SessionScope
public class ReportGenerator {
    private final MemberService memberService;
    private final UserService userService;
    private final EvaluationService evaluationService;
    private final MemberGroupService groupService;
    private final ActivityLogService activityLogService;
    private final MemberGroupService memberGroupService;
    private final EventService eventService;
    private final RelationshipService relationshipService;

    /**
     * Generates ALL quarterly reports for the logged in user.
     */
    public void generateReport() {
        System.out.println("Generating Quarterly report");

        // todo: Put in a check to see if they are already made

        // Fetch currently logged in user information
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("No authenticated user found");
            return;
        }

        String username;

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User currentUser = (User) userService.loadUserByUsername(username);

        // Get list of members along with their associated evaluations
        List<Member> memberList = memberService.report(currentUser);

        // Create date intervals from when user was created to today


    }
}
