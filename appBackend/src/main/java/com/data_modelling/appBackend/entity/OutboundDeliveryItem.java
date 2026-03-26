package com.data_modelling.appBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "outbound_delivery_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(OutboundDeliveryItemId.class)
public class OutboundDeliveryItem {

    @Id
    @Column(name = "delivery_document", length = 20)
    private String deliveryDocument;

    @Id
    @Column(name = "delivery_document_item", length = 10)
    private String deliveryDocumentItem;

    private BigDecimal actualDeliveryQuantity;

    @Column(length = 5)
    private String deliveryQuantityUnit;

    @Column(length = 20)
    private String batch;

    @Column(length = 10)
    private String itemBillingBlockReason;

    private LocalDate lastChangeDate;

    @Column(length = 10)
    private String plant;

    @Column(length = 20)
    private String referenceSdDocument;

    @Column(length = 10)
    private String referenceSdDocumentItem;

    @Column(length = 10)
    private String storageLocation;
}
