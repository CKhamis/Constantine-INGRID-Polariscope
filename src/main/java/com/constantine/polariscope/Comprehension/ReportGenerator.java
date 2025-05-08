package com.constantine.polariscope.Comprehension;

import com.constantine.polariscope.DTO.StatisticReport;
import com.constantine.polariscope.Model.ActivityLog;
import com.constantine.polariscope.Model.Member;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Service.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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

    private List<Member> memberList;
    private List<ActivityLog> activityLogList;

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
        memberList = memberService.report(currentUser);

        // Get list of activities
        activityLogList = activityLogService.findAll(currentUser);

        // Create date intervals from when user was created to today
        //run report for those intervals; persist them in database
        StatisticReport report = generateStatisticReport(LocalDate.of(2019, 1, 1), LocalDate.of(2025, 3, 31));
        System.out.println(report);
    }

    private StatisticReport generateStatisticReport(LocalDate startDate, LocalDate endDate) {
        StatisticReport statisticReport = new StatisticReport();

        List<ActivityLog> activityLogListSegment = activityLogList.stream().filter((log) ->  log.getCreated().isBefore(endDate.atStartOfDay()) && log.getCreated().isAfter(startDate.atStartOfDay())).toList();
        HashMap<String, Integer> activityTypeScores = new HashMap<>();

        for(ActivityLog activityLog : activityLogListSegment) {
            System.out.println(activityLog.getCreated());
            String type = activityLog.getActivityType().toString();

            if(activityTypeScores.containsKey(type)){
                activityTypeScores.put(type, activityTypeScores.get(type) + 1);
            }else{
                activityTypeScores.put(type, 1);
            }
        }

        statisticReport.setActivityScore(activityLogListSegment.size());
        statisticReport.setActivityTypeScores(activityTypeScores);


        int newMembers = 0;
        int totalMembers = 0;
        UUID largestSTDId = null;
        double largestSTD = 0;
        UUID smallestSTDId = null;
        double smallestSTD = Double.MAX_VALUE;
        UUID scoreMaxId = null;
        int scoreMax = 0;

        List<Integer> allScores = new ArrayList<>();

        for(Member member : memberList) {
            // Check for member creation
            if(member.getCreated().isAfter(startDate.atStartOfDay()) && member.getCreated().isBefore(endDate.atStartOfDay())){
                newMembers++;
            }

            // Check for total members by the end of the period
            if(member.getCreated().isBefore(endDate.atStartOfDay())){
                totalMembers++;
            }

            // Calculating cScore
            List<Integer> memberScores = new ArrayList<>();

            member.getTimeline().stream().filter((eval) -> eval.getTimestamp().isBefore(endDate.atStartOfDay()) && eval.getTimestamp().isAfter(startDate.atStartOfDay())).forEach((eval) -> {
                memberScores.add(eval.getCScore());
                System.out.print(eval.getCScore() + " ");
            });

            double[] memberArray = memberScores.stream().mapToDouble(value -> (double) value).toArray();
            StandardDeviation sd = new StandardDeviation(false);
            double std = sd.evaluate(memberArray);

            if(std < smallestSTD && memberArray.length > 3){
                smallestSTD = std;
                smallestSTDId = member.getId();
            }

            if(std > largestSTD && memberArray.length > 3){
                largestSTD = std;
                largestSTDId = member.getId();
            }

            // Max Score
            if(member.getTimeline().getLast().getCScore() > scoreMax){
                scoreMax = member.getTimeline().getLast().getCScore();
                scoreMaxId = member.getId();
            }

            allScores.addAll(memberScores);
            System.out.println(std);
        }

        // Calculate cScore average

        // Calculate overall sd
        double[] totalScoreArray = allScores.stream().mapToDouble(value -> (double) value).toArray();
        StandardDeviation sd = new StandardDeviation(false);
        statisticReport.setOverallSTD(sd.evaluate(totalScoreArray));

        // Calculate overall score average
        long totalScore = 0;
        for(int s : allScores){
            totalScore += s;
        }

        statisticReport.setOverallScoreCount(totalScore);
        statisticReport.setOverallScoreAverage((double) totalScore /allScores.size());

        // Write accumulated data to report
        statisticReport.setNewMembers(newMembers);
        statisticReport.setTotalMembers(totalMembers);

        statisticReport.setStable(smallestSTDId);
        statisticReport.setStableSTD(smallestSTD);

        statisticReport.setUnstable(largestSTDId);
        statisticReport.setUnstableSTD(largestSTD);

        statisticReport.setTopMemberScore(scoreMax);
        statisticReport.setTopMember(scoreMaxId);

        return statisticReport;
    }
}
