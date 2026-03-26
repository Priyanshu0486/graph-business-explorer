package com.data_modelling.appBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "outbound_delivery_header")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboundDeliveryHeader {

    @Id
    @Column(name = "delivery_document", length = 20)
    private String deliveryDocument;

    private LocalDate actualGoodsMovementDate;

    private LocalDate creationDate;

    @Column(length = 10)
    private String deliveryBlockReason;

    @Column(length = 5)
    private String hdrGeneralIncompletionStatus;

    @Column(length = 10)
    private String headerBillingBlockReason;

    private LocalDate lastChangeDate;

    @Column(length = 5)
    private String overallGoodsMovementStatus;

    @Column(length = 5)
    private String overallPickingStatus;

    @Column(length = 5)
    private String overallProofOfDeliveryStatus;

    @Column(length = 10)
    private String shippingPoint;
}
