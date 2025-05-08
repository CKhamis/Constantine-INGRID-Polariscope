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
    int newMembers; // inconclusive
    int totalMembers; // does not work
    int events;
    int activityScore;

    // cScore
    double overallSTD;
    String topGrossing;
    int topScoreIncrease;
    String antiGrossing;
    int antiScoreDecrease;
    String topMember;// does not work
    int topMemberScore;// does not work
    double scoreAverage;
    UUID stable;// wrong
    double stableSTD;// wrong
    UUID unstable;// does not work
    double unstableSTD;// does not work

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
                '}';
    }
}
