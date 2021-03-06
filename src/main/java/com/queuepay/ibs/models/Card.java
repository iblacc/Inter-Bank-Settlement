package com.queuepay.ibs.models;

import com.queuepay.ibs.dto.CardType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(unique = true, updatable = false)
    @NotNull(message = "Must provide PAN")
    @NotBlank(message = "PAN mustn't be empty")
    private String PAN;

    @NotNull(message = "Must provide name")
    @NotBlank(message = "Name mustn't be empty")
    private String name;

    @NotNull(message = "Must provide card type")
    @NotBlank(message = "Card type mustn't be empty")
    @Column(name = "card_type")
    private CardType cardType;

    @NotNull(message = "Must provide CVV")
    @NotBlank(message = "CVV mustn't be empty")
    @Size(min = 3, max = 3)
    private String CVV;

    @NotNull(message = "Must provide pin")
    @NotBlank(message = "Pin mustn't be empty")
    @Size(min = 4, max = 4)
    private String pin;

    @NotNull(message = "Must provide expiry date")
    @NotBlank(message = "Expiry date mustn't be empty")
    private Date expiryDate;

    @OneToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;

    public Card() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPAN() {
        return PAN;
    }

    public void setPAN(String PAN) {
        this.PAN = PAN;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getCVV() {
        return CVV;
    }

    public void setCVV(String CVV) {
        this.CVV = CVV;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }
}
