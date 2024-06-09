package com.aquariux.crypto.endpoints;

import com.aquariux.crypto.dto.request.TransactionRequest;
import com.aquariux.crypto.dto.response.HistoryResponse;
import com.aquariux.crypto.dto.response.PairDetails;
import com.aquariux.crypto.dto.response.WalletResponse;
import com.aquariux.crypto.service.CryptoService;
import com.aquariux.crypto.service.PriceAggregators;
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

  private final PriceAggregators priceAggregators;
  private final CryptoService cryptoService;

  /**
   * Extract crypto price rest endpoint.
   */
  @GetMapping("price")
  public ResponseEntity<List<PairDetails>> getPrice() {

    priceAggregators.extractPrice();
    List<PairDetails> pricesResponse = cryptoService.getLatestPrice();

    return ResponseEntity.ok(pricesResponse);
  }

  /**
   * Perform buy or sell crypto base on latest aggregated price rest endpoint.
   */
  @PostMapping("transact")
  public ResponseEntity transact(@RequestBody TransactionRequest transactionRequest)
          throws Exception {

    String response = cryptoService.transact(transactionRequest);

    return ResponseEntity.ok(response);
  }

  /**
   * Retrieve crypto wallet balance rest endpoint.
   */
  @GetMapping("balance")
  public ResponseEntity balance() {

    WalletResponse walletResponse = cryptoService.getBalance();

    return ResponseEntity.ok(walletResponse);
  }

  /**
   * Retrieve all time user trading history rest endpoint.
   */
  @GetMapping("history/{userId}")
  public ResponseEntity history(@PathVariable String userId) {

    List<HistoryResponse> historyResponseList = cryptoService.getHistory(userId);

    return ResponseEntity.ok(historyResponseList);
  }
}
