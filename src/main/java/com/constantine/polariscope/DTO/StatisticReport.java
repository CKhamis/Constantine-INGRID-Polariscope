package com.constantine.polariscope.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class StatisticReport {
    private LocalDate startDate;
    private LocalDate endDate;
    private int newMemberCount; // only counting ones within the time period
    private int memberCount; // cumulative
    private int events;
    private int newActivityScore; // non-cumulative
    private int activityScore; // cumulative

    // cScore
    private double overallSD;
    private double overallScoreAverage;
    private long overallScoreTotal;
    private long overallScoreCount;

    // Network
    private long overallConnectionCount;
    private double overallConnectionAverage;
    private double overallConnectionSD;


    // Human Statistics
    ArrayList<MemberReport> memberReports;
}