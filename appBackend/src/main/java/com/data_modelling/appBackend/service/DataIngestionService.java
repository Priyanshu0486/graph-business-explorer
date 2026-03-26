package com.data_modelling.appBackend.service;

import com.data_modelling.appBackend.entity.*;
import com.data_modelling.appBackend.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataIngestionService {

    private final BusinessPartnerRepository businessPartnerRepo;
    private final ProductRepository productRepo;
    private final SalesOrderHeaderRepository salesOrderHeaderRepo;
    private final SalesOrderItemRepository salesOrderItemRepo;
    private final BillingDocumentHeaderRepository billingDocumentHeaderRepo;
    private final BillingDocumentItemRepository billingDocumentItemRepo;
    private final OutboundDeliveryHeaderRepository outboundDeliveryHeaderRepo;
    private final OutboundDeliveryItemRepository outboundDeliveryItemRepo;
    private final PaymentAccountsReceivableRepository paymentRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.data.path}")
    private String dataPath;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @PostConstruct
    public void ingestData() {
        log.info("Starting data ingestion from: {}", dataPath);
        try {
            Path basePath = Paths.get(dataPath).toAbsolutePath();
            if (!Files.exists(basePath)) {
                log.error("Data path does not exist: {}", basePath);
                return;
            }

            ingestBusinessPartners(basePath.resolve("business_partners"));
            ingestProducts(basePath.resolve("products"));
            ingestSalesOrderHeaders(basePath.resolve("sales_order_headers"));
            ingestSalesOrderItems(basePath.resolve("sales_order_items"));
            ingestBillingDocumentHeaders(basePath.resolve("billing_document_headers"));
            ingestBillingDocumentItems(basePath.resolve("billing_document_items"));
            ingestOutboundDeliveryHeaders(basePath.resolve("outbound_delivery_headers"));
            ingestOutboundDeliveryItems(basePath.resolve("outbound_delivery_items"));
            ingestPayments(basePath.resolve("payments_accounts_receivable"));

            log.info("Data ingestion complete!");
            logCounts();
        } catch (Exception e) {
            log.error("Data ingestion failed", e);
        }
    }

    private void logCounts() {
        log.info("  BusinessPartners: {}", businessPartnerRepo.count());
        log.info("  Products: {}", productRepo.count());
        log.info("  SalesOrderHeaders: {}", salesOrderHeaderRepo.count());
        log.info("  SalesOrderItems: {}", salesOrderItemRepo.count());
        log.info("  BillingDocumentHeaders: {}", billingDocumentHeaderRepo.count());
        log.info("  BillingDocumentItems: {}", billingDocumentItemRepo.count());
        log.info("  OutboundDeliveryHeaders: {}", outboundDeliveryHeaderRepo.count());
        log.info("  OutboundDeliveryItems: {}", outboundDeliveryItemRepo.count());
        log.info("  Payments: {}", paymentRepo.count());
    }

    // ---- Individual ingestion methods ----

    @Transactional
    public void ingestBusinessPartners(Path dir) {
        if (!Files.exists(dir)) { log.warn("Directory not found: {}", dir); return; }
        log.info("Ingesting business_partners...");
        List<BusinessPartner> batch = new ArrayList<>();
        processJsonlFiles(dir, node -> {
            BusinessPartner bp = BusinessPartner.builder()
                    .businessPartner(text(node, "businessPartner"))
                    .customer(text(node, "customer"))
                    .businessPartnerFullName(text(node, "businessPartnerFullName"))
                    .businessPartnerName(text(node, "businessPartnerName"))
                    .businessPartnerCategory(text(node, "businessPartnerCategory"))
                    .businessPartnerGrouping(text(node, "businessPartnerGrouping"))
                    .industry(text(node, "industry"))
                    .language(text(node, "language"))
                    .organizationBpName1(text(node, "organizationBPName1"))
                    .searchTerm1(text(node, "searchTerm1"))
                    .businessPartnerType(text(node, "businessPartnerType"))
                    .build();
            batch.add(bp);
            if (batch.size() >= 500) { businessPartnerRepo.saveAll(batch); batch.clear(); }
        });
        if (!batch.isEmpty()) businessPartnerRepo.saveAll(batch);
    }

    @Transactional
    public void ingestProducts(Path dir) {
        if (!Files.exists(dir)) { log.warn("Directory not found: {}", dir); return; }
        log.info("Ingesting products...");
        List<Product> batch = new ArrayList<>();
        processJsonlFiles(dir, node -> {
            Product p = Product.builder()
                    .product(text(node, "product"))
                    .productType(text(node, "productType"))
                    .crossPlantStatus(text(node, "crossPlantStatus"))
                    .crossPlantStatusValidityDate(date(node, "crossPlantStatusValidityDate"))
                    .creationDate(date(node, "creationDate"))
                    .createdByUser(text(node, "createdByUser"))
                    .lastChangeDate(date(node, "lastChangeDate"))
                    .productOldId(text(node, "productOldID"))
                    .grossWeight(dbl(node, "grossWeight"))
                    .weightUnit(text(node, "weightUnit"))
                    .netWeight(dbl(node, "netWeight"))
                    .productGroup(text(node, "productGroup"))
                    .baseUnit(text(node, "baseUnit"))
                    .division(text(node, "division"))
                    .industrySector(text(node, "industrySector"))
                    .build();
            batch.add(p);
            if (batch.size() >= 500) { productRepo.saveAll(batch); batch.clear(); }
        });
        if (!batch.isEmpty()) productRepo.saveAll(batch);
    }

    @Transactional
    public void ingestSalesOrderHeaders(Path dir) {
        if (!Files.exists(dir)) { log.warn("Directory not found: {}", dir); return; }
        log.info("Ingesting sales_order_headers...");
        List<SalesOrderHeader> batch = new ArrayList<>();
        processJsonlFiles(dir, node -> {
            SalesOrderHeader h = SalesOrderHeader.builder()
                    .salesOrder(text(node, "salesOrder"))
                    .salesOrderType(text(node, "salesOrderType"))
                    .salesOrganization(text(node, "salesOrganization"))
                    .distributionChannel(text(node, "distributionChannel"))
                    .organizationDivision(text(node, "organizationDivision"))
                    .soldToParty(text(node, "soldToParty"))
                    .creationDate(date(node, "creationDate"))
                    .totalNetAmount(decimal(node, "totalNetAmount"))
                    .transactionCurrency(text(node, "transactionCurrency"))
                    .overallDeliveryStatus(text(node, "overallDeliveryStatus"))
                    .overallSDProcessStatus(text(node, "overallSDProcessStatus"))
                    .build();
            batch.add(h);
            if (batch.size() >= 500) { salesOrderHeaderRepo.saveAll(batch); batch.clear(); }
        });
        if (!batch.isEmpty()) salesOrderHeaderRepo.saveAll(batch);
    }

    @Transactional
    public void ingestSalesOrderItems(Path dir) {
        if (!Files.exists(dir)) { log.warn("Directory not found: {}", dir); return; }
        log.info("Ingesting sales_order_items...");
        List<SalesOrderItem> batch = new ArrayList<>();
        processJsonlFiles(dir, node -> {
            SalesOrderItem item = SalesOrderItem.builder()
                    .salesOrder(text(node, "salesOrder"))
                    .salesOrderItem(text(node, "salesOrderItem"))
                    .material(text(node, "material"))
                    .salesOrderItemText(text(node, "salesOrderItemText"))
                    .requestedQuantity(decimal(node, "requestedQuantity"))
                    .requestedQuantityUnit(text(node, "requestedQuantityUnit"))
                    .netAmount(decimal(node, "netAmount"))
                    .transactionCurrency(text(node, "transactionCurrency"))
                    .materialGroup(text(node, "materialGroup"))
                    .plant(text(node, "plant"))
                    .storageLocation(text(node, "storageLocation"))
                    .deliveryStatus(text(node, "deliveryStatus"))
                    .build();
            batch.add(item);
            if (batch.size() >= 500) { salesOrderItemRepo.saveAll(batch); batch.clear(); }
        });
        if (!batch.isEmpty()) salesOrderItemRepo.saveAll(batch);
    }

    @Transactional
    public void ingestBillingDocumentHeaders(Path dir) {
        if (!Files.exists(dir)) { log.warn("Directory not found: {}", dir); return; }
        log.info("Ingesting billing_document_headers...");
        List<BillingDocumentHeader> batch = new ArrayList<>();
        processJsonlFiles(dir, node -> {
            BillingDocumentHeader h = BillingDocumentHeader.builder()
                    .billingDocument(text(node, "billingDocument"))
                    .billingDocumentType(text(node, "billingDocumentType"))
                    .creationDate(date(node, "creationDate"))
                    .billingDocumentDate(date(node, "billingDocumentDate"))
                    .billingDocumentIsCancelled(bool(node, "billingDocumentIsCancelled"))
                    .cancelledBillingDocument(text(node, "cancelledBillingDocument"))
                    .totalNetAmount(decimal(node, "totalNetAmount"))
                    .transactionCurrency(text(node, "transactionCurrency"))
                    .companyCode(text(node, "companyCode"))
                    .fiscalYear(text(node, "fiscalYear"))
                    .accountingDocument(text(node, "accountingDocument"))
                    .soldToParty(text(node, "soldToParty"))
                    .build();
            batch.add(h);
            if (batch.size() >= 500) { billingDocumentHeaderRepo.saveAll(batch); batch.clear(); }
        });
        if (!batch.isEmpty()) billingDocumentHeaderRepo.saveAll(batch);
    }

    @Transactional
    public void ingestBillingDocumentItems(Path dir) {
        if (!Files.exists(dir)) { log.warn("Directory not found: {}", dir); return; }
        log.info("Ingesting billing_document_items...");
        List<BillingDocumentItem> batch = new ArrayList<>();
        processJsonlFiles(dir, node -> {
            BillingDocumentItem item = BillingDocumentItem.builder()
                    .billingDocument(text(node, "billingDocument"))
                    .billingDocumentItem(text(node, "billingDocumentItem"))
                    .material(text(node, "material"))
                    .billingQuantity(decimal(node, "billingQuantity"))
                    .billingQuantityUnit(text(node, "billingQuantityUnit"))
                    .netAmount(decimal(node, "netAmount"))
                    .transactionCurrency(text(node, "transactionCurrency"))
                    .referenceSdDocument(text(node, "referenceSdDocument"))
                    .referenceSdDocumentItem(text(node, "referenceSdDocumentItem"))
                    .build();
            batch.add(item);
            if (batch.size() >= 500) { billingDocumentItemRepo.saveAll(batch); batch.clear(); }
        });
        if (!batch.isEmpty()) billingDocumentItemRepo.saveAll(batch);
    }

    @Transactional
    public void ingestOutboundDeliveryHeaders(Path dir) {
        if (!Files.exists(dir)) { log.warn("Directory not found: {}", dir); return; }
        log.info("Ingesting outbound_delivery_headers...");
        List<OutboundDeliveryHeader> batch = new ArrayList<>();
        processJsonlFiles(dir, node -> {
            OutboundDeliveryHeader h = OutboundDeliveryHeader.builder()
                    .deliveryDocument(text(node, "deliveryDocument"))
                    .actualGoodsMovementDate(date(node, "actualGoodsMovementDate"))
                    .creationDate(date(node, "creationDate"))
                    .deliveryBlockReason(text(node, "deliveryBlockReason"))
                    .hdrGeneralIncompletionStatus(text(node, "hdrGeneralIncompletionStatus"))
                    .headerBillingBlockReason(text(node, "headerBillingBlockReason"))
                    .lastChangeDate(date(node, "lastChangeDate"))
                    .overallGoodsMovementStatus(text(node, "overallGoodsMovementStatus"))
                    .overallPickingStatus(text(node, "overallPickingStatus"))
                    .overallProofOfDeliveryStatus(text(node, "overallProofOfDeliveryStatus"))
                    .shippingPoint(text(node, "shippingPoint"))
                    .build();
            batch.add(h);
            if (batch.size() >= 500) { outboundDeliveryHeaderRepo.saveAll(batch); batch.clear(); }
        });
        if (!batch.isEmpty()) outboundDeliveryHeaderRepo.saveAll(batch);
    }

    @Transactional
    public void ingestOutboundDeliveryItems(Path dir) {
        if (!Files.exists(dir)) { log.warn("Directory not found: {}", dir); return; }
        log.info("Ingesting outbound_delivery_items...");
        List<OutboundDeliveryItem> batch = new ArrayList<>();
        processJsonlFiles(dir, node -> {
            OutboundDeliveryItem item = OutboundDeliveryItem.builder()
                    .deliveryDocument(text(node, "deliveryDocument"))
                    .deliveryDocumentItem(text(node, "deliveryDocumentItem"))
                    .actualDeliveryQuantity(decimal(node, "actualDeliveryQuantity"))
                    .deliveryQuantityUnit(text(node, "deliveryQuantityUnit"))
                    .batch(text(node, "batch"))
                    .itemBillingBlockReason(text(node, "itemBillingBlockReason"))
                    .lastChangeDate(date(node, "lastChangeDate"))
                    .plant(text(node, "plant"))
                    .referenceSdDocument(text(node, "referenceSdDocument"))
                    .referenceSdDocumentItem(text(node, "referenceSdDocumentItem"))
                    .storageLocation(text(node, "storageLocation"))
                    .build();
            batch.add(item);
            if (batch.size() >= 500) { outboundDeliveryItemRepo.saveAll(batch); batch.clear(); }
        });
        if (!batch.isEmpty()) outboundDeliveryItemRepo.saveAll(batch);
    }

    @Transactional
    public void ingestPayments(Path dir) {
        if (!Files.exists(dir)) { log.warn("Directory not found: {}", dir); return; }
        log.info("Ingesting payments_accounts_receivable...");
        List<PaymentAccountsReceivable> batch = new ArrayList<>();
        processJsonlFiles(dir, node -> {
            PaymentAccountsReceivable pay = PaymentAccountsReceivable.builder()
                    .accountingDocument(text(node, "accountingDocument"))
                    .accountingDocumentItem(text(node, "accountingDocumentItem"))
                    .companyCode(text(node, "companyCode"))
                    .fiscalYear(text(node, "fiscalYear"))
                    .clearingDate(date(node, "clearingDate"))
                    .clearingAccountingDocument(text(node, "clearingAccountingDocument"))
                    .clearingDocFiscalYear(text(node, "clearingDocFiscalYear"))
                    .amountInTransactionCurrency(decimal(node, "amountInTransactionCurrency"))
                    .transactionCurrency(text(node, "transactionCurrency"))
                    .amountInCompanyCodeCurrency(decimal(node, "amountInCompanyCodeCurrency"))
                    .companyCodeCurrency(text(node, "companyCodeCurrency"))
                    .customer(text(node, "customer"))
                    .postingDate(date(node, "postingDate"))
                    .documentDate(date(node, "documentDate"))
                    .glAccount(text(node, "glAccount"))
                    .financialAccountType(text(node, "financialAccountType"))
                    .profitCenter(text(node, "profitCenter"))
                    .build();
            batch.add(pay);
            if (batch.size() >= 500) { paymentRepo.saveAll(batch); batch.clear(); }
        });
        if (!batch.isEmpty()) paymentRepo.saveAll(batch);
    }

    // ---- Utility methods ----

    private void processJsonlFiles(Path dir, java.util.function.Consumer<JsonNode> consumer) {
        try (Stream<Path> files = Files.list(dir)) {
            files.filter(f -> f.toString().endsWith(".jsonl")).sorted().forEach(file -> {
                log.info("  Processing file: {}", file.getFileName());
                try (BufferedReader reader = Files.newBufferedReader(file)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty()) continue;
                        try {
                            JsonNode node = objectMapper.readTree(line);
                            consumer.accept(node);
                        } catch (Exception e) {
                            log.warn("  Skipping malformed line: {}", e.getMessage());
                        }
                    }
                } catch (IOException e) {
                    log.error("  Error reading file: {}", file, e);
                }
            });
        } catch (IOException e) {
            log.error("Error listing directory: {}", dir, e);
        }
    }

    private String text(JsonNode node, String field) {
        JsonNode val = node.get(field);
        if (val == null || val.isNull()) return null;
        String s = val.asText().trim();
        return s.isEmpty() ? null : s;
    }

    private LocalDate date(JsonNode node, String field) {
        String s = text(node, field);
        if (s == null) return null;
        try {
            // Handle /Date(...)/ format from SAP
            if (s.startsWith("/Date(")) {
                long millis = Long.parseLong(s.replaceAll("[^0-9-]", ""));
                return java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            }
            return LocalDate.parse(s, DATE_FMT);
        } catch (DateTimeParseException | NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal decimal(JsonNode node, String field) {
        JsonNode val = node.get(field);
        if (val == null || val.isNull()) return null;
        try {
            return new BigDecimal(val.asText().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double dbl(JsonNode node, String field) {
        BigDecimal bd = decimal(node, field);
        return bd != null ? bd.doubleValue() : null;
    }

    private Boolean bool(JsonNode node, String field) {
        JsonNode val = node.get(field);
        if (val == null || val.isNull()) return null;
        if (val.isBoolean()) return val.asBoolean();
        String s = val.asText().trim().toLowerCase();
        if (s.equals("true") || s.equals("x")) return true;
        if (s.equals("false") || s.isEmpty()) return false;
        return null;
    }
}
