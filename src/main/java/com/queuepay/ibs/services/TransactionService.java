package com.queuepay.ibs.services;

import com.queuepay.ibs.dto.*;
import com.queuepay.ibs.exceptions.CustomException;
import com.queuepay.ibs.models.Bank;
import com.queuepay.ibs.models.Card;
import com.queuepay.ibs.models.PaymentGateway;
import com.queuepay.ibs.models.Transaction;
import com.queuepay.ibs.repositories.BankRepository;
import com.queuepay.ibs.repositories.CardRepository;
import com.queuepay.ibs.repositories.GatewayRepository;
import com.queuepay.ibs.repositories.TransactionRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

@Service
public class TransactionService {

    private GatewayRepository gatewayRepository;
    private CardRepository cardRepository;
    private RestTemplate restTemplate;
    private BankRepository bankRepository;
    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(GatewayRepository gatewayRepository,
                              CardRepository cardRepository,
                              RestTemplate restTemplate,
                              BankRepository bankRepository,
                              TransactionRepository transactionRepository) {
        this.gatewayRepository = gatewayRepository;
        this.cardRepository = cardRepository;
        this.restTemplate = restTemplate;
        this.bankRepository = bankRepository;
        this.transactionRepository = transactionRepository;
    }

    public ResponseEntity<Object> validate(String email, String secretKey, CardDTO card) {

        authenticateGateway(email, secretKey);
        Card validatedCard = validateCard(card);

        if(validatedCard != null) {
            HashMap<String, String> payload = new HashMap<>();
            payload.put("PAN", validatedCard.getPAN());

            return sendTransactionDetails(payload, validatedCard.getBank().getEndpoint() + "/verification");
        }

        throw new CustomException(HttpStatus.NOT_ACCEPTABLE, "Invalid card provided");
    }

    public ResponseEntity<Object> validate(String email, String secretKey, Account account) {

        authenticateGateway(email, secretKey);
        Optional<Bank> bank = bankRepository.findByCbnCode(account.getBankCBNCode());

        if(bank.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_ACCEPTABLE, "Invalid account details provided");
        }

        HashMap<String, String> payload = new HashMap<>();
        payload.put("account-number", account.getAccountNumber());
        return sendTransactionDetails(payload, bank.get().getEndpoint() + "/verification");
    }


    public ResponseEntity<Object> enableTransaction(String email, String secretKey, Token token) {
        PaymentGateway gateway = authenticateGateway(email, secretKey);

        HashMap<String, String> payload = new HashMap<>();
        payload.put("OTP", token.getOTP());
        payload.put("account-detail", token.getAccountDetail());
        payload.put("amount", token.getAmount() + "");


        Transaction transaction = new Transaction();
        Bank sendingBank;
        Bank receivingBank = gateway.getBank();
        HttpStatus responseStatus;
        String responseMessage = "";

        if (token.isCard()) {
            Card card = cardRepository.findByPAN(token.getAccountDetail()).get();
            sendingBank = card.getBank();
        } else {
            sendingBank = bankRepository.findByCbnCode(token.getBankCBNCode()).get();
        }
        transaction.setSendingBank(sendingBank);
        transaction.setReceivingBank(receivingBank);
        transaction.setReceiverAccount(gateway.getAccountNumber());

        try {

            ResponseEntity<Object> responseEntity = sendTransactionDetails(payload, getEndpoint(token) + "/debit");

            HashMap<String, String> responseBody = (HashMap<String, String>) responseEntity.getBody();

            assert responseBody != null;
            transaction.setSenderName(responseBody.get("name"));
            transaction.setSenderAccount(responseBody.get("account-number"));

            payload.remove("OTP");
            payload.put("account-detail", gateway.getAccountNumber());

            sendTransactionDetails(payload, gateway.getBank().getEndpoint() + "/credit");
            transaction.setStatus(Status.SUCCESSFUL);
            responseStatus = HttpStatus.OK;
            transactionRepository.save(transaction);
            responseMessage = "Transaction successful";



        } catch (HttpStatusCodeException ex) {

            String[] responseBody = ex.getResponseBodyAsString().split(" ");
            transaction.setSenderName(responseBody[0]);
            transaction.setSenderAccount(responseBody[1]);

            if(ex.getStatusCode().equals(HttpStatus.FAILED_DEPENDENCY)) {
                transaction.setStatus(Status.FAILED);
                responseStatus = HttpStatus.FAILED_DEPENDENCY;
                responseMessage = "Insufficient fund, transaction failed";
            } else if(ex.getStatusCode().equals(HttpStatus.EXPECTATION_FAILED)) {
                payload.put("account-detail", token.getAccountDetail());
                sendTransactionDetails(payload, getEndpoint(token) + "/credit");
                transaction.setStatus(Status.FAILED);
                responseStatus = HttpStatus.EXPECTATION_FAILED;
                responseMessage = "Incorrect merchant account number, transaction failed.";
            } else {
                responseStatus = HttpStatus.NOT_ACCEPTABLE;
                responseMessage = "Transaction failed.";
                transaction.setStatus(Status.FAILED);
            }
        }

        transactionRepository.save(transaction);

        return new ResponseEntity<>(responseMessage, responseStatus);
    }

    private PaymentGateway authenticateGateway(String email, String secretKey) {
        Optional<PaymentGateway> foundGateway = gatewayRepository.findByEmail(email);

        if(foundGateway.isPresent()) {
            PaymentGateway gateway = foundGateway.get();

            boolean gatewayAuthenticated = BCrypt.checkpw(secretKey, gateway.getSecretKey());

            if (!gatewayAuthenticated) {
                throw new CustomException(HttpStatus.UNAUTHORIZED, "Incorrect secret key provided.");
            }

            return gateway;
        }

        throw new EntityNotFoundException(String.format("Payment gateway with the email {%s} was not found.", email));
    }

    private Card validateCard(CardDTO cardToFind) {
        Optional<Card> foundCard = cardRepository.findByPAN(cardToFind.getPAN());

        if(foundCard.isPresent()) {
            Card card = foundCard.get();
            boolean validPin = BCrypt.checkpw(cardToFind.getPin(), card.getPin());
            boolean validCVV = BCrypt.checkpw(cardToFind.getCVV(), card.getCVV());

            return (validPin && validCVV) ? foundCard.get() : null;
        }

        return null;
    }

    private String getEndpoint(Token token) {
        if(token.isCard()) {
            Optional<Card> card = cardRepository.findByPAN(token.getAccountDetail());
            if(card.isPresent()) {
                return card.get().getBank().getEndpoint();
            }
            throw new CustomException(HttpStatus.NOT_ACCEPTABLE, "Invalid card PAN provided");
        }

        Optional<Bank> bank = bankRepository.findByCbnCode(token.getBankCBNCode());
        if(bank.isPresent()) {
            return bank.get().getEndpoint();
        }
        throw new CustomException(HttpStatus.NOT_ACCEPTABLE, "Invalid bank CBN Code provided");
    }

    private ResponseEntity<Object> sendTransactionDetails(HashMap<String, String> payload, String url) {
        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<HashMap<String, String>> entity = new HttpEntity<>(payload, headers);
        return restTemplate.exchange(
                url, HttpMethod.POST, entity, Object.class);
    }

    private String hash(String secret) {
        return BCrypt.hashpw(secret, BCrypt.gensalt(11));
    }

}
