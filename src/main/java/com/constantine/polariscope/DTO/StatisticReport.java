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
    int events; // works
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
    HashMap<UUID, Double> groupAverageScore;//works
    UUID mostCohesive;//works
    double mostCohesiveSTD;//works
    UUID leastCohesive;//works
    double leastCohesiveSTD;//works
}
