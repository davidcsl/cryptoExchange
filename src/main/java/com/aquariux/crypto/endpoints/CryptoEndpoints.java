package com.aquariux.crypto.endpoints;

import com.aquariux.crypto.dto.request.TransactionRequest;
import com.aquariux.crypto.dto.response.HistoryResponse;
import com.aquariux.crypto.dto.response.PairDetails;
import com.aquariux.crypto.dto.response.WalletResponse;
import com.aquariux.crypto.service.CryptoService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Crypto RESTFul endpoint controller.
 */
@RestController
@RequestMapping("/")
@Slf4j
@AllArgsConstructor
public class CryptoEndpoints {
  private final CryptoService cryptoService;

  /**
   * Extract crypto price rest endpoint.
   */
  @GetMapping("price")
  public ResponseEntity getPrice() {
    try {
      List<PairDetails> pricesResponse = cryptoService.getLatestPrice();

      return ResponseEntity.ok(pricesResponse);
    } catch (Exception e) {
      log.error(e.getMessage());
      return ResponseEntity.status(500).body(e.getMessage());
    }
  }

  /**
   * Perform buy or sell crypto base on latest aggregated price rest endpoint.
   */
  @PostMapping("transact")
  public ResponseEntity transact(@RequestBody TransactionRequest transactionRequest)
          throws Exception {

    try {
      String response = cryptoService.transact(transactionRequest);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error(e.getMessage());
      return ResponseEntity.status(500).body(e.getMessage());
    }
  }

  /**
   * Retrieve crypto wallet balance rest endpoint.
   */
  @GetMapping("balance/{userId}")
  public ResponseEntity balance(@PathVariable String userId) {

    try {
      WalletResponse walletResponse = cryptoService.getBalance(userId);

      return ResponseEntity.ok(walletResponse);
    } catch (Exception e) {
      log.error(e.getMessage());
      return ResponseEntity.status(500).body(e.getMessage());
    }
  }

  /**
   * Retrieve all time user trading history rest endpoint.
   */
  @GetMapping("history/{userId}")
  public ResponseEntity history(@PathVariable String userId) {

    try {
      List<HistoryResponse> historyResponseList = cryptoService.getHistory(userId);

      return ResponseEntity.ok(historyResponseList);
    } catch (Exception e) {
      log.error(e.getMessage());
      return ResponseEntity.status(500).body(e.getMessage());
    }
  }
}
