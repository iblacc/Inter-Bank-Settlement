package com.queuepay.ibs.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.queuepay.ibs.dto.Status;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JoinColumn(name = "sending_bank")
    private Bank sendingBank;

    @OneToOne
    @JoinColumn(name = "receiving_bank")
    private Bank receivingBank;

    @NotBlank
    @NotBlank
    @Column(name = "sender_name")
    private String senderName;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime date;

    public Transaction() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
