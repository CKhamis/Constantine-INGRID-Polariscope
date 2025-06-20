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

    // events
    private int eventsIncluded;

    // network
    private int connectionsCount;
    private int connectionsTotal;
    private int connectionsAverage;
    private double connectionsSD;
}
