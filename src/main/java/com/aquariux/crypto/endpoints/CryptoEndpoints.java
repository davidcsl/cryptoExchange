package com.aquariux.crypto.endpoints;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Crypto RESTFul endpoint controller.
 */
@RestController
@RequestMapping("/")
@Slf4j
public class CryptoEndpoints {

  @GetMapping("price")
  public ResponseEntity getPrice() {
    return ResponseEntity.ok("BTC: 70000 USDT, ETH: 3800 USDT");
  }
}
