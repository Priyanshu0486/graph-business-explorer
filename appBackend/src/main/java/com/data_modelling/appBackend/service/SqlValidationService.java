package com.data_modelling.appBackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class SqlValidationService {

    private static final Pattern DANGEROUS_KEYWORDS = Pattern.compile(
            "\\b(INSERT|UPDATE|DELETE|DROP|ALTER|TRUNCATE|CREATE|GRANT|REVOKE|EXEC|EXECUTE|MERGE|CALL)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern MULTI_STATEMENT = Pattern.compile(";");

    /**
     * Validates that the SQL is a safe, read-only SELECT query.
     * Returns the validated (and possibly modified) SQL.
     * Throws SecurityException if the SQL is deemed unsafe.
     */
    public String validate(String sql) {
        if (sql == null || sql.isBlank()) {
            throw new SecurityException("Empty SQL query");
        }

        String trimmed = sql.trim();

        // Remove trailing semicolons
        while (trimmed.endsWith(";")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1).trim();
        }

        // Check multi-statement (semicolons within the body)
        if (MULTI_STATEMENT.matcher(trimmed).find()) {
            throw new SecurityException("Multi-statement SQL is not allowed");
        }

        // Must start with SELECT
        if (!trimmed.toUpperCase().startsWith("SELECT")) {
            throw new SecurityException("Only SELECT queries are allowed. Got: " + trimmed.substring(0, Math.min(20, trimmed.length())));
        }

        // Check for dangerous keywords
        if (DANGEROUS_KEYWORDS.matcher(trimmed).find()) {
            throw new SecurityException("SQL contains prohibited keywords (DML/DDL operations are not allowed)");
        }

        // Add LIMIT if not present
        if (!trimmed.toUpperCase().contains("LIMIT")) {
            trimmed = trimmed + " LIMIT 100";
        }

        log.info("Validated SQL: {}", trimmed);
        return trimmed;
    }
}
