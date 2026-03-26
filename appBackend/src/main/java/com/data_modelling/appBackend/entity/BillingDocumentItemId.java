package com.data_modelling.appBackend.entity;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingDocumentItemId implements Serializable {
    private String billingDocument;
    private String billingDocumentItem;
}
