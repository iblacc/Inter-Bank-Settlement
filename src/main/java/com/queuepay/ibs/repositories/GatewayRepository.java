package com.queuepay.ibs.repositories;

import com.queuepay.ibs.models.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GatewayRepository extends JpaRepository<PaymentGateway, Integer> {
    Optional<PaymentGateway> findByName(String name);
}
