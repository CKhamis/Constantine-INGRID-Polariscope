package com.constantine.polariscope.Comprehension;

import com.constantine.polariscope.DTO.MemberReport;
import com.constantine.polariscope.DTO.StatisticReport;
import com.constantine.polariscope.Model.*;
import lombok.Getter;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.constantine.polariscope.Service.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@SessionScope
public class ReportGenerator {
    private final MemberService memberService;
    private final UserService userService;
    private final ActivityLogService activityLogService;
    private final EventService eventService;
    private final RelationshipService relationshipService;

    public ReportGenerator(MemberService memberService, UserService userService, ActivityLogService activityLogService, EventService eventService, RelationshipService relationshipService) {
        this.memberService = memberService;
        this.userService = userService;
        this.activityLogService = activityLogService;
        this.eventService = eventService;
        this.relationshipService = relationshipService;
    }

    private User loggedInUser;
    private List<Member> memberList;
    private List<ActivityLog> activityLogList;
    private List<Relationship> relationshipList;
    private List<Event> eventList;

    @Getter
    private List<StatisticReport> quarterlyReportList;
    @Getter
    private boolean quarterlyReportFinished = false;
    @Getter
    private HashMap<String, StatisticReport> customReports;

    /**
     * Generates ALL quarterly reports for the logged in user.
     */
    public void generateReport() {
        System.out.println("Generating Quarterly report");

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
        loggedInUser = currentUser;

        // Get list of members along with their associated evaluations
        memberList = memberService.report(currentUser);

        // Get list of activities
        activityLogList = activityLogService.findAll(currentUser);

        // Get list of relationships
        relationshipList = relationshipService.findAll(currentUser);

        // Get list of events
        eventList = eventService.findAllByAuthor(currentUser);

        // Create date intervals from when user was created to today
        //run report for those intervals; persist them in database
        generateQuarterlyReports();
    }

    private void generateQuarterlyReports() {
        quarterlyReportList = new ArrayList<>();
        quarterlyReportFinished = false;

        // get creation date of user
        LocalDate start = loggedInUser.getCreated().toLocalDate();
        start = getQuarterStart(start);

        LocalDate now = LocalDate.now();

        while(start.isBefore(now)){
            LocalDate end = start.plusMonths(3).minusDays(1);
            if(end.isAfter(now)){
                end = now;
            }

            //System.out.println(start.getMonthValue() + "/"+ start.getDayOfMonth() + "/"+ start.getYear()+" to "  + end.getMonthValue() + "/"+ end.getDayOfMonth() + "/"+ end.getYear());
            StatisticReport report = generateStatisticReport(start, end);

            quarterlyReportList.add(report);

            start = start.plusMonths(3);
        }
        quarterlyReportFinished = true;
    }

    private LocalDate getQuarterStart(LocalDate date) {
        int month = date.getMonthValue();
        int quarterStartMonth = ((month - 1) / 3) * 3 + 1;
        return LocalDate.of(date.getYear(), quarterStartMonth, 1);
    }


