package com.company.issuebot.logs;

import java.util.List;

public record LogAnalysisResult(
        String summary,
        String probableCause,
        List<String> impactedComponents,
        List<String> recommendedChecks,
        List<String> suspiciousPatterns
) {
}
