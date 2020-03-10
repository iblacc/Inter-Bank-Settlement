package com.queuepay.ibs.repositories;

import com.queuepay.ibs.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByBankId(int bankId);

    Optional<Card> findByPAN(String PAN);

    @Query(value = "delete from Card c where c.id = :cardId and c.bank.id = :bankId")
    void removeCard(int bankId, long cardId);
}
