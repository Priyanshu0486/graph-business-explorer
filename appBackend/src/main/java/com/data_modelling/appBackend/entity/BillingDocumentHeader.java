package com.data_modelling.appBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "billing_document_header")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingDocumentHeader {

    @Id
    @Column(name = "billing_document", length = 20)
    private String billingDocument;

    @Column(length = 10)
    private String billingDocumentType;

    private LocalDate creationDate;

    private LocalDate billingDocumentDate;

    private Boolean billingDocumentIsCancelled;

    @Column(length = 20)
    private String cancelledBillingDocument;

    private BigDecimal totalNetAmount;

    @Column(length = 5)
    private String transactionCurrency;

    @Column(length = 10)
    private String companyCode;

    @Column(length = 10)
    private String fiscalYear;

    @Column(length = 20)
    private String accountingDocument;

    @Column(length = 20)
    private String soldToParty;
}
