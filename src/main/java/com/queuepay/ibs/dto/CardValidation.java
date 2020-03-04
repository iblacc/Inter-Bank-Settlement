package com.queuepay.ibs.dto;

import java.util.Date;

public class CardValidation {

    private String PAN;
    private String name;
    private CardType cardType;
    private String CVV;
    private String pin;
    private Date expiryDate;

    public CardValidation(String PAN, String name, CardType cardType, String CVV, String pin, Date expiryDate) {
        this.PAN = PAN;
        this.name = name;
        this.cardType = cardType;
        this.CVV = CVV;
        this.pin = pin;
        this.expiryDate = expiryDate;
    }

    public CardValidation() {
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
}