    private StatisticReport generateStatisticReport(LocalDate startDate, LocalDate endDate) {
        StatisticReport statisticReport = new StatisticReport();
        statisticReport.setStartDate(startDate);
        statisticReport.setEndDate(endDate);

        List<Member> currentMembers = memberList.stream().filter(member -> member.getCreated().isBefore(endDate.atStartOfDay())).toList();
        statisticReport.setMemberCount(currentMembers.size());
        List<Member> newMembers = currentMembers.stream().filter(member -> member.getCreated().isAfter(startDate.atStartOfDay())).toList();
        statisticReport.setNewMemberCount(newMembers.size());
        int events = eventList.stream().filter(event -> event.getCreated().isBefore(endDate.atStartOfDay()) && event.getCreated().isAfter(startDate.atStartOfDay())).toList().size();
        statisticReport.setEvents(events);

        List<ActivityLog> currentLogs = activityLogList.stream().filter(log -> log.getCreated().isBefore(endDate.atStartOfDay())).toList();
        statisticReport.setActivityScore(currentLogs.size());
        List<ActivityLog> newLogs = currentLogs.stream().filter(event -> event.getCreated().isAfter(startDate.atStartOfDay())).toList();
        statisticReport.setNewActivityScore(newLogs.size());

        ArrayList<MemberReport> memberReports = new ArrayList<>();
        List<Integer> allScores = new ArrayList<>();
        long scoreTotal = 0;

        // using the total list of members, even though some members wouldnt have been created yet
        // this is mostly a design thing, so this might change depending on what's more intuitive
        for(Member member : memberList) {
            MemberReport memberReport = new MemberReport();
            memberReport.setId(member.getId());
            memberReport.setFirstName(member.getFirstName());
            memberReport.setLastName(member.getLastName());

            List<Evaluation> scopedTimeline = member.getTimeline().stream().filter((eval) -> eval.getTimestamp().isBefore(endDate.atStartOfDay()) && eval.getTimestamp().isAfter(startDate.atStartOfDay())).sorted(Comparator.comparing(Evaluation::getTimestamp)).toList();

            if(!scopedTimeline.isEmpty()) {
                memberReport.setStartScore(scopedTimeline.get(0));
                memberReport.setEndScore(scopedTimeline.get(scopedTimeline.size() - 1));
            }

            SimpleRegression regression = new SimpleRegression();
            List<Integer> memberScores = new ArrayList<>();
            long memberTotal = 0L;

            // Go through all evals for each member
            for(Evaluation evaluation : scopedTimeline) {
                memberScores.add(evaluation.getCScore());
                allScores.add(evaluation.getCScore());

                scoreTotal += evaluation.getCScore();
                memberTotal += evaluation.getCScore();

                long x = ChronoUnit.DAYS.between(startDate, evaluation.getTimestamp().toLocalDate());
                int y = evaluation.getCScore();
                regression.addData(x, y);
            }

            memberReport.setScoreAverage((double) memberTotal /scopedTimeline.size());
            memberReport.setScoreTotal(memberTotal);
            memberReport.setScoreCount(memberScores.size());

            double slope = regression.getSlope();
            memberReport.setScoreSlope(slope);

            double[] memberArray = memberScores.stream().mapToDouble(value -> (double) value).toArray();
            StandardDeviation standardDeviation = new StandardDeviation(false);
            double sd = standardDeviation.evaluate(memberArray);
            memberReport.setScoreSD(sd);

            // events
            memberReport.setEventsIncluded(member.getEvents().size());

            // network
            List<Relationship> incomingRelationships = relationshipList.stream().filter((relationship -> {return relationship.getOther().getId().equals(member.getId());})).toList();
            List<Relationship> outgoingRelationships = relationshipList.stream().filter((relationship -> {return relationship.getSelf().getId().equals(member.getId());})).toList();

            memberReport.setConnectionsCount(incomingRelationships.size() + outgoingRelationships.size());


            // Outgoing connections
            List<Integer> outgoingConnections = new ArrayList<>();
            long outgoingConnectionsTotal = 0L;

            for(Relationship relationship : outgoingRelationships) {
                outgoingConnections.add(relationship.getHealth());
                outgoingConnectionsTotal += relationship.getHealth();
            }

            memberReport.setOutgoingConnectionsCount(outgoingRelationships.size());
            memberReport.setOutgoingConnectionsAverage((double) outgoingConnectionsTotal / outgoingRelationships.size());

            double[] outgoingConnectionsArray = outgoingConnections.stream().mapToDouble(value -> (double) value).toArray();
            StandardDeviation outgoingConnectionsSD = new StandardDeviation(false);
            double osd = outgoingConnectionsSD.evaluate(outgoingConnectionsArray);
            memberReport.setOutgoingConnectionsSD(osd);

            // Incoming connections
            List<Integer> incomingConnections = new ArrayList<>();
            long incomingConnectionsTotal = 0L;

            for(Relationship relationship : incomingRelationships) {
                incomingConnections.add(relationship.getHealth());
                incomingConnectionsTotal += relationship.getHealth();
            }

            memberReport.setIncomingConnectionsCount(incomingRelationships.size());
            memberReport.setIncomingConnectionsAverage((double) incomingConnectionsTotal / incomingRelationships.size());

            double[] incomingConnectionsArray = incomingConnections.stream().mapToDouble(value -> (double) value).toArray();
            StandardDeviation incomingConnectionsSD = new StandardDeviation(false);
            double isd = incomingConnectionsSD.evaluate(incomingConnectionsArray);
            memberReport.setIncomingConnectionsSD(isd);

            memberReports.add(memberReport);
        }
        statisticReport.setMemberReports(memberReports);

        double[] scoreArray = allScores.stream().mapToDouble(value -> (double) value).toArray();
        StandardDeviation sd = new StandardDeviation(false);
        statisticReport.setOverallScoreSD(sd.evaluate(scoreArray));

        statisticReport.setOverallScoreAverage((double) scoreTotal / allScores.size());
        statisticReport.setOverallScoreCount(allScores.size());
        statisticReport.setOverallScoreTotal(scoreTotal);

        // connections
        List<Relationship> scopedConnections = relationshipList.stream().filter(event -> event.getCreated().isBefore(endDate.atStartOfDay()) && event.getCreated().isAfter(startDate.atStartOfDay())).toList();
        long connectionTotal = 0;

        for(Relationship relationship : scopedConnections) {
            connectionTotal += relationship.getHealth();
        }

        double[] connectionArray = scopedConnections.stream().mapToDouble(rel -> (double) rel.getHealth()).toArray();
        StandardDeviation casd = new StandardDeviation(false);
        statisticReport.setOverallConnectionSD(casd.evaluate(connectionArray));

        statisticReport.setOverallConnectionCount(scopedConnections.size());
        statisticReport.setOverallConnectionAverage((double) connectionTotal / scopedConnections.size());


        return statisticReport;
    }


//        private StatisticReport generateStatisticReportOld(LocalDate startDate, LocalDate endDate) {
//        StatisticReport statisticReport = new StatisticReport();
//
//        statisticReport.setStartDate(startDate);
//        statisticReport.setEndDate(endDate);
//
//        List<ActivityLog> activityLogListSegment = activityLogList.stream().filter((log) ->  log.getCreated().isBefore(endDate.atStartOfDay()) && log.getCreated().isAfter(startDate.atStartOfDay())).toList();
//        HashMap<String, Integer> activityTypeScores = new HashMap<>();
//
//        for(ActivityLog activityLog : activityLogListSegment) {String type = activityLog.getActivityType().toString();
//
//            if(activityTypeScores.containsKey(type)){
//                activityTypeScores.put(type, activityTypeScores.get(type) + 1);
//            }else{
//                activityTypeScores.put(type, 1);
//            }
//        }
//
//        statisticReport.setActivityScore(activityLogListSegment.size());
//        statisticReport.setActivityTypeScores(activityTypeScores);
//
//
//        int newMembers = 0;
//        int totalMembers = 0;
//        UUID largestSTDId = null;
//        double largestSTD = 0;
//        UUID smallestSTDId = null;
//        double smallestSTD = Double.MAX_VALUE;
//        UUID scoreMaxId = null;
//        int scoreMax = 0;
//        UUID topGrossing = null;
//        double topGrossingSlope = 0;
//        UUID antiGrossing = null;
//        double antiGrossingSlope = Double.MAX_VALUE;
//
//        List<Integer> allScores = new ArrayList<>();
//
//        HashMap<UUID, ArrayList<Integer>> groupScores = new HashMap<>();
//
//        for(Member member : memberList) {
//            // Check for member creation
//            if(member.getCreated().isAfter(startDate.atStartOfDay()) && member.getCreated().isBefore(endDate.atStartOfDay())){
//                newMembers++;
//            }
//
//            // Check for total members by the end of the period
//            if(member.getCreated().isBefore(endDate.atStartOfDay())){
//                totalMembers++;
//            }
//
//            // Calculating cScore
//            List<Integer> memberScores = new ArrayList<>();
//            SimpleRegression regression = new SimpleRegression();
//
//            List<Evaluation> scopedTimeline = member.getTimeline().stream().filter((eval) -> eval.getTimestamp().isBefore(endDate.atStartOfDay()) && eval.getTimestamp().isAfter(startDate.atStartOfDay())).sorted(Comparator.comparing(Evaluation::getTimestamp)).toList();
//
//            if(member.getGroup()!=null && !scopedTimeline.isEmpty()){
//                if(!groupScores.containsKey(member.getGroup().getId())){
//                    groupScores.put(member.getGroup().getId(), new ArrayList<>());
//                }
//                groupScores.get(member.getGroup().getId()).add(scopedTimeline.getLast().getCScore());
//            }
//
//            for (Evaluation evaluation : scopedTimeline) {
//                long x = ChronoUnit.DAYS.between(startDate, evaluation.getTimestamp().toLocalDate());
//                int y = evaluation.getCScore();
//                memberScores.add(y);
//                regression.addData(x, y);
//            }
//
//            double[] memberArray = memberScores.stream().mapToDouble(value -> (double) value).toArray();
//            StandardDeviation sd = new StandardDeviation(false);
//            double std = sd.evaluate(memberArray);
//
//            double bfs = regression.getSlope();
//
//            if(bfs > topGrossingSlope){
//                topGrossingSlope = bfs;
//                topGrossing = member.getId();
//            }
//
//            if(bfs < antiGrossingSlope){
//                antiGrossingSlope = bfs;
//                antiGrossing = member.getId();
//            }
//
//            if(std < smallestSTD && memberArray.length > 3){
//                smallestSTD = std;
//                smallestSTDId = member.getId();
//            }
//
//            if(std > largestSTD && memberArray.length > 3){
//                largestSTD = std;
//                largestSTDId = member.getId();
//            }
//
//            // Max Score
//            if(member.getTimeline().getLast().getCScore() > scoreMax){
//                scoreMax = member.getTimeline().getLast().getCScore();
//                scoreMaxId = member.getId();
//            }
//
//            allScores.addAll(memberScores);
//        }
//
//        // Write accumulated cScore data to report
//        statisticReport.setNewMembers(newMembers);
//        statisticReport.setTotalMembers(totalMembers);
//
//        statisticReport.setStable(smallestSTDId);
//        statisticReport.setStableSTD(smallestSTD);
//
//        statisticReport.setUnstable(largestSTDId);
//        statisticReport.setUnstableSTD(largestSTD);
//
//        statisticReport.setTopMemberScore(scoreMax);
//        statisticReport.setTopMember(scoreMaxId);
//
//        statisticReport.setTopGrossing(topGrossing);
//        statisticReport.setTopScoreIncrease(topGrossingSlope);
//
//        statisticReport.setAntiGrossing(antiGrossing);
//        statisticReport.setAntiScoreDecrease(antiGrossingSlope);
//
//        // Calculate overall sd
//        double[] totalScoreArray = allScores.stream().mapToDouble(value -> (double) value).toArray();
//        StandardDeviation sd = new StandardDeviation(false);
//        statisticReport.setOverallSTD(sd.evaluate(totalScoreArray));
//
//        // Calculate overall score average
//        long totalScore = 0;
//        for(int s : allScores){
//            totalScore += s;
//        }
//
//        statisticReport.setOverallScoreCount(totalScore);
//        statisticReport.setOverallScoreAverage((double) totalScore /allScores.size());
//
//        // Member network information
//        UUID mostConnectionsId = null;
//        long mostConnectionsCount = 0;
//        UUID leastConnectionsId = null;
//        long leastConnectionsCount = Long.MAX_VALUE;
//        HashMap<UUID, Integer> connectionTotal = new HashMap<>();
//        HashMap<UUID, HashSet<Integer>> incomingRatings = new HashMap<>();
//        HashMap<String, HashSet<Integer>> relationshipTypeRatings = new HashMap<>();
//        long relationCount = 0;
//
//        for(Relationship r : relationshipList){
//            UUID receiver = r.getOther().getId();
//            // filter out all dates that are not in the right period
//            if(r.getLastModified().isAfter(startDate.atStartOfDay()) && r.getLastModified().isBefore(endDate.atStartOfDay())){
//                relationCount++;
//
//                if(connectionTotal.containsKey(receiver)){
//                    connectionTotal.put(receiver, connectionTotal.get(receiver) + 1);
//                }else{
//                    connectionTotal.put(receiver, 1);
//                }
//
//                if(connectionTotal.get(receiver) > mostConnectionsCount){
//                    mostConnectionsCount = connectionTotal.get(receiver);
//                    mostConnectionsId = receiver;
//                }
//                if(connectionTotal.get(receiver) < leastConnectionsCount){
//                    leastConnectionsCount = connectionTotal.get(receiver);
//                    leastConnectionsId = receiver;
//                }
//
//                if(incomingRatings.containsKey(receiver)){
//                    incomingRatings.get(receiver).add(r.getHealth());
//                }else{
//                    incomingRatings.put(receiver, new HashSet<>());
//                    incomingRatings.get(receiver).add(r.getHealth());
//                }
//
//                if(relationshipTypeRatings.containsKey(String.valueOf(r.getType()))){
//                    relationshipTypeRatings.get(String.valueOf(r.getType())).add(r.getHealth());
//                }else{
//                    relationshipTypeRatings.put(String.valueOf(r.getType()), new HashSet<>());
//                    relationshipTypeRatings.get(String.valueOf(r.getType())).add(r.getHealth());
//                }
//            }
//        }
//        statisticReport.setNumConnections(relationCount);
//
//        // Calculate category scores
//        HashMap<String, Double> categoryScore = new HashMap<>();
//        for(String key : relationshipTypeRatings.keySet()){
//            int total = 0;
//            for(Integer score : relationshipTypeRatings.get(key)){
//                total += score;
//            }
//            categoryScore.put(key, (double) total / relationshipTypeRatings.get(key).size());
//        }
//        statisticReport.setCategoryScoreAverage(categoryScore);
//
//        // Calculate controversial members
//        UUID mostControversialId = null;
//        double mostControversialScore = 0;
//        UUID leastControversialId = null;
//        double leastControversialScore = Double.MAX_VALUE;
//
//        for(UUID memberId : incomingRatings.keySet()){
//            if(incomingRatings.get(memberId).size() < 3){
//                // remove all members who do not have enough evals
//                continue;
//            }
//            StandardDeviation incomingSD = new StandardDeviation(false);
//            double[] data = new double[incomingRatings.get(memberId).size()];
//            int i = 0;
//            for(int score : incomingRatings.get(memberId)){
//                data[i] = score;
//                i++;
//            }
//            double memberSD = incomingSD.evaluate(data);
//
//            if(memberSD > mostControversialScore){
//                mostControversialId = memberId;
//                mostControversialScore = memberSD;
//            }
//
//            if(memberSD < leastControversialScore){
//                leastControversialId = memberId;
//                leastControversialScore = memberSD;
//            }
//        }
//        statisticReport.setMostControversial(mostControversialId);
//        statisticReport.setMostControversialSTD(mostControversialScore);
//        statisticReport.setLeastControversial(leastControversialId);
//        statisticReport.setLeastControversialSTD(leastControversialScore);
//
//        statisticReport.setMostConnections(mostConnectionsId);
//        statisticReport.setMostConnectionsCount(mostConnectionsCount);
//        statisticReport.setLeastConnections(leastConnectionsId);
//        statisticReport.setLeastConnectionsCount(leastConnectionsCount);
//
//        // Group analytics
//        HashMap<UUID, Double> averageGroupRatings = new HashMap<>();
//        UUID mostCohesiveGroup = null;
//        double mostCohesiveScore = Double.MAX_VALUE;
//        UUID leastCohesiveGroup = null;
//        double leastCohesiveScore = 0;
//
//        Set<UUID> groupIds = groupScores.keySet();
//        for(UUID groupId : groupIds){
//            ArrayList<Integer> array = groupScores.get(groupId);
//
//            StandardDeviation incomingSD = new StandardDeviation(false);
//            double[] scores = new double[array.size()];
//            long total = 0;
//
//            for(int j = 0; j < array.size(); j++){
//                scores[j] = array.get(j);
//                total += array.get(j);
//            }
//
//            double groupSD = incomingSD.evaluate(scores);
//            double average = (double) total / array.size();
//
//            if(groupSD < mostCohesiveScore){
//                mostCohesiveScore = groupSD;
//                mostCohesiveGroup = groupId;
//            }
//
//            if(groupSD > leastCohesiveScore){
//                leastCohesiveScore = groupSD;
//                leastCohesiveGroup = groupId;
//            }
//
//            averageGroupRatings.put(groupId, average);
//        }
//
//        statisticReport.setGroupAverageScore(averageGroupRatings);
//        statisticReport.setMostCohesive(mostCohesiveGroup);
//        statisticReport.setLeastCohesive(leastCohesiveGroup);
//        statisticReport.setMostCohesiveSTD(mostCohesiveScore);
//        statisticReport.setLeastCohesiveSTD(leastCohesiveScore);
//
//        List<Event> scopedEventList = eventList.stream().filter((log) ->  log.getCreated().isBefore(endDate.atStartOfDay()) && log.getCreated().isAfter(startDate.atStartOfDay())).toList();
//        statisticReport.setEvents(scopedEventList.size());
//
//        System.out.println(statisticReport);
//        return statisticReport;
//    }
}
