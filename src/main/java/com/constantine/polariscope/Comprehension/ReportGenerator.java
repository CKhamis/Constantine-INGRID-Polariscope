package com.constantine.polariscope.Comprehension;

import com.constantine.polariscope.DTO.MemberListItem;
import com.constantine.polariscope.DTO.QuarterlyReport;
import com.constantine.polariscope.Model.Evaluation;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Service.*;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Component
public class ReportGenerator {
    private final MemberService memberService;
    private final UserService userService;
    private final EvaluationService evaluationService;
    private final MemberGroupService groupService;
    private final ActivityLogService activityLogService;
    private final MemberGroupService memberGroupService;
    private final EventService eventService;
    private final RelationshipService relationshipService;

    @PostConstruct
    public void init() {
        generateReport();
    }

    @Scheduled(cron = "0 0 6,18 * * ?")
    public void generateReport() {
        HashMap<UUID, HashSet<QuarterlyReport>> quarterlyReport = new HashMap<>();
        System.out.println("Generating Quarterly report");

        // Get all users
        List<User> userList = userService.getAll();

        // Generate reports for each user. Will be slow, but it runs on a schedule
        for (User u : userList) {
            quarterlyReport.put(u.getId(), new HashSet<>());

            // Get all members of given user
            List<MemberListItem> memberList;
            try{
                memberList = memberService.allMembers(u);
            }catch (Exception e){
                System.out.println("Error reporting members: " + e.getMessage());
                continue;
            }

            for(MemberListItem m : memberList) {
                System.out.println("Processing user: " + m.getFirstName());
                for(Evaluation e : m.)
            }

        }

        // optionally update quarterlyReports
    }
}
