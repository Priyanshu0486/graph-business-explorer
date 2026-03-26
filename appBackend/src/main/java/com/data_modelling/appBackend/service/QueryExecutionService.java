package com.data_modelling.appBackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryExecutionService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Executes a validated SELECT SQL query and returns the results as a list of maps.
     */
    public List<Map<String, Object>> execute(String sql) {
        log.info("Executing SQL: {}", sql);
        return jdbcTemplate.queryForList(sql);
    }
}
