package com.data_modelling.appBackend.repository;

import com.data_modelling.appBackend.entity.OutboundDeliveryHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboundDeliveryHeaderRepository extends JpaRepository<OutboundDeliveryHeader, String> {
}
