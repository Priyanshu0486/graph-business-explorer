package com.data_modelling.appBackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.*;

@Service
@Slf4j
public class LlmService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private static final String SCHEMA_CONTEXT = """
            You are a SQL expert. You have access to a PostgreSQL database with the following schema:
            
            TABLE: business_partner
            Columns: business_partner (PK, VARCHAR), customer (VARCHAR), business_partner_full_name (VARCHAR),
            business_partner_name (VARCHAR), business_partner_category (VARCHAR), business_partner_grouping (VARCHAR),
            industry (VARCHAR), language (VARCHAR), organization_bp_name1 (VARCHAR), search_term1 (VARCHAR),
            business_partner_type (VARCHAR)
            
            TABLE: product
            Columns: product (PK, VARCHAR), product_type (VARCHAR), cross_plant_status (VARCHAR),
            cross_plant_status_validity_date (DATE), creation_date (DATE), created_by_user (VARCHAR),
            last_change_date (DATE), product_old_id (VARCHAR), gross_weight (DOUBLE), weight_unit (VARCHAR),
            net_weight (DOUBLE), product_group (VARCHAR), base_unit (VARCHAR), division (VARCHAR),
            industry_sector (VARCHAR)
            
            TABLE: sales_order_header
            Columns: sales_order (PK, VARCHAR), sales_order_type (VARCHAR), sales_organization (VARCHAR),
            distribution_channel (VARCHAR), organization_division (VARCHAR), sold_to_party (VARCHAR, FK -> business_partner),
            creation_date (DATE), total_net_amount (DECIMAL), transaction_currency (VARCHAR),
            overall_delivery_status (VARCHAR), overall_sdprocess_status (VARCHAR)
            
            TABLE: sales_order_item
            Columns: sales_order (PK, VARCHAR, FK -> sales_order_header), sales_order_item (PK, VARCHAR),
            material (VARCHAR, FK -> product), sales_order_item_text (VARCHAR), requested_quantity (DECIMAL),
            requested_quantity_unit (VARCHAR), net_amount (DECIMAL), transaction_currency (VARCHAR),
            material_group (VARCHAR), plant (VARCHAR), storage_location (VARCHAR), delivery_status (VARCHAR)
            
            TABLE: billing_document_header
            Columns: billing_document (PK, VARCHAR), billing_document_type (VARCHAR), creation_date (DATE),
            billing_document_date (DATE), billing_document_is_cancelled (BOOLEAN),
            cancelled_billing_document (VARCHAR), total_net_amount (DECIMAL), transaction_currency (VARCHAR),
            company_code (VARCHAR), fiscal_year (VARCHAR), accounting_document (VARCHAR),
            sold_to_party (VARCHAR, FK -> business_partner)
            
            TABLE: billing_document_item
            Columns: billing_document (PK, VARCHAR, FK -> billing_document_header), billing_document_item (PK, VARCHAR),
            material (VARCHAR, FK -> product), billing_quantity (DECIMAL), billing_quantity_unit (VARCHAR),
            net_amount (DECIMAL), transaction_currency (VARCHAR), reference_sd_document (VARCHAR),
            reference_sd_document_item (VARCHAR)
            
            TABLE: outbound_delivery_header
            Columns: delivery_document (PK, VARCHAR), actual_goods_movement_date (DATE), creation_date (DATE),
            delivery_block_reason (VARCHAR), hdr_general_incompletion_status (VARCHAR),
            header_billing_block_reason (VARCHAR), last_change_date (DATE),
            overall_goods_movement_status (VARCHAR), overall_picking_status (VARCHAR),
            overall_proof_of_delivery_status (VARCHAR), shipping_point (VARCHAR)
            
            TABLE: outbound_delivery_item
            Columns: delivery_document (PK, VARCHAR, FK -> outbound_delivery_header), delivery_document_item (PK, VARCHAR),
            actual_delivery_quantity (DECIMAL), delivery_quantity_unit (VARCHAR), batch (VARCHAR),
            item_billing_block_reason (VARCHAR), last_change_date (DATE), plant (VARCHAR),
            reference_sd_document (VARCHAR), reference_sd_document_item (VARCHAR), storage_location (VARCHAR)
            
            TABLE: payment_accounts_receivable
            Columns: accounting_document (PK, VARCHAR), accounting_document_item (PK, VARCHAR),
            company_code (VARCHAR), fiscal_year (VARCHAR), clearing_date (DATE),
            clearing_accounting_document (VARCHAR), clearing_doc_fiscal_year (VARCHAR),
            amount_in_transaction_currency (DECIMAL), transaction_currency (VARCHAR),
            amount_in_company_code_currency (DECIMAL), company_code_currency (VARCHAR),
            customer (VARCHAR, FK -> business_partner), posting_date (DATE), document_date (DATE),
            gl_account (VARCHAR), financial_account_type (VARCHAR), profit_center (VARCHAR)
            
            RELATIONSHIPS:
            - sales_order_header.sold_to_party -> business_partner.business_partner
            - sales_order_item.sales_order -> sales_order_header.sales_order
            - sales_order_item.material -> product.product
            - billing_document_header.sold_to_party -> business_partner.business_partner
            - billing_document_item.billing_document -> billing_document_header.billing_document
            - billing_document_item.material -> product.product
            - outbound_delivery_item.delivery_document -> outbound_delivery_header.delivery_document
            - payment_accounts_receivable.customer -> business_partner.customer
            
            RULES:
            1. Generate ONLY a single SELECT query. Never generate INSERT, UPDATE, DELETE, or DDL statements.
            2. Use PostgreSQL syntax.
            3. Always add appropriate LIMIT clauses (max 100 rows).
            4. If the question is not related to the database, respond with: NOT_RELEVANT
            5. Return ONLY the SQL query, nothing else. No explanations, no markdown.
            """;

    /**
     * Sends a natural language question to Gemini and extracts the generated SQL.
     */
    public String generateSql(String question) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("Gemini API key is not configured. Set GEMINI_API_KEY environment variable.");
        }

        String url = GEMINI_URL + "?key=" + apiKey;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", SCHEMA_CONTEXT + "\n\nUser Question: " + question)
                        ))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.1,
                        "maxOutputTokens", 1024
                )
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String text = root.path("candidates").path(0).path("content")
                        .path("parts").path(0).path("text").asText();

                return extractSql(text);
            } else {
                throw new RuntimeException("Gemini API returned: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new RuntimeException("Failed to generate SQL: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts SQL from the Gemini response text, handling markdown code blocks.
     */
    private String extractSql(String text) {
        if (text == null || text.isBlank()) {
            throw new RuntimeException("Empty response from Gemini");
        }

        text = text.trim();

        // Check if the response indicates non-relevant question
        if (text.contains("NOT_RELEVANT")) {
            throw new RuntimeException("NOT_RELEVANT: This question is not related to the business data.");
        }

        // Extract from ```sql ... ``` blocks
        Pattern pattern = Pattern.compile("```(?:sql)?\\s*\\n?(.*?)\\n?```", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        // If no code block, return the raw text (assuming it's just SQL)
        return text.trim();
    }
}
