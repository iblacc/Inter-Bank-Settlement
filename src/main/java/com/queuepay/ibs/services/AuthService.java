package com.queuepay.ibs.services;

import com.queuepay.ibs.dto.CardValidation;
import com.queuepay.ibs.models.Card;
import com.queuepay.ibs.models.PaymentGateway;
import com.queuepay.ibs.repositories.AuthRepository;
import com.queuepay.ibs.repositories.CardRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private AuthRepository authRepository;
    private CardRepository cardRepository;

    @Autowired
    public AuthService(AuthRepository authRepository, CardRepository cardRepository) {
        this.authRepository = authRepository;
        this.cardRepository = cardRepository;
    }

    public ResponseEntity<Object> validate(String name, String secretKey, CardValidation card) {
        Optional<PaymentGateway> foundGateway = authRepository.findByName(name);

        if(foundGateway.isPresent()) {
            PaymentGateway gateway = foundGateway.get();

            boolean gatewayAuthenticated = BCrypt.checkpw(secretKey, gateway.getSecretKey());

            if(gatewayAuthenticated && validateCard(card)) {
                // Rest template or web client
                // return success
            }

            // return failure
        }

        // return failure
        return null;
    }

    private boolean validateCard(CardValidation cardToFind) {
        Optional<Card> foundCard = cardRepository.findByPAN(cardToFind.getPAN());

        if(foundCard.isPresent()) {
            Card card = foundCard.get();
            boolean validPin = BCrypt.checkpw(cardToFind.getPin(), card.getPin());
            boolean validCVV = BCrypt.checkpw(cardToFind.getCVV(), card.getCVV());

            return (validPin && validCVV);
        }

        return false;
    }

    // BCrypt.hashpw(password, BCrypt.gensalt(11));
}
