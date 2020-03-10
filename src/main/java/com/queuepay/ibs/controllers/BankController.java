package com.queuepay.ibs.controllers;

import com.queuepay.ibs.models.Bank;
import com.queuepay.ibs.services.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "banks")
public class BankController {

    private BankService bankService;

    @Autowired
    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public ResponseEntity<List<Bank>> getAllBanks() {
        return bankService.getAllBanks();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addBank(@RequestBody Bank bank) {
        return bankService.addBank(bank);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<String> updateBank(@PathVariable("id") int id, @RequestBody Bank bank) {
        return bankService.updateBank(id, bank);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> removeBank(@PathVariable("id") int id) {
        return bankService.removeBank(id);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Bank> getBank(@PathVariable("id") int id) {
        return bankService.getBank(id);
    }
}
