package com.data_modelling.appBackend.repository;

import com.data_modelling.appBackend.entity.BusinessPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessPartnerRepository extends JpaRepository<BusinessPartner, String> {
}
