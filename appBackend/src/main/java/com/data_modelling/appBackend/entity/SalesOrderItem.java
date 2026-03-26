package com.data_modelling.appBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sales_order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(SalesOrderItemId.class)
public class SalesOrderItem {

    @Id
    @Column(name = "sales_order", length = 20)
    private String salesOrder;

    @Id
    @Column(name = "sales_order_item", length = 10)
    private String salesOrderItem;

    @Column(length = 40)
    private String material;

    @Column(length = 200)
    private String salesOrderItemText;

    private BigDecimal requestedQuantity;

    @Column(length = 5)
    private String requestedQuantityUnit;

    private BigDecimal netAmount;

    @Column(length = 5)
    private String transactionCurrency;

    @Column(length = 20)
    private String materialGroup;

    @Column(length = 10)
    private String plant;

    @Column(length = 10)
    private String storageLocation;

    @Column(length = 5)
    private String deliveryStatus;
}
