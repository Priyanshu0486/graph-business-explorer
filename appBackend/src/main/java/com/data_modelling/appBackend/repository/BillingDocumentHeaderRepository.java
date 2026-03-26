package com.data_modelling.appBackend.repository;

import com.data_modelling.appBackend.entity.BillingDocumentHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingDocumentHeaderRepository extends JpaRepository<BillingDocumentHeader, String> {
}
