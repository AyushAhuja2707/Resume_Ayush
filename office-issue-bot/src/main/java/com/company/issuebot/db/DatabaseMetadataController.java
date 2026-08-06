package com.company.issuebot.db;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/db")
public class DatabaseMetadataController {

    private final DatabaseMetadataService databaseMetadataService;

    public DatabaseMetadataController(DatabaseMetadataService databaseMetadataService) {
        this.databaseMetadataService = databaseMetadataService;
    }

    @GetMapping("/metadata")
    public ResponseEntity<DatabaseMetadataResponse> metadata(
            @RequestParam(required = false) String schemaName,
            @RequestParam(required = false) String objectName,
            @RequestParam(defaultValue = "false") boolean includeProcedures
    ) {
        return ResponseEntity.ok(databaseMetadataService.inspect(
                new DatabaseMetadataRequest(schemaName, objectName, includeProcedures)
        ));
    }
}
