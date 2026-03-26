package com.data_modelling.appBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_accounts_receivable")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PaymentAccountsReceivableId.class)
public class PaymentAccountsReceivable {

    @Id
    @Column(name = "accounting_document", length = 20)
    private String accountingDocument;

    @Id
    @Column(name = "accounting_document_item", length = 10)
    private String accountingDocumentItem;

    @Column(length = 10)
    private String companyCode;

    @Column(length = 10)
    private String fiscalYear;

    private LocalDate clearingDate;

    @Column(length = 20)
    private String clearingAccountingDocument;

    @Column(length = 10)
    private String clearingDocFiscalYear;

    private BigDecimal amountInTransactionCurrency;

    @Column(length = 5)
    private String transactionCurrency;

    private BigDecimal amountInCompanyCodeCurrency;

    @Column(length = 5)
    private String companyCodeCurrency;

    @Column(length = 20)
    private String customer;

    private LocalDate postingDate;

    private LocalDate documentDate;

    @Column(length = 20)
    private String glAccount;

    @Column(length = 5)
    private String financialAccountType;

    @Column(length = 20)
    private String profitCenter;
}
