package com.data_modelling.appBackend.controller;

import com.data_modelling.appBackend.dto.ChatRequest;
import com.data_modelling.appBackend.dto.ChatResponse;
import com.data_modelling.appBackend.service.LlmService;
import com.data_modelling.appBackend.service.QueryExecutionService;
import com.data_modelling.appBackend.service.SqlValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final LlmService llmService;
    private final SqlValidationService sqlValidationService;
    private final QueryExecutionService queryExecutionService;

    @PostMapping("/query")
    public ResponseEntity<ChatResponse> query(@RequestBody ChatRequest request) {
        String question = request.getQuestion();
        log.info("Received question: {}", question);

        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ChatResponse.builder().error("Question cannot be empty").build()
            );
        }

        try {
            // Step 1: Generate SQL from natural language via Gemini
            String rawSql = llmService.generateSql(question);
            log.info("Generated SQL: {}", rawSql);

            // Step 2: Validate the SQL (security guardrails)
            String validatedSql = sqlValidationService.validate(rawSql);

            // Step 3: Execute the validated SQL
            List<Map<String, Object>> results = queryExecutionService.execute(validatedSql);

            // Step 4: Build response
            String answer = buildAnswer(results, question);
            return ResponseEntity.ok(ChatResponse.builder()
                    .question(question)
                    .sql(validatedSql)
                    .answer(answer)
                    .results(results)
                    .build());

        } catch (SecurityException e) {
            log.warn("SQL validation failed: {}", e.getMessage());
            return ResponseEntity.ok(ChatResponse.builder()
                    .question(question)
                    .error("Security violation: " + e.getMessage())
                    .answer("I cannot execute that query because it violates security rules. Only read-only SELECT queries are allowed.")
                    .build());
        } catch (RuntimeException e) {
            log.error("Query processing failed: {}", e.getMessage());
            String msg = e.getMessage();
            if (msg != null && msg.startsWith("NOT_RELEVANT")) {
                return ResponseEntity.ok(ChatResponse.builder()
                        .question(question)
                        .answer("This question doesn't seem related to the business data. Please ask about sales orders, business partners, products, deliveries, billing documents, or payments.")
                        .build());
            }
            return ResponseEntity.ok(ChatResponse.builder()
                    .question(question)
                    .error(e.getMessage())
                    .answer("Sorry, I encountered an error processing your question. Please try rephrasing it.")
                    .build());
        }
    }

    private String buildAnswer(List<Map<String, Object>> results, String question) {
        if (results.isEmpty()) {
            return "No results found for your query.";
        }
        if (results.size() == 1 && results.get(0).size() == 1) {
            // Single aggregate result
            Object val = results.get(0).values().iterator().next();
            return "The result is: " + val;
        }
        return "Found " + results.size() + " result(s). See the table below for details.";
    }
}
