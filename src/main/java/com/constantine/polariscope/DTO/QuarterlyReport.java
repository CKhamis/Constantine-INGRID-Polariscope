package com.constantine.polariscope.DTO;

import java.util.HashMap;

/***
 * A quarterly report of all input information. Cached in ram and most recent is regenerated
 */
public class QuarterlyReport {
    int newMembers;
    int totalMembers;
    int events;
    int activityScore;

    // cScore
    double overallSTD;
    String topGrossing;
    int topScoreIncrease;
    String antiGrossing;
    int antiScoreDecrease;
    String topMember;
    int topMemberScore;
    double scoreAverage;
    String stable;
    double stableSTD;
    String unstable;
    double unstableSTD;

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
}
