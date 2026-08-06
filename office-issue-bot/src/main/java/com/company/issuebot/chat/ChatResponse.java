package com.company.issuebot.chat;

import com.company.issuebot.db.DatabaseMetadataResponse;
import com.company.issuebot.logs.LogAnalysisResult;

public record ChatResponse(
        String answer,
        LogAnalysisResult logAnalysis,
        DatabaseMetadataResponse databaseMetadata
) {
}
