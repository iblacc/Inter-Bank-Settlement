package com.queuepay.ibs.controllers;

import com.queuepay.ibs.dto.CardValidation;
import com.queuepay.ibs.models.Card;
import com.queuepay.ibs.services.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/auth")
public class AuthController {

    private AuthService authService;

    private ModelMapper modelMapper;

    @Autowired
    public AuthController(AuthService authService, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<Object> validateCard(@RequestHeader("name") String name,
                                       @RequestHeader("secret-key") String secretKey, @RequestBody Card card) {
        CardValidation cardValidation = modelMapper.map(card, CardValidation.class);

        return authService.validate(name, secretKey, cardValidation);

    }
}
