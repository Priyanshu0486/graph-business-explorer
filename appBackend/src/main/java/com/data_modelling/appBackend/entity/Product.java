package com.data_modelling.appBackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @Column(name = "product", length = 40)
    private String product;

    @Column(length = 10)
    private String productType;

    @Column(length = 10)
    private String crossPlantStatus;

    private java.time.LocalDate crossPlantStatusValidityDate;

    private java.time.LocalDate creationDate;

    @Column(length = 20)
    private String createdByUser;

    private java.time.LocalDate lastChangeDate;

    @Column(length = 100)
    private String productOldId;

    private Double grossWeight;

    @Column(length = 5)
    private String weightUnit;

    private Double netWeight;

    @Column(length = 20)
    private String productGroup;

    @Column(length = 5)
    private String baseUnit;

    @Column(length = 5)
    private String division;

    @Column(length = 5)
    private String industrySector;
}
