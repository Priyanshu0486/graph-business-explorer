package com.data_modelling.appBackend.controller;

import com.data_modelling.appBackend.dto.GraphResponse;
import com.data_modelling.appBackend.dto.GraphResponse.GraphNode;
import com.data_modelling.appBackend.dto.GraphResponse.GraphEdge;
import com.data_modelling.appBackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/graph")
@RequiredArgsConstructor
public class GraphController {

    private final BusinessPartnerRepository businessPartnerRepo;
    private final ProductRepository productRepo;
    private final SalesOrderHeaderRepository salesOrderHeaderRepo;
    private final SalesOrderItemRepository salesOrderItemRepo;
    private final BillingDocumentHeaderRepository billingDocHeaderRepo;
    private final BillingDocumentItemRepository billingDocItemRepo;
    private final OutboundDeliveryHeaderRepository deliveryHeaderRepo;
    private final OutboundDeliveryItemRepository deliveryItemRepo;
    private final PaymentAccountsReceivableRepository paymentRepo;

    @GetMapping("/overview")
    public GraphResponse getOverview() {
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphEdge> edges = new ArrayList<>();

        // Entity type nodes with record counts
        nodes.add(GraphNode.builder().id("bp").label("Business Partners").type("business_partner")
                .count(businessPartnerRepo.count()).data(Map.of("color", "#6366f1")).build());
        nodes.add(GraphNode.builder().id("prod").label("Products").type("product")
                .count(productRepo.count()).data(Map.of("color", "#8b5cf6")).build());
        nodes.add(GraphNode.builder().id("soh").label("Sales Orders").type("sales_order_header")
                .count(salesOrderHeaderRepo.count()).data(Map.of("color", "#3b82f6")).build());
        nodes.add(GraphNode.builder().id("soi").label("Sales Order Items").type("sales_order_item")
                .count(salesOrderItemRepo.count()).data(Map.of("color", "#06b6d4")).build());
        nodes.add(GraphNode.builder().id("bdh").label("Billing Documents").type("billing_document_header")
                .count(billingDocHeaderRepo.count()).data(Map.of("color", "#f59e0b")).build());
        nodes.add(GraphNode.builder().id("bdi").label("Billing Doc Items").type("billing_document_item")
                .count(billingDocItemRepo.count()).data(Map.of("color", "#f97316")).build());
        nodes.add(GraphNode.builder().id("odh").label("Deliveries").type("outbound_delivery_header")
                .count(deliveryHeaderRepo.count()).data(Map.of("color", "#22c55e")).build());
        nodes.add(GraphNode.builder().id("odi").label("Delivery Items").type("outbound_delivery_item")
                .count(deliveryItemRepo.count()).data(Map.of("color", "#10b981")).build());
        nodes.add(GraphNode.builder().id("pay").label("Payments").type("payment_accounts_receivable")
                .count(paymentRepo.count()).data(Map.of("color", "#ef4444")).build());

        // Relationship edges (O2C flow)
        edges.add(GraphEdge.builder().id("e1").source("bp").target("soh").label("places").relationship("soldToParty").build());
        edges.add(GraphEdge.builder().id("e2").source("soh").target("soi").label("has items").relationship("salesOrder").build());
        edges.add(GraphEdge.builder().id("e3").source("soi").target("prod").label("contains").relationship("material").build());
        edges.add(GraphEdge.builder().id("e4").source("soh").target("odh").label("delivered by").relationship("referenceSdDocument").build());
        edges.add(GraphEdge.builder().id("e5").source("odh").target("odi").label("has items").relationship("deliveryDocument").build());
        edges.add(GraphEdge.builder().id("e6").source("soh").target("bdh").label("billed by").relationship("referenceSdDocument").build());
        edges.add(GraphEdge.builder().id("e7").source("bdh").target("bdi").label("has items").relationship("billingDocument").build());
        edges.add(GraphEdge.builder().id("e8").source("bp").target("bdh").label("billed to").relationship("soldToParty").build());
        edges.add(GraphEdge.builder().id("e9").source("bp").target("pay").label("pays via").relationship("customer").build());
        edges.add(GraphEdge.builder().id("e10").source("bdh").target("pay").label("paid by").relationship("accountingDocument").build());

        return GraphResponse.builder().nodes(nodes).edges(edges).build();
    }
}
