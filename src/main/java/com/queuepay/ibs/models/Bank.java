package com.queuepay.ibs.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "banks")
public class Bank {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotNull(message = "Must provide name")
    @NotBlank(message = "Name mustn't be empty")
    private String name;

    @NotNull(message = "Must provide short code")
    @NotBlank(message = "Short code mustn't be empty")
    @Column(name = "short_code")
    private String shortCode;

    @NotNull(message = "Must provide CBN code")
    @NotBlank(message = "CBN code mustn't be empty")
    @Column(name = "CBN_code")
    private String CBNCode;

    public Bank() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getCBNCode() {
        return CBNCode;
    }

    public void setCBNCode(String CBNCode) {
        this.CBNCode = CBNCode;
    }
}
