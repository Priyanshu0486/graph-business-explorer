package com.data_modelling.appBackend.entity;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutboundDeliveryItemId implements Serializable {
    private String deliveryDocument;
    private String deliveryDocumentItem;
}
