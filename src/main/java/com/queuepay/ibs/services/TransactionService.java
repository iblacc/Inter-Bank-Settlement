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
import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    public ResponseEntity<Object> validateTransactionViaCard(String email, String secretKey, HashMap<String, String> request) {

        authenticateGateway(email, secretKey);
        CardDTO card = new CardDTO();
        card.setCardType(CardType.valueOf(request.get("cardType")));
        card.setCVV(request.get("cvv"));
        card.setExpiryDate(Date.valueOf(request.get("expiryDate")));
        card.setName(request.get("name"));
        card.setPAN(request.get("PAN"));
        card.setPin(request.get("pin"));

        Card validatedCard = validateCard(card);

        if(validatedCard != null) {
            System.out.println(validatedCard.getBank().getEndpoint());
            HashMap<String, String> payload = new HashMap<>();
            payload.put("PAN", validatedCard.getPAN());

            return sendTransactionDetails(payload, validatedCard.getBank().getEndpoint() + "/transaction/verification");
        }

        throw new CustomException(HttpStatus.NOT_ACCEPTABLE, "Invalid card provided");
    }

    public ResponseEntity<Object> validateTransactionViaBank(String email, String secretKey,
                                           HashMap<String, String> account) {

        authenticateGateway(email, secretKey);
        Optional<Bank> bank = bankRepository.findByCbnCode(account.get("cbnCode"));

        if(bank.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_ACCEPTABLE, "Invalid account details provided");
        }

        HashMap<String, String> payload = new HashMap<>();
        payload.put("account-number", account.get("accountNumber"));
        payload.put("email", email);
       return sendTransactionDetails(payload, bank.get().getEndpoint() + "/transaction/verification");
    // return ResponseEntity.status(HttpStatus.OK).body(payload);
    }


    public ResponseEntity<Object> enableTransaction(String email, String secretKey, HashMap<String, String> otpRequest) {
        System.out.println("a");
        PaymentGateway gateway = authenticateGateway(email, secretKey);
        Token token = new Token();
        token.setAccountDetail(otpRequest.get("accountDetail"));
        token.setAmount(Double.parseDouble(otpRequest.get("amount")));
        token.setBankCBNCode(otpRequest.get("cbnCode"));
        token.setCard(Boolean.parseBoolean(otpRequest.get("isPaymentByCard")));
        token.setOTP(otpRequest.get("token"));
        System.out.println("b");

        HashMap<String, String> payload = new HashMap<>();
        payload.put("OTP", token.getOTP());
        payload.put("account-detail", token.getAccountDetail());
        payload.put("amount", token.getAmount() + "");
        System.out.println("c");
        Transaction transaction = new Transaction();
        Bank sendingBank;
        Bank receivingBank = gateway.getBank();
        HttpStatus responseStatus;
        String responseMessage = "";
        System.out.println("d");
        if (token.isCard()) {
            System.out.println("e");
            Card card = cardRepository.findByPAN(token.getAccountDetail()).get();
            System.out.println("f");
            sendingBank = card.getBank();
            System.out.println("g");
        } else {
            System.out.println("h");
            sendingBank = bankRepository.findByCbnCode(token.getBankCBNCode()).get();
            System.out.println("i");
        }
        System.out.println("yyyy");
        transaction.setSendingBank(sendingBank);
        transaction.setReceivingBank(receivingBank);
        transaction.setReceiverAccount(gateway.getAccountNumber());
        System.out.println("xxxx");
        try {
            System.out.println("j");
            ResponseEntity<Object> responseEntity = sendTransactionDetails(payload,
                   // getEndpoint(token)
                    "http://192.168.88.143:3000/transaction/debit");
            System.out.println("k");
            HashMap<String, String> responseBody = (HashMap<String, String>) responseEntity.getBody();
            System.out.println("l");
            assert responseBody != null;
            transaction.setSenderName(responseBody.get("name"));
            transaction.setSenderAccount(responseBody.get("account-number"));
            System.out.println("m");
            payload.remove("OTP");
            payload.put("account-detail", gateway.getAccountNumber());
            System.out.println("n");
            sendTransactionDetails(payload,
                    //gateway.getBank().getEndpoint() +
                    "http://192.168.88.143:3000/transaction/credit");
            transaction.setStatus(Status.SUCCESSFUL);
            responseStatus = HttpStatus.OK;
            transactionRepository.save(transaction);
            responseMessage = "Transaction successful";


            System.out.println("o");
        } catch (HttpStatusCodeException ex) {
            System.out.println("p");
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
        System.out.println("q");
        transactionRepository.save(transaction);
        System.out.println("r");
        //return new ResponseEntity<>(responseMessage, responseStatus);
        HashMap<String, String> map = new HashMap<>();
        map.put("message", "you have paid");
        System.out.println("s");
        return  ResponseEntity.status(HttpStatus.OK).body(map);
    }

    private PaymentGateway authenticateGateway(String email, String secretKey) {
        Optional<PaymentGateway> foundGateway = gatewayRepository.findByEmail(email);

        if(foundGateway.isPresent()) {
            PaymentGateway gateway = foundGateway.get();

//            boolean gatewayAuthenticated = BCrypt.checkpw(secretKey, gateway.getSecretKey());
//
//            if (!gatewayAuthenticated) {
//                throw new CustomException(HttpStatus.UNAUTHORIZED, "Incorrect secret key provided.");
//            }

            return gateway;
        }

        throw new EntityNotFoundException(String.format("Payment gateway with the email {%s} was not found.", email));
    }

    private Card validateCard(CardDTO cardToFind) {
        Optional<Card> foundCard = cardRepository.findByPAN(cardToFind.getPAN());

        System.out.println(cardToFind.getPAN());

        if(foundCard.isPresent()) {
            Card card = foundCard.get();
            boolean validPin = BCrypt.checkpw(cardToFind.getPin(), card.getPin());
            boolean validCVV = BCrypt.checkpw(cardToFind.getCVV(), card.getCVV());

            if(validPin) System.out.println("Correct pin");
            if(validCVV) System.out.println("Correct cvv");

            return (validPin && validCVV) ? foundCard.get() : null;
        }

        System.out.println("Not found");

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
