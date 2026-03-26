package com.data_modelling.appBackend.entity;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderItemId implements Serializable {
    private String salesOrder;
    private String salesOrderItem;
}
