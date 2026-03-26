package com.data_modelling.appBackend.repository;

import com.data_modelling.appBackend.entity.SalesOrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderHeaderRepository extends JpaRepository<SalesOrderHeader, String> {
}
