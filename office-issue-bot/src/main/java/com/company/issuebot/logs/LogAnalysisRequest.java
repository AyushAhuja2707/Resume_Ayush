package com.company.issuebot.logs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record LogAnalysisRequest(
        @NotBlank String incidentId,
        @NotEmpty List<String> logLines,
        String systemName,
        String environment,
        String userQuestion
) {
}
