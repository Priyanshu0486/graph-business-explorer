package com.data_modelling.appBackend.repository;

import com.data_modelling.appBackend.entity.PaymentAccountsReceivable;
import com.data_modelling.appBackend.entity.PaymentAccountsReceivableId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentAccountsReceivableRepository extends JpaRepository<PaymentAccountsReceivable, PaymentAccountsReceivableId> {
}
