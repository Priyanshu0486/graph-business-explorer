package com.data_modelling.appBackend.repository;

import com.data_modelling.appBackend.entity.BillingDocumentItem;
import com.data_modelling.appBackend.entity.BillingDocumentItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingDocumentItemRepository extends JpaRepository<BillingDocumentItem, BillingDocumentItemId> {
}
