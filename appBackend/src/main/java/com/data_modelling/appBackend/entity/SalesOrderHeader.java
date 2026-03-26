package com.data_modelling.appBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales_order_header")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderHeader {

    @Id
    @Column(name = "sales_order", length = 20)
    private String salesOrder;

    @Column(length = 10)
    private String salesOrderType;

    @Column(length = 20)
    private String salesOrganization;

    @Column(length = 10)
    private String distributionChannel;

    @Column(length = 10)
    private String organizationDivision;

    @Column(length = 20)
    private String soldToParty;

    private LocalDate creationDate;

    private BigDecimal totalNetAmount;

    @Column(length = 5)
    private String transactionCurrency;

    @Column(length = 20)
    private String overallDeliveryStatus;

    @Column(length = 10)
    private String overallSDProcessStatus;
}
