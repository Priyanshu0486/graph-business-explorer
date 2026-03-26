package com.data_modelling.appBackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "business_partner")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessPartner {

    @Id
    @Column(name = "business_partner", length = 20)
    private String businessPartner;

    @Column(length = 20)
    private String customer;

    @Column(length = 200)
    private String businessPartnerFullName;

    @Column(length = 100)
    private String businessPartnerName;

    @Column(length = 10)
    private String businessPartnerCategory;

    @Column(length = 10)
    private String businessPartnerGrouping;

    @Column(length = 100)
    private String industry;

    @Column(length = 10)
    private String language;

    @Column(length = 10)
    private String organizationBpName1;

    @Column(length = 5)
    private String searchTerm1;

    @Column(length = 2)
    private String businessPartnerType;
}
