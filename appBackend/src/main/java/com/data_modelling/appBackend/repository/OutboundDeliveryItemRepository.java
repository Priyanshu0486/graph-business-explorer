package com.data_modelling.appBackend.repository;

import com.data_modelling.appBackend.entity.OutboundDeliveryItem;
import com.data_modelling.appBackend.entity.OutboundDeliveryItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboundDeliveryItemRepository extends JpaRepository<OutboundDeliveryItem, OutboundDeliveryItemId> {
}
