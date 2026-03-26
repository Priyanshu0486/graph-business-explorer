package com.data_modelling.appBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "billing_document_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(BillingDocumentItemId.class)
public class BillingDocumentItem {

    @Id
    @Column(name = "billing_document", length = 20)
    private String billingDocument;

    @Id
    @Column(name = "billing_document_item", length = 10)
    private String billingDocumentItem;

    @Column(length = 40)
    private String material;

    private BigDecimal billingQuantity;

    @Column(length = 5)
    private String billingQuantityUnit;

    private BigDecimal netAmount;

    @Column(length = 5)
    private String transactionCurrency;

    @Column(length = 20)
    private String referenceSdDocument;

    @Column(length = 10)
    private String referenceSdDocumentItem;
}
