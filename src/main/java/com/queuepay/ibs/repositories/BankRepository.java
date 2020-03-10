package com.queuepay.ibs.repositories;

import com.queuepay.ibs.models.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Integer> {
//    Optional<Bank> findByName(String name);
    Optional<Bank> findByCbnCode(String CBNCode);


//    @Query(value = "from Bank b where b.name = :name")
//    List<Bank> findByName(String name);
}
