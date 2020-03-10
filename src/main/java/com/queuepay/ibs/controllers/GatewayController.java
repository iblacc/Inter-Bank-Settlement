package com.queuepay.ibs.controllers;

import com.queuepay.ibs.models.PaymentGateway;
import com.queuepay.ibs.services.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("gateways")
public class GatewayController {

    private GatewayService gatewayService;

    @Autowired
    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }


    @GetMapping
    public ResponseEntity<List<PaymentGateway>> getAllGateways() {
        return gatewayService.getAllGateways();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentGateway> getGateway(@PathVariable("id") int id) {
        return gatewayService.getGateway(id);
    }

    @PostMapping(path = "/{bankId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, String>> addGateway(@PathVariable("bankId") int bankId,
                                                              @RequestBody PaymentGateway paymentGateway) {
        return gatewayService.addGateway(bankId, paymentGateway);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeGateway(@PathVariable("id") int id) {
        return gatewayService.removeGateway(id);
    }
}
