package com.constantine.polariscope.DTO;

import com.constantine.polariscope.Model.Evaluation;
import lombok.*;

import java.util.UUID;

/**
 * To be included within an array for member comparison.
 * Data points are to be derived from within the selected range of dates and not cumulative
 * except for network statistics and "counts"
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberReport {
    private UUID id;
    private String firstName;
    private String lastName;

    // eval scores
    private Evaluation startScore;
    private Evaluation endScore;
    private double scoreAverage;
    private int scoreTotal; // summation of scores
    private double scoreSD;
    private int scoreCount; // amount of scores
    private double scoreSlope;

    // events
    private int eventsIncluded;

    // network
    private int connectionsCount;

    // network incoming
    private int incomingConnectionsCount;
    private double incomingConnectionsSD;
    private double incomingConnectionsAverage;

    // network outgoing
    private int outgoingConnectionsCount;
    private double outgoingConnectionsSD;
    private double outgoingConnectionsAverage;
}
