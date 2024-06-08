package com.aquariux.crypto.endpoints;

import com.aquariux.crypto.dto.response.PairDetails;
import com.aquariux.crypto.service.CryptoService;
import com.aquariux.crypto.service.PriceAggregators;
import java.util.List;
import lombok.AllArgsConstructor;
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
}
