package com.queuepay.ibs.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "banks")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(generator = "UUID")
//    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private int id;

    @NotNull(message = "Must provide name")
    @NotBlank(message = "Name mustn't be empty")
    @Column(unique = true, updatable = false)
    private String name;

    @NotNull(message = "Must provide short code")
    @NotBlank(message = "Short code mustn't be empty")
    @Column(name = "short_code", unique = true, updatable = false)
    private String shortCode;

    @NotNull(message = "Must provide CBN code")
    @NotBlank(message = "CBN code mustn't be empty")
    @Column(name = "CBN_code", unique = true, updatable = false)
    private String cbnCode;

    @NotNull(message = "Must provide endpoint")
    @NotBlank(message = "Endpoint mustn't be empty")
    @Column(unique = true, updatable = true)
    private String endpoint;

    public Bank() {
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

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getCbnCode() {
        return cbnCode;
    }

    public void setCbnCode(String CBNCode) {
        this.cbnCode = CBNCode;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String toString() {
        return "Bank{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", cbnCode='" + cbnCode + '\'' +
                ", endpoint='" + endpoint + '\'' +
                '}';
    }
}
