package com.queuepay.ibs;

import com.queuepay.ibs.dto.Account;
import com.queuepay.ibs.dto.CardValidation;
import com.queuepay.ibs.dto.Token;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
public class IbsApplication {

    public static void main(String[] args) {
        SpringApplication.run(IbsApplication.class, args);
    }

//    @Bean
//    public ModelMapper modelMapper() {
//        return new ModelMapper();
//    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Token token() {
        return new Token();
    }

    @Bean
    public CardValidation cardValidation() {
        return new CardValidation();
    }

    @Bean
    public Account account() {
        return new Account();
    }
}
