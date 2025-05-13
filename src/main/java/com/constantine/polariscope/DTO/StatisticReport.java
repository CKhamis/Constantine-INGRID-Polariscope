package com.constantine.polariscope.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class StatisticReport {
    int newMembers; // works
    int totalMembers; // works
    int events;
    int activityScore; // works
    HashMap<String, Integer> activityTypeScores; // works

    // cScore
    double overallSTD; // works
    double overallScoreAverage; //works
    long overallScoreCount; //works
    UUID topGrossing; // prob works
    double topScoreIncrease; // prob works
    UUID antiGrossing; // prob works
    double antiScoreDecrease; // prob works
    UUID topMember;// works
    int topMemberScore;// works
    UUID stable;// works
    double stableSTD;// works
    UUID unstable;// works
    double unstableSTD;// works

    // Network
    long numConnections;//works
    UUID mostConnections;//works
    long mostConnectionsCount;//works
    UUID leastConnections;//works
    long leastConnectionsCount;//works
    //String popular;
    HashMap<String, Double> categoryScoreAverage;//works
    UUID mostControversial;//works
    double mostControversialSTD;//works
    UUID leastControversial;//works
    double leastControversialSTD;//works

    // Group
    UUID highestRated;
    double highestRatedScore;
    UUID lowestRated;
    double lowestRatedScore;
    HashMap<UUID, Double> groupAverageScore;
    UUID mostCohesive;
    double mostCohesiveSTD;
    UUID leastCohesive;
    double leastCohesiveSTD;

    @Override
    public String toString() {
        return "StatisticReport{" +
                "newMembers=" + newMembers +
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
                ", mostConnections=" + mostConnections +
                ", mostConnectionsCount=" + mostConnectionsCount +
                ", leastConnections=" + leastConnections +
                ", leastConnectionsCount=" + leastConnectionsCount +
                ", categoryScoreAverage=" + categoryScoreAverage +
                ", mostControversial=" + mostControversial +
                ", mostControversialSTD=" + mostControversialSTD +
                ", leastControversial=" + leastControversial +
                ", leastControversialSTD=" + leastControversialSTD +
                ", highestRated='" + highestRated + '\'' +
                ", highestRatedScore=" + highestRatedScore +
                ", groupAverageScoreChange=" + groupAverageScoreChange +
                ", mostCohesive='" + mostCohesive + '\'' +
                ", mostCohesiveSTD=" + mostCohesiveSTD +
                '}';
    }
}
