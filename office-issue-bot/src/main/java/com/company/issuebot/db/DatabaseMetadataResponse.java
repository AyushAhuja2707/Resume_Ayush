package com.company.issuebot.db;

import java.util.List;

public record DatabaseMetadataResponse(
        String mode,
        List<String> tables,
        List<String> columns,
        List<String> procedures,
        List<String> notes
) {
}
