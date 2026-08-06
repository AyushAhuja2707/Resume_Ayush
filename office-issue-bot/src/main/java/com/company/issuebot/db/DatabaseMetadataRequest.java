package com.company.issuebot.db;

public record DatabaseMetadataRequest(
        String schemaName,
        String objectName,
        boolean includeProcedures
) {
}
