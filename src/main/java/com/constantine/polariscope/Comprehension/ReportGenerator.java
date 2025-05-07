package com.constantine.polariscope.Comprehension;

import com.constantine.polariscope.DTO.QuarterlyReport;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class ReportGenerator {
    public static HashSet<QuarterlyReport> quarterlyReports = new HashSet<>();

    @Scheduled(cron = "0 0 6,18 * * ?")
    public static void generateReport() {
        System.out.println("Generating Quarterly report");
    }
}
