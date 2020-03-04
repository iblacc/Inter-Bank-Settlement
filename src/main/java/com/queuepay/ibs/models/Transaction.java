package com.queuepay.ibs.models;

import com.queuepay.ibs.dto.Status;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "sending_bank")
    private Bank sendingBank;

    @OneToOne
    @JoinColumn(name = "receiving_bank")
    private Bank receivingBank;

    @NotBlank
    @NotBlank
    @Column(name = "sender_account")
    private String senderAccount;

    @NotBlank
    @NotNull
    @Column(name = "receiver_account")
    private String receiverAccount;

    @NotNull
    private Status status;

    @CreationTimestamp
    private Timestamp date;

    public Transaction() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Bank getSendingBank() {
        return sendingBank;
    }

    public void setSendingBank(Bank sendingBank) {
        this.sendingBank = sendingBank;
    }

    public Bank getReceivingBank() {
        return receivingBank;
    }

    public void setReceivingBank(Bank receivingBank) {
        this.receivingBank = receivingBank;
    }

    public String getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public String getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(String receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
