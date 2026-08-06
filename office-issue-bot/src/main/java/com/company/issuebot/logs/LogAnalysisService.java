package com.company.issuebot.logs;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class LogAnalysisService {

    public LogAnalysisResult analyze(LogAnalysisRequest request) {
        Set<String> suspiciousPatterns = new LinkedHashSet<>();
        Set<String> impactedComponents = new LinkedHashSet<>();
        List<String> recommendedChecks = new ArrayList<>();

        for (String line : request.logLines()) {
            String normalized = line.toLowerCase(Locale.ROOT);
            if (normalized.contains("timeout")) {
                suspiciousPatterns.add("Timeout detected in log flow");
                impactedComponents.add("Network or downstream dependency");
            }
            if (normalized.contains("connection refused") || normalized.contains("socket")) {
                suspiciousPatterns.add("Connectivity issue");
                impactedComponents.add("Infrastructure or target host");
            }
            if (normalized.contains("ora-") || normalized.contains("sqlstate") || normalized.contains("deadlock")) {
                suspiciousPatterns.add("Database exception");
                impactedComponents.add("Database layer");
            }
            if (normalized.contains("nullpointerexception") || normalized.contains("indexoutofbounds")) {
                suspiciousPatterns.add("Application code defect");
                impactedComponents.add("Application service");
            }
            if (normalized.contains("kafka") || normalized.contains("mq") || normalized.contains("queue")) {
                impactedComponents.add("Messaging layer");
            }
        }

        if (suspiciousPatterns.isEmpty()) {
            suspiciousPatterns.add("No known signature matched, manual inspection recommended");
        }

        recommendedChecks.add("Validate the exact error timestamp against deployment and restart history.");
        recommendedChecks.add("Check whether the same incident appears across dependent services.");
        if (impactedComponents.contains("Database layer")) {
            recommendedChecks.add("Inspect schema objects, recent locks, and failing procedures for the impacted module.");
        }
        if (impactedComponents.contains("Infrastructure or target host")) {
            recommendedChecks.add("Verify DNS, firewall, and service availability from the application host.");
        }

        String probableCause = suspiciousPatterns.iterator().next();
        String summary = "Incident " + request.incidentId()
                + " shows "
                + suspiciousPatterns.size()
                + " notable signal(s) in "
                + request.logLines().size()
                + " supplied log lines.";

        return new LogAnalysisResult(
                summary,
                probableCause,
                List.copyOf(impactedComponents),
                recommendedChecks,
                List.copyOf(suspiciousPatterns)
        );
    }
}
