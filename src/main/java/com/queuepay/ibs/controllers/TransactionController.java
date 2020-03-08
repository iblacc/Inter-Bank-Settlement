package com.queuepay.ibs.controllers;

import com.queuepay.ibs.dto.Account;
import com.queuepay.ibs.dto.CardValidation;
import com.queuepay.ibs.dto.Token;
import com.queuepay.ibs.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private TransactionService transactionService;

//    private ModelMapper modelMapper;

    @Autowired
    public TransactionController(TransactionService transactionService /*ModelMapper modelMapper*/) {
//        this.modelMapper = modelMapper;
        this.transactionService = transactionService;
    }

    @PostMapping("/card")
    public ResponseEntity<Object> validateCard(@RequestHeader("name") String name,
                                               @RequestHeader("secret-key") String secretKey,
                                               @RequestBody CardValidation card) {
//        CardValidation cardValidation = modelMapper.map(card, CardValidation.class);

        System.out.println(card.toString());
        return transactionService.validate(name, secretKey, card);
    }
    @PostMapping("/account")
    public ResponseEntity<Object> validateCard(@RequestHeader("name") String name,
                                               @RequestHeader("secret-key") String secretKey,
                                               @RequestBody Account account) {

        return transactionService.validate(name, secretKey, account);
    }

    @PostMapping("/token")
    public ResponseEntity<Object> enableTransaction(@RequestHeader("name") String name,
                                               @RequestHeader("secret-key") String secretKey,
                                               @RequestBody Token token) {

        return transactionService.enableTransaction(name, secretKey, token);
    }


    @GetMapping("/data")
    public void addBanks() {
        transactionService.createData();
    }
}
