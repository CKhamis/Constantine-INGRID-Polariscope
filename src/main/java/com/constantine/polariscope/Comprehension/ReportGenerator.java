package com.constantine.polariscope.Comprehension;

import com.constantine.polariscope.DTO.StatisticReport;
import com.constantine.polariscope.Model.*;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.constantine.polariscope.Service.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    private List<Relationship> relationshipList;
    private List<MemberGroup> groupList;

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

        // Get list of relationships
        relationshipList = relationshipService.findAll(currentUser);

        // Get list of groups
        groupList = groupService.findAll(currentUser);

        // Create date intervals from when user was created to today
        //run report for those intervals; persist them in database
        StatisticReport report = generateStatisticReport(LocalDate.of(2019, 1, 1), LocalDate.of(2025, 3, 31));
        System.out.println(report);
    }

    private StatisticReport generateStatisticReport(LocalDate startDate, LocalDate endDate) {
        StatisticReport statisticReport = new StatisticReport();

        List<ActivityLog> activityLogListSegment = activityLogList.stream().filter((log) ->  log.getCreated().isBefore(endDate.atStartOfDay()) && log.getCreated().isAfter(startDate.atStartOfDay())).toList();
        HashMap<String, Integer> activityTypeScores = new HashMap<>();

        for(ActivityLog activityLog : activityLogListSegment) {String type = activityLog.getActivityType().toString();

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
        UUID topGrossing = null;
        double topGrossingSlope = 0;
        UUID antiGrossing = null;
        double antiGrossingSlope = Double.MAX_VALUE;

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
            SimpleRegression regression = new SimpleRegression();

            List<Evaluation> scopedTimeline = member.getTimeline().stream().filter((eval) -> eval.getTimestamp().isBefore(endDate.atStartOfDay()) && eval.getTimestamp().isAfter(startDate.atStartOfDay())).sorted(Comparator.comparing(Evaluation::getTimestamp)).toList();

            for (Evaluation evaluation : scopedTimeline) {
                long x = ChronoUnit.DAYS.between(startDate, evaluation.getTimestamp().toLocalDate());
                int y = evaluation.getCScore();
                memberScores.add(y);
                regression.addData(x, y);
            }

            double[] memberArray = memberScores.stream().mapToDouble(value -> (double) value).toArray();
            StandardDeviation sd = new StandardDeviation(false);
            double std = sd.evaluate(memberArray);

            double bfs = regression.getSlope();

            if(bfs > topGrossingSlope){
                topGrossingSlope = bfs;
                topGrossing = member.getId();
            }

            if(bfs < antiGrossingSlope){
                antiGrossingSlope = bfs;
                antiGrossing = member.getId();
            }

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
        }

        // Write accumulated cScore data to report
        statisticReport.setNewMembers(newMembers);
        statisticReport.setTotalMembers(totalMembers);

        statisticReport.setStable(smallestSTDId);
        statisticReport.setStableSTD(smallestSTD);

        statisticReport.setUnstable(largestSTDId);
        statisticReport.setUnstableSTD(largestSTD);

        statisticReport.setTopMemberScore(scoreMax);
        statisticReport.setTopMember(scoreMaxId);

        statisticReport.setTopGrossing(topGrossing);
        statisticReport.setTopScoreIncrease(topGrossingSlope);

        statisticReport.setAntiGrossing(antiGrossing);
        statisticReport.setAntiScoreDecrease(antiGrossingSlope);

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

        // Member network information
        UUID mostConnectionsId = null;
        long mostConnectionsCount = 0;
        UUID leastConnectionsId = null;
        long leastConnectionsCount = Long.MAX_VALUE;
        HashMap<UUID, Integer> connectionTotal = new HashMap<>();
        HashMap<UUID, HashSet<Integer>> incomingRatings = new HashMap<>();
        HashMap<String, HashSet<Integer>> relationshipTypeRatings = new HashMap<>();
        long relationCount = 0;

        for(Relationship r : relationshipList){
            UUID receiver = r.getOther().getId();
            // filter out all dates that are not in the right period
            if(r.getLastModified().isAfter(startDate.atStartOfDay()) && r.getLastModified().isBefore(endDate.atStartOfDay())){
                relationCount++;

                if(connectionTotal.containsKey(receiver)){
                    connectionTotal.put(receiver, connectionTotal.get(receiver) + 1);
                }else{
                    connectionTotal.put(receiver, 1);
                }

                if(connectionTotal.get(receiver) > mostConnectionsCount){
                    mostConnectionsCount = connectionTotal.get(receiver);
                    mostConnectionsId = receiver;
                }
                if(connectionTotal.get(receiver) < leastConnectionsCount){
                    leastConnectionsCount = connectionTotal.get(receiver);
                    leastConnectionsId = receiver;
                }

                if(incomingRatings.containsKey(receiver)){
                    incomingRatings.get(receiver).add(r.getHealth());
                }else{
                    incomingRatings.put(receiver, new HashSet<>());
                    incomingRatings.get(receiver).add(r.getHealth());
                }

                if(relationshipTypeRatings.containsKey(String.valueOf(r.getType()))){
                    relationshipTypeRatings.get(String.valueOf(r.getType())).add(r.getHealth());
                }else{
                    relationshipTypeRatings.put(String.valueOf(r.getType()), new HashSet<>());
                    relationshipTypeRatings.get(String.valueOf(r.getType())).add(r.getHealth());
                }
            }
        }
        statisticReport.setNumConnections(relationCount);

        // Calculate category scores
        HashMap<String, Double> categoryScore = new HashMap<>();
        for(String key : relationshipTypeRatings.keySet()){
            int total = 0;
            for(Integer score : relationshipTypeRatings.get(key)){
                total += score;
            }
            categoryScore.put(key, (double) total / relationshipTypeRatings.get(key).size());
        }
        statisticReport.setCategoryScoreAverage(categoryScore);

        // Calculate controversial members
        UUID mostControversialId = null;
        double mostControversialScore = 0;
        UUID leastControversialId = null;
        double leastControversialScore = Double.MAX_VALUE;

        for(UUID memberId : incomingRatings.keySet()){
            if(incomingRatings.get(memberId).size() < 3){
                // remove all members who do not have enough evals
                continue;
            }
            StandardDeviation incomingSD = new StandardDeviation(false);
            double[] data = new double[incomingRatings.get(memberId).size()];
            int i = 0;
            for(int score : incomingRatings.get(memberId)){
                data[i] = score;
                i++;
            }
            double memberSD = incomingSD.evaluate(data);

            if(memberSD > mostControversialScore){
                mostControversialId = memberId;
                mostControversialScore = memberSD;
            }

            if(memberSD < leastControversialScore){
                leastControversialId = memberId;
                leastControversialScore = memberSD;
            }
        }
        statisticReport.setMostControversial(mostControversialId);
        statisticReport.setMostControversialSTD(mostControversialScore);
        statisticReport.setLeastControversial(leastControversialId);
        statisticReport.setLeastControversialSTD(leastControversialScore);

        statisticReport.setMostConnections(mostConnectionsId);
        statisticReport.setMostConnectionsCount(mostConnectionsCount);
        statisticReport.setLeastConnections(leastConnectionsId);
        statisticReport.setLeastConnectionsCount(leastConnectionsCount);

        UUID highestRatedGroup = null;
        double highestRatedScore = 0;
        UUID lowestRatedGroup = null;
        double lowestRatedScore = Double.MAX_VALUE;
        HashMap<UUID, Double> averageGroupRatings = new HashMap<>();
        UUID mostCohesiveGroup = null;
        double mostCohesiveScore = 0;
        UUID leastCohesiveGroup = null;
        double leastCohesiveScore = Double.MAX_VALUE;

        for()


        System.out.println(statisticReport);
        return statisticReport;
    }
}
