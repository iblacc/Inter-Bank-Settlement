package com.queuepay.ibs.dto;

public class Account {
    private String bankCBNCode;
    private String accountNumber;

    public String getBankCBNCode() {
        return bankCBNCode;
    }

    public void setBankCBNCode(String bankCBNCode) {
        this.bankCBNCode = bankCBNCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        return "Account{" +
                "bankCBNCode='" + bankCBNCode + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
