package com.queuepay.ibs.repositories;

import com.queuepay.ibs.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    Optional<Card> findByPAN(String PAN);
}
