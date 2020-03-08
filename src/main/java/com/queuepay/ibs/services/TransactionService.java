package com.queuepay.ibs.services;

import com.queuepay.ibs.dto.Account;
import com.queuepay.ibs.dto.CardValidation;
import com.queuepay.ibs.dto.Status;
import com.queuepay.ibs.dto.Token;
import com.queuepay.ibs.exceptions.CustomException;
import com.queuepay.ibs.models.Bank;
import com.queuepay.ibs.models.Card;
import com.queuepay.ibs.models.PaymentGateway;
import com.queuepay.ibs.models.Transaction;
import com.queuepay.ibs.repositories.GatewayRepository;
import com.queuepay.ibs.repositories.BankRepository;
import com.queuepay.ibs.repositories.CardRepository;
import com.queuepay.ibs.repositories.TransactionRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

@Service
public class TransactionService {

    private GatewayRepository authRepository;
    private CardRepository cardRepository;
    private RestTemplate restTemplate;
    private BankRepository bankRepository;
    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(GatewayRepository authRepository,
                              CardRepository cardRepository,
                              RestTemplate restTemplate,
                              BankRepository bankRepository,
                              TransactionRepository transactionRepository) {
        this.authRepository = authRepository;
        this.cardRepository = cardRepository;
        this.restTemplate = restTemplate;
        this.bankRepository = bankRepository;
        this.transactionRepository = transactionRepository;
    }

    public ResponseEntity<Object> validate(String name, String secretKey, CardValidation card) {

        authenticateGateway(name, secretKey);
        Card validatedCard = validateCard(card);

        if(validatedCard != null) {
            HashMap<String, String> payload = new HashMap<>();
            payload.put("PAN", validatedCard.getPAN());

            return sendTransactionDetails(payload, validatedCard.getBank().getEndpoint() + "/verification");
        }

        throw new CustomException(HttpStatus.NOT_ACCEPTABLE, "Invalid card provided");
    }

    public ResponseEntity<Object> validate(String name, String secretKey, Account account) {

        authenticateGateway(name, secretKey);
        Optional<Bank> bank = bankRepository.findByCBNCode(account.getBankCBNCode());

        if(bank.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_ACCEPTABLE, "Invalid account details provided");
        }

        HashMap<String, String> payload = new HashMap<>();
        payload.put("account-number", account.getAccountNumber());
        return sendTransactionDetails(payload, bank.get().getEndpoint() + "/verification");
    }


    public ResponseEntity<Object> enableTransaction(String name, String secretKey, Token token) {
        PaymentGateway gateway = authenticateGateway(name, secretKey);

        HashMap<String, String> payload = new HashMap<>();
        payload.put("OTP", token.getOTP());
        payload.put("account-detail", token.getAccountDetail());
        payload.put("amount", token.getAmount() + "");
        ResponseEntity<Object> responseEntity = sendTransactionDetails(payload, getEndpoint(token) + "/debit");

        HttpStatus debitStatus = responseEntity.getStatusCode();
        HashMap<String, String> responseBody = (HashMap<String, String>) responseEntity.getBody();
        String responseMessage = "";

        Transaction transaction = new Transaction();
        Bank sendingBank;

        if (token.isCard()) {
            Card card = cardRepository.findByPAN(token.getAccountDetail()).get();
            sendingBank = card.getBank();
        } else {
            sendingBank = bankRepository.findByCBNCode(token.getBankCBNCode()).get();
        }

        Bank receivingBank = gateway.getBank();

        transaction.setSendingBank(sendingBank);
        transaction.setReceivingBank(receivingBank);
        transaction.setSenderName(responseBody.get("name"));
        transaction.setSenderAccount(responseBody.get("account-number"));
        transaction.setReceiverAccount(gateway.getAccountNumber());

        HttpStatus responseStatus;

        if(debitStatus.equals(HttpStatus.OK)) {
            transaction.setStatus(Status.PENDING);
            responseStatus = HttpStatus.OK;

            transactionRepository.save(transaction);
            responseMessage = "Transaction pending";

            payload.remove("OTP");
            payload.put("account-detail", gateway.getAccountNumber());
            HttpStatus creditStatus = sendTransactionDetails(payload, gateway.getBank().getEndpoint() + "/credit").getStatusCode();
            if(creditStatus.equals(HttpStatus.OK)) {
                transaction.setStatus(Status.SUCCESSFUL);
                transactionRepository.save(transaction);
                responseMessage = "Transaction successful";
            }
        } else {
            transaction.setStatus(Status.FAILED);
            transactionRepository.save(transaction);
            responseMessage = "Transaction failed";
            responseStatus = HttpStatus.FORBIDDEN;
        }

        return new ResponseEntity<>(responseMessage, responseStatus);
    }

    private PaymentGateway authenticateGateway(String name, String secretKey) {
        Optional<PaymentGateway> foundGateway = authRepository.findByName(name);

        if(foundGateway.isPresent()) {
            PaymentGateway gateway = foundGateway.get();

            boolean gatewayAuthenticated = BCrypt.checkpw(secretKey, gateway.getSecretKey());

            if (!gatewayAuthenticated) {
                throw new CustomException(HttpStatus.UNAUTHORIZED, "Incorrect secret key provided.");
            }

            return gateway;
        }

        throw new EntityNotFoundException(String.format("Payment gateway with the name {%s} was not found.", name));
    }

    private Card validateCard(CardValidation cardToFind) {
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

        Optional<Bank> bank = bankRepository.findByCBNCode(token.getBankCBNCode());
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

//    private Bank getGatewayBank(String name) {
//        PaymentGateway paymentGateway = authRepository.findByName(name).get();
//        return paymentGateway.getBank();
//    }

    public void createData() {
//        Bank bank = new Bank();
////
//        bank.setShortCode("GTB");
//        bank.setCBNCode("0123");
//        bank.setName("Guaranty Trust Bank");
//        bankRepository.save(bank);

//        Bank bank = bankRepository.findByName("Guaranty Trust Bank").get();
//        bank.setCBNCode("22222");
//        bankRepository.save(bank);
//        Card card = new Card();
//        card.setBank(bankRepository.findByName("Guaranty Trust Bank").get());
//        card.setName("Garba Isah");
//        card.setCardType(CardType.MASTERCARD);
//        card.setCVV(hash("212"));
//        card.setPAN(hash("2222990905257051"));
//        card.setPin(hash("1112"));
//        card.setExpiryDate("2022-04-12");
//        cardRepository.save(card);
//
//
//        PaymentGateway gateway = new PaymentGateway();
//        gateway.setName("QueuePay");
//        gateway.setAccountNumber("1200020100");
//        gateway.setBank(bankRepository.findByName("Guaranty Trust Bank").get());
//        gateway.setSecretKey(hash("43RFVFRHIHhI9Hg8YHgy8GgHgNBgk9"));
//        authRepository.save(gateway);
    }

    private String hash(String secret) {
        return BCrypt.hashpw(secret, BCrypt.gensalt(11));
    }
    // BCrypt.hashpw(password, BCrypt.gensalt(11));
}
