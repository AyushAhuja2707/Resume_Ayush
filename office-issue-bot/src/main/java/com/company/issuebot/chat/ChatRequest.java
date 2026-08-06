package com.company.issuebot.chat;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ChatRequest(
        @NotBlank String sessionId,
        @NotBlank String userMessage,
        List<String> pastedLogs,
        String schemaName,
        String objectName,
        boolean includeProcedures
) {
}
