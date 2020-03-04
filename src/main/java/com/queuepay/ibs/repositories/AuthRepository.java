package com.queuepay.ibs.repositories;

import com.queuepay.ibs.models.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthRepository extends JpaRepository<PaymentGateway, UUID> {
    Optional<PaymentGateway> findByName(String name);
}
