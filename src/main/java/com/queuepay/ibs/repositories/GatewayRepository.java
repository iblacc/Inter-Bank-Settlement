package com.queuepay.ibs.repositories;

import com.queuepay.ibs.models.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GatewayRepository extends JpaRepository<PaymentGateway, Integer> {
    Optional<PaymentGateway> findByEmail(String email);
}
