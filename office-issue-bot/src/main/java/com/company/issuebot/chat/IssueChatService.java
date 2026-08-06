package com.company.issuebot.chat;

import com.company.issuebot.db.DatabaseMetadataRequest;
import com.company.issuebot.db.DatabaseMetadataResponse;
import com.company.issuebot.db.DatabaseMetadataService;
import com.company.issuebot.logs.LogAnalysisRequest;
import com.company.issuebot.logs.LogAnalysisResult;
import com.company.issuebot.logs.LogAnalysisService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueChatService {

    private final LogAnalysisService logAnalysisService;
    private final DatabaseMetadataService databaseMetadataService;

    public IssueChatService(LogAnalysisService logAnalysisService, DatabaseMetadataService databaseMetadataService) {
        this.logAnalysisService = logAnalysisService;
        this.databaseMetadataService = databaseMetadataService;
    }

    public ChatResponse respond(ChatRequest request) {
        LogAnalysisResult logAnalysis = null;
        if (request.pastedLogs() != null && !request.pastedLogs().isEmpty()) {
            logAnalysis = logAnalysisService.analyze(new LogAnalysisRequest(
                    request.sessionId(),
                    request.pastedLogs(),
                    "unknown-system",
                    "unknown-env",
                    request.userMessage()
            ));
        }

        DatabaseMetadataResponse databaseMetadata = null;
        if (request.schemaName() != null || request.objectName() != null || request.includeProcedures()) {
            databaseMetadata = databaseMetadataService.inspect(new DatabaseMetadataRequest(
                    request.schemaName(),
                    request.objectName(),
                    request.includeProcedures()
            ));
        }

        String answer = buildAnswer(request, logAnalysis, databaseMetadata);
        return new ChatResponse(answer, logAnalysis, databaseMetadata);
    }

    private String buildAnswer(ChatRequest request,
                               LogAnalysisResult logAnalysis,
                               DatabaseMetadataResponse databaseMetadata) {
        StringBuilder answer = new StringBuilder();
        answer.append("Question received: ").append(request.userMessage()).append(System.lineSeparator());

        if (logAnalysis != null) {
            answer.append("Log summary: ").append(logAnalysis.summary()).append(System.lineSeparator());
            answer.append("Probable cause: ").append(logAnalysis.probableCause()).append(System.lineSeparator());
        }

        if (databaseMetadata != null) {
            answer.append("DB mode: ").append(databaseMetadata.mode()).append(System.lineSeparator());
            answer.append("Tables found: ").append(databaseMetadata.tables().size()).append(System.lineSeparator());
            answer.append("Columns found: ").append(databaseMetadata.columns().size()).append(System.lineSeparator());
            if (!databaseMetadata.procedures().isEmpty()) {
                answer.append("Procedures found: ").append(databaseMetadata.procedures().size()).append(System.lineSeparator());
            }
            if (!databaseMetadata.notes().isEmpty()) {
                answer.append("Notes: ").append(String.join(" | ", databaseMetadata.notes())).append(System.lineSeparator());
            }
        }

        if (logAnalysis == null && databaseMetadata == null) {
            answer.append("Share logs or a schema/object name to start guided incident analysis.");
        } else {
            answer.append("Next step: validate the suspected component and compare against the exact failing transaction.");
        }

        return answer.toString().trim();
    }
}
