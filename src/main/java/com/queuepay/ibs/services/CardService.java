package com.queuepay.ibs.services;

import com.queuepay.ibs.models.Bank;
import com.queuepay.ibs.models.Card;
import com.queuepay.ibs.repositories.BankRepository;
import com.queuepay.ibs.repositories.CardRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    private CardRepository cardRepository;
    private BankRepository bankRepository;

    @Autowired
    public CardService(CardRepository cardRepository, BankRepository bankRepository) {
        this.cardRepository = cardRepository;
        this.bankRepository = bankRepository;
    }


    public ResponseEntity<List<Card>> getAllCards(int bankId) {
        List<Card> cards = cardRepository.findAllByBankId(bankId);
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    public ResponseEntity<List<Card>> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    public ResponseEntity<String> addCard(int bankId, Card card) {
        Optional<Bank> bank = bankRepository.findById(bankId);
        if(bank.isEmpty()) {
            throw new EntityNotFoundException(String.format("Bank with {id=%d} was not found", bankId));
        }

        System.out.println(card.getCardType());
        card.setBank(bank.get());
        card.setPAN(hash(card.getPAN()));
        card.setPin(hash(card.getPin()));
        card.setCVV(hash(card.getCVV()));
        cardRepository.save(card);
        return new ResponseEntity<>("Card added successfully.", HttpStatus.CREATED);
    }

    public ResponseEntity<String> removeCard(int bankId, long cardId) {
        cardRepository.removeCard(bankId, cardId);
        return new ResponseEntity<>("Card removed successfully.", HttpStatus.OK);
    }

    public ResponseEntity<Card> getCard(long cardId) {
        Optional<Card> card = cardRepository.findById(cardId);
        if(card.isEmpty()) {
            throw new EntityNotFoundException(String.format("Card with {id=%d} was not found", cardId));
        }

        return new ResponseEntity<>(card.get(), HttpStatus.FOUND);
    }

    private String hash(String secret) {
        return BCrypt.hashpw(secret, BCrypt.gensalt(11));
    }
}
