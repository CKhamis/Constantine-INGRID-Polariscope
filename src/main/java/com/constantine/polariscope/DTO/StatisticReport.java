package com.constantine.polariscope.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class StatisticReport {
    private LocalDate startDate;
    private LocalDate endDate;
    private int newMembers; // works
    private int totalMembers; // works
    private int events; // works
    private int activityScore; // works
    private HashMap<String, Integer> activityTypeScores; // works

    // cScore
    private double overallSTD; // works
    private double overallScoreAverage; //works
    private long overallScoreCount; //works
    private UUID topGrossing; // prob works
    private double topScoreIncrease; // prob works
    private UUID antiGrossing; // prob works
    private double antiScoreDecrease; // prob works
    private UUID topMember;// works
    private int topMemberScore;// works
    private UUID stable;// works
    private double stableSTD;// works
    private UUID unstable;// works
    private double unstableSTD;// works

    // Network
    private long numConnections;//works
    private UUID mostConnections;//works
    private long mostConnectionsCount;//works
    private UUID leastConnections;//works
    private long leastConnectionsCount;//works
    //String popular;
    private HashMap<String, Double> categoryScoreAverage;//works
    private UUID mostControversial;//works
    private double mostControversialSTD;//works
    private UUID leastControversial;//works
    private double leastControversialSTD;//works

    // Group
    private HashMap<UUID, Double> groupAverageScore;//works
    private UUID mostCohesive;//works
    private double mostCohesiveSTD;//works
    private UUID leastCohesive;//works
    private double leastCohesiveSTD;//works

    @Override
    public String toString() {
        return "StatisticReport{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", newMembers=" + newMembers +
                ", totalMembers=" + totalMembers +
                ", events=" + events +
                ", activityScore=" + activityScore +
                ", activityTypeScores=" + activityTypeScores +
                ", overallSTD=" + overallSTD +
                ", overallScoreAverage=" + overallScoreAverage +
                ", overallScoreCount=" + overallScoreCount +
                ", topGrossing=" + topGrossing +
                ", topScoreIncrease=" + topScoreIncrease +
                ", antiGrossing=" + antiGrossing +
                ", antiScoreDecrease=" + antiScoreDecrease +
                ", topMember=" + topMember +
                ", topMemberScore=" + topMemberScore +
                ", stable=" + stable +
                ", stableSTD=" + stableSTD +
                ", unstable=" + unstable +
                ", unstableSTD=" + unstableSTD +
                ", numConnections=" + numConnections +
                ", mostConnections=" + mostConnections +
                ", mostConnectionsCount=" + mostConnectionsCount +
                ", leastConnections=" + leastConnections +
                ", leastConnectionsCount=" + leastConnectionsCount +
                ", categoryScoreAverage=" + categoryScoreAverage +
                ", mostControversial=" + mostControversial +
                ", mostControversialSTD=" + mostControversialSTD +
                ", leastControversial=" + leastControversial +
                ", leastControversialSTD=" + leastControversialSTD +
                ", groupAverageScore=" + groupAverageScore +
                ", mostCohesive=" + mostCohesive +
                ", mostCohesiveSTD=" + mostCohesiveSTD +
                ", leastCohesive=" + leastCohesive +
                ", leastCohesiveSTD=" + leastCohesiveSTD +
                '}';
    }
}
