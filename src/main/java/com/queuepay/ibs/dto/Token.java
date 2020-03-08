package com.queuepay.ibs.dto;

public class Token {
    private String OTP;
    private String accountDetail;
    private double amount;
    private boolean isCard;
    private String bankCBNCode;

    public String getAccountDetail() {
        return accountDetail;
    }

    public String getOTP() {
        return OTP;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }

    public void setAccountDetail(String accountDetail) {
        this.accountDetail = accountDetail;
    }

    public boolean isCard() {
        return isCard;
    }

    public void setCard(boolean card) {
        isCard = card;
    }

    public String getBankCBNCode() {
        return bankCBNCode;
    }

    public void setBankCBNCode(String bankCBNCode) {
        this.bankCBNCode = bankCBNCode;
    }

    @Override
    public String toString() {
        return "Token{" +
                "OTP='" + OTP + '\'' +
                ", accountDetail='" + accountDetail + '\'' +
                ", amount=" + amount +
                ", isCard=" + isCard +
                ", bankCBNCode='" + bankCBNCode + '\'' +
                '}';
    }
}
