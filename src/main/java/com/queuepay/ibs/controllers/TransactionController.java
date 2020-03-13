package com.queuepay.ibs.controllers;

import com.queuepay.ibs.dto.Account;
import com.queuepay.ibs.dto.CardDTO;
import com.queuepay.ibs.dto.Token;
import com.queuepay.ibs.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("transaction")
public class TransactionController {

    private TransactionService transactionService;

//    private ModelMapper modelMapper;

    @Autowired
    public TransactionController(TransactionService transactionService /*ModelMapper modelMapper*/) {
//        this.modelMapper = modelMapper;
        this.transactionService = transactionService;
    }

    @PostMapping("/card")
    public ResponseEntity<Object> validateTransactionViaCard(@RequestHeader("email") String email,
                                               @RequestHeader("secret-key") String secretKey,
                                               @RequestBody HashMap<String,String> card) {
//        CardValidation cardValidation = modelMapper.map(card, CardValidation.class);

        System.out.println(card.toString());
        return transactionService.validateTransactionViaCard(email, secretKey, card);
    }
    @PostMapping("/account")
    public ResponseEntity<Object> validateTransactionViaBank(@RequestHeader("email") String email,
                                               @RequestHeader("secret-key") String secretKey,
                                               @RequestBody HashMap<String, String> account) {

        return transactionService.validateTransactionViaBank(email, secretKey, account);
    }

    @PostMapping("/token")
    public ResponseEntity<Object> enableTransaction(@RequestHeader("email") String email,
                                               @RequestHeader("secret-key") String secretKey,
                                               @RequestBody HashMap<String, String> token) {

        return transactionService.enableTransaction(email, secretKey, token);
    }
}
