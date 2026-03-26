package com.data_modelling.appBackend.entity;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAccountsReceivableId implements Serializable {
    private String accountingDocument;
    private String accountingDocumentItem;
}
