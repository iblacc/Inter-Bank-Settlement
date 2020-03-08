package com.queuepay.ibs.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "payment_gateways")
public class PaymentGateway {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull(message = "Must provide name")
    @NotBlank(message = "Name mustn't be empty")
    @Column(unique = true)
    private String name;

    @NotNull(message = "Must provide secret key")
    @NotBlank(message = "Secret key mustn't be empty")
    @Column(name = "secret_key", length = 1000)
    private String secretKey;

    @OneToOne
    @JoinColumn(name = "bank")
    private Bank bank;

    @NotNull(message = "Must provide account number")
    @NotBlank(message = "Account number mustn't be empty")
    @Column(name = "account_number")
    private String accountNumber;

    public PaymentGateway() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
