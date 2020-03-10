package com.queuepay.ibs.services;

import com.queuepay.ibs.models.Bank;
import com.queuepay.ibs.repositories.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class BankService {

    private BankRepository bankRepository;

    @Autowired
    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public ResponseEntity<List<Bank>> getAllBanks() {
        List<Bank> banks = bankRepository.findAll();
        return new ResponseEntity<>(banks, HttpStatus.OK);
    }

    public ResponseEntity<String> addBank(Bank bank) {
        bankRepository.save(bank);
        return new ResponseEntity<>("Bank added successfully.", HttpStatus.CREATED);
    }

    public ResponseEntity<String> updateBank(int id, Bank updateBank) {
        Bank bank = getBankById(id);

        bank.setName(updateBank.getName());
        bank.setShortCode(updateBank.getShortCode());
        bank.setCbnCode(updateBank.getCbnCode());
        bank.setEndpoint(updateBank.getEndpoint());
        bankRepository.save(bank);

        return new ResponseEntity<>(String.format("Bank with {id=%d} was updated successfully.", id), HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> removeBank(int id) {
        try {
            bankRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new EntityNotFoundException(String.format("Bank with {id=%d} was not found.", id));
        }
        return new ResponseEntity<>(String.format("Bank with {id=%d} was removed successfully.", id), HttpStatus.OK);
    }

    public ResponseEntity<Bank> getBank(int id) {
        Bank bank = getBankById(id);
        return new ResponseEntity<>(bank, HttpStatus.FOUND);
    }

    private Bank getBankById(int id) {
        Optional<Bank> bank = bankRepository.findById(id);
        if(bank.isEmpty()) {
            throw new EntityNotFoundException(String.format("Bank with {id=%d} was not found.", id));
        }

        return bank.get();
    }
}
