package com.queuepay.ibs.repositories;

import com.queuepay.ibs.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByPAN(String PAN);
}
