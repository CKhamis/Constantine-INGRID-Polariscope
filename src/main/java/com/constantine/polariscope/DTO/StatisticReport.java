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
    double overallSTD;
    UUID topGrossing;
    int topScoreIncrease;
    UUID antiGrossing;
    int antiScoreDecrease;
    UUID topMember;// works
    int topMemberScore;// works
    double scoreAverage;
    UUID stable;// works
    double stableSTD;// works
    UUID unstable;// works
    double unstableSTD;// works

    // Network
    String mostConnections;
    int mostConnectionsCount;
    String leastConnections;
    int leastConnectionsCount;
    String popular;
    HashMap<String, Integer> categoryScore;
    String mostControversial;
    double mostControversialSTD;

    // Group
    String highestRated;
    double highestRatedScore;
    HashMap<String, Integer> groupAverageScoreChange;
    String mostCohesive;
    double mostCohesiveSTD;

    @Override
    public String toString() {
        return "StatisticReport{" +
                "newMembers=" + newMembers +
                ", totalMembers=" + totalMembers +
                ", events=" + events +
                ", activityScore=" + activityScore +
                ", overallSTD=" + overallSTD +
                ", topGrossing='" + topGrossing + '\'' +
                ", topScoreIncrease=" + topScoreIncrease +
                ", antiGrossing='" + antiGrossing + '\'' +
                ", antiScoreDecrease=" + antiScoreDecrease +
                ", topMember='" + topMember + '\'' +
                ", topMemberScore=" + topMemberScore +
                ", scoreAverage=" + scoreAverage +
                ", stable=" + stable +
                ", stableSTD=" + stableSTD +
                ", unstable=" + unstable +
                ", unstableSTD=" + unstableSTD +
                ", mostConnections='" + mostConnections + '\'' +
                ", mostConnectionsCount=" + mostConnectionsCount +
                ", leastConnections='" + leastConnections + '\'' +
                ", leastConnectionsCount=" + leastConnectionsCount +
                ", popular='" + popular + '\'' +
                ", categoryScore=" + categoryScore +
                ", mostControversial='" + mostControversial + '\'' +
                ", mostControversialSTD=" + mostControversialSTD +
                ", highestRated='" + highestRated + '\'' +
                ", highestRatedScore=" + highestRatedScore +
                ", groupAverageScoreChange=" + groupAverageScoreChange +
                ", mostCohesive='" + mostCohesive + '\'' +
                ", mostCohesiveSTD=" + mostCohesiveSTD +
                ", activityScores=" + activityTypeScores.size() +
                '}';
    }
}
