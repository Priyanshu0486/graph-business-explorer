package com.data_modelling.appBackend.controller;

import com.data_modelling.appBackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/entities")
@RequiredArgsConstructor
public class EntityController {

    private final BusinessPartnerRepository businessPartnerRepo;
    private final ProductRepository productRepo;
    private final SalesOrderHeaderRepository salesOrderHeaderRepo;
    private final SalesOrderItemRepository salesOrderItemRepo;
    private final BillingDocumentHeaderRepository billingDocHeaderRepo;
    private final BillingDocumentItemRepository billingDocItemRepo;
    private final OutboundDeliveryHeaderRepository deliveryHeaderRepo;
    private final OutboundDeliveryItemRepository deliveryItemRepo;
    private final PaymentAccountsReceivableRepository paymentRepo;

    @GetMapping("/{type}")
    public ResponseEntity<?> listEntities(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageRequest pageReq = PageRequest.of(page, Math.min(size, 100));

        return switch (type) {
            case "business-partners" -> ResponseEntity.ok(businessPartnerRepo.findAll(pageReq));
            case "products" -> ResponseEntity.ok(productRepo.findAll(pageReq));
            case "sales-orders" -> ResponseEntity.ok(salesOrderHeaderRepo.findAll(pageReq));
            case "sales-order-items" -> ResponseEntity.ok(salesOrderItemRepo.findAll(pageReq));
            case "billing-documents" -> ResponseEntity.ok(billingDocHeaderRepo.findAll(pageReq));
            case "billing-document-items" -> ResponseEntity.ok(billingDocItemRepo.findAll(pageReq));
            case "deliveries" -> ResponseEntity.ok(deliveryHeaderRepo.findAll(pageReq));
            case "delivery-items" -> ResponseEntity.ok(deliveryItemRepo.findAll(pageReq));
            case "payments" -> ResponseEntity.ok(paymentRepo.findAll(pageReq));
            default -> ResponseEntity.badRequest().body(Map.of("error", "Unknown entity type: " + type));
        };
    }

    @GetMapping("/{type}/{id}")
    public ResponseEntity<?> getEntity(@PathVariable String type, @PathVariable String id) {
        return switch (type) {
            case "business-partners" -> businessPartnerRepo.findById(id)
                    .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
            case "products" -> productRepo.findById(id)
                    .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
            case "sales-orders" -> salesOrderHeaderRepo.findById(id)
                    .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
            case "billing-documents" -> billingDocHeaderRepo.findById(id)
                    .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
            case "deliveries" -> deliveryHeaderRepo.findById(id)
                    .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
            default -> ResponseEntity.badRequest().body(Map.of("error", "Unknown entity type or composite key: " + type));
        };
    }
}
