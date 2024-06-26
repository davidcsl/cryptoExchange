package com.aquariux.crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Crypto spring boot application entry point. */
@SpringBootApplication
@EnableScheduling
public class CryptoApplication {
  public static void main(String[] args) {
    SpringApplication.run(CryptoApplication.class, args);
  }
}
