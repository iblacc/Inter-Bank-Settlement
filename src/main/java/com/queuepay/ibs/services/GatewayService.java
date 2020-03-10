package com.queuepay.ibs.services;

import com.queuepay.ibs.exceptions.CustomException;
import com.queuepay.ibs.models.Bank;
import com.queuepay.ibs.models.PaymentGateway;
import com.queuepay.ibs.repositories.BankRepository;
import com.queuepay.ibs.repositories.GatewayRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GatewayService {

    private GatewayRepository gatewayRepository;
    private BankRepository bankRepository;

    @Autowired
    public GatewayService(GatewayRepository gatewayRepository, BankRepository bankRepository) {
        this.gatewayRepository = gatewayRepository;
        this.bankRepository = bankRepository;
    }


    public ResponseEntity<List<PaymentGateway>> getAllGateways() {
        List<PaymentGateway> gateways = gatewayRepository.findAll();
        return new ResponseEntity<>(gateways, HttpStatus.OK);
    }

    public ResponseEntity<PaymentGateway> getGateway(int id) {
        Optional<PaymentGateway> gateway = gatewayRepository.findById(id);
        if(gateway.isEmpty()) {
            throw new EntityNotFoundException(String.format("Payment gateway with {id=%d} was not found.", id));
        }

        return new ResponseEntity<>(gateway.get(), HttpStatus.FOUND);
    }

    public ResponseEntity<HashMap<String, String>> addGateway(int bankId, PaymentGateway paymentGateway) {
        Optional<Bank> bank = bankRepository.findById(bankId);

        if(bank.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND, String.format("Bank with {id=%d} was not found.", bankId));
        }

        String secretKey = RandomStringUtils.randomAlphanumeric(64);
        paymentGateway.setSecretKey(hash(secretKey));
        paymentGateway.setBank(bank.get());

        gatewayRepository.save(paymentGateway);
        HashMap<String, String> response = new HashMap<>(
                Map.of(
                        "email", paymentGateway.getEmail(),
                        "secretKey", secretKey
                )
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    public ResponseEntity<String> removeGateway(int id) {
        gatewayRepository.deleteById(id);
        return new ResponseEntity<>("Gateway removed successfully", HttpStatus.OK);
    }

    private String hash(String secret) {
        return BCrypt.hashpw(secret, BCrypt.gensalt(11));
    }
}
