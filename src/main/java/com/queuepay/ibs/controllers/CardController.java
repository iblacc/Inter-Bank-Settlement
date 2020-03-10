package com.queuepay.ibs.controllers;

import com.queuepay.ibs.models.Card;
import com.queuepay.ibs.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/api/v1/")
public class CardController {

    private CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("banks/{bankId}/cards")
    public ResponseEntity<List<Card>> getAllCardsByBank(@PathVariable("bankId") int bankId) {
        return cardService.getAllCards(bankId);
    }

    @GetMapping("cards")
    public ResponseEntity<List<Card>> getAllCards() {
        return cardService.getAllCards();
    }

    @PostMapping(path = "banks/{bankId}/cards", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addCard(@PathVariable("bankId") int bankId, @RequestBody Card card) {
        return cardService.addCard(bankId, card);
    }

    @GetMapping("cards/{cardId}")
    public ResponseEntity<Card> getCard(@PathVariable("cardId") long cardId) {
        return cardService.getCard(cardId);
    }

    @DeleteMapping("banks/{bankId}/cards/{cardId}")
    public ResponseEntity<String> removeCard(@PathVariable("bankId") int bankId, @PathVariable("cardId") long cardId) {
        return cardService.removeCard(bankId, cardId);
    }
}
