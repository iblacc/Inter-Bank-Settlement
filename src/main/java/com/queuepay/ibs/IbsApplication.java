package com.queuepay.ibs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class IbsApplication {

    public static void main(String[] args) {

//        String password = "121212";
//        String hashPassword = hash(password);
//        System.out.println(hashPassword);
//
//        System.out.println(verifyHash("password", hashPassword));

        SpringApplication.run(IbsApplication.class, args);

    }

//    public static String hash(String password) {
//        return BCrypt.hashpw(password, BCrypt.gensalt(11));
//    }
//
//    public static boolean verifyHash(String password, String hash) {
//        return BCrypt.checkpw(password, hash);
//    }

}
