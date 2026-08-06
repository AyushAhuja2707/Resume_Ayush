package com.company.issuebot.db;

import com.company.issuebot.config.DbAccessProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class DatabaseMetadataService {

    private final JdbcTemplate jdbcTemplate;
    private final DbAccessProperties properties;

    public DatabaseMetadataService(JdbcTemplate jdbcTemplate, DbAccessProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
    }

    public DatabaseMetadataResponse inspect(DatabaseMetadataRequest request) {
        validateSchemaAccess(request.schemaName());

        List<String> notes = new ArrayList<>();
        List<String> tables = fetchTables(request.schemaName(), request.objectName());
        List<String> columns = fetchColumns(request.schemaName(), request.objectName());

        List<String> procedures = List.of();
        if (request.includeProcedures() && properties.isAllowProcedures()) {
            procedures = fetchProcedures(request.schemaName(), request.objectName());
        } else if (request.includeProcedures()) {
            notes.add("Procedure lookup requested but disabled by configuration.");
        }

        if (tables.isEmpty() && columns.isEmpty() && procedures.isEmpty()) {
            notes.add("No metadata matched the requested schema/object filter.");
        }

        return new DatabaseMetadataResponse(
                properties.getMode(),
                tables,
                columns,
                procedures,
                notes
        );
    }

    private void validateSchemaAccess(String schemaName) {
        if (schemaName == null || schemaName.isBlank()) {
            return;
        }
        if (!properties.getAllowedSchemas().isEmpty()
                && properties.getAllowedSchemas().stream().noneMatch(schema -> schema.equalsIgnoreCase(schemaName))) {
            throw new IllegalArgumentException("Schema is not allowed for chatbot access: " + schemaName);
        }
    }

    private List<String> fetchTables(String schemaName, String objectName) {
        String sql = """
                select table_schema || '.' || table_name
                from information_schema.tables
                where (? is null or lower(table_schema) = lower(?))
                  and (? is null or lower(table_name) like lower(?))
                order by table_schema, table_name
                """;
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString(1),
                schemaName,
                schemaName,
                likeToken(objectName),
                likeTokenWithWildcards(objectName)
        ).stream().filter(this::isAllowedObject).toList();
    }

    private List<String> fetchColumns(String schemaName, String objectName) {
        String sql = """
                select table_schema || '.' || table_name || '.' || column_name
                from information_schema.columns
                where (? is null or lower(table_schema) = lower(?))
                  and (? is null or lower(table_name) like lower(?))
                order by table_schema, table_name, ordinal_position
                """;
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString(1),
                schemaName,
                schemaName,
                likeToken(objectName),
                likeTokenWithWildcards(objectName)
        ).stream().filter(this::isAllowedObject).toList();
    }

    private List<String> fetchProcedures(String schemaName, String objectName) {
        String sql = """
                select routine_schema || '.' || routine_name
                from information_schema.routines
                where (? is null or lower(routine_schema) = lower(?))
                  and (? is null or lower(routine_name) like lower(?))
                order by routine_schema, routine_name
                """;
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString(1),
                schemaName,
                schemaName,
                likeToken(objectName),
                likeTokenWithWildcards(objectName)
        ).stream().filter(this::isAllowedObject).toList();
    }

    private String likeToken(String objectName) {
        return objectName == null || objectName.isBlank() ? null : objectName;
    }

    private String likeTokenWithWildcards(String objectName) {
        return objectName == null || objectName.isBlank() ? null : "%" + objectName + "%";
    }

    private boolean isAllowedObject(String dbObject) {
        String normalized = Objects.requireNonNullElse(dbObject, "").toLowerCase(Locale.ROOT);
        return properties.getBlockedObjects().stream()
                .map(item -> item.toLowerCase(Locale.ROOT))
                .noneMatch(normalized::contains);
    }
}
