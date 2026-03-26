package com.data_modelling.appBackend.repository;

import com.data_modelling.appBackend.entity.SalesOrderItem;
import com.data_modelling.appBackend.entity.SalesOrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, SalesOrderItemId> {
}
