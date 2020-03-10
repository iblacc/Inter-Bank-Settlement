package com.queuepay.ibs.dto;

import java.util.Date;

public class CardDTO {

    private String pan;
    private String name;
    private CardType cardType;
    private String cvv;
    private String pin;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date expiryDate;

    public CardDTO(String pan, String name, CardType cardType, String cvv, String pin, Date expiryDate) {
        this.pan = pan;
        this.name = name;
        this.cardType = cardType;
        this.cvv = cvv;
        this.pin = pin;
        this.expiryDate = expiryDate;
    }

    public CardDTO() {
    }

    public String getPAN() {
        return pan;
    }

    public void setPAN(String PAN) {
        this.pan = PAN;
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
        return cvv;
    }

    public void setCVV(String CVV) {
        this.cvv = CVV;
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
