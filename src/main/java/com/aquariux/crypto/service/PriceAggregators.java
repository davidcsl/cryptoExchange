package com.aquariux.crypto.service;

import com.aquariux.crypto.dto.response.PairDetails;
import com.aquariux.crypto.dto.response.PairResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Crypto Price Aggregator from externals.
 */
@Component
@Slf4j
public class PriceAggregators {

  @Value("${rest.baseuri.binance}")
  private String binanceUri;

  @Value("${rest.baseuri.huobi}")
  private String huobiUri;

  private final RestTemplate restTemplate = new RestTemplate();

  /** Method to extract crypto prices from externals. */
  public List<PairDetails> extractPrice() {

    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);

    final HttpEntity httpEntity = new HttpEntity<>(httpHeaders);

    ResponseEntity<List<PairDetails>> responseEntity =
            restTemplate.exchange(binanceUri,
                    HttpMethod.GET,
                    httpEntity,
                    new ParameterizedTypeReference<List<PairDetails>>(){});

    Map<String, PairDetails> pairDetailsFromBinance = Optional
            .ofNullable(responseEntity.getBody())
            .map(list -> list.stream()
                    .filter(pair -> pair.getSymbol().equalsIgnoreCase("BTCUSDT")
                            || pair.getSymbol().equalsIgnoreCase("ETHUSDT"))
                    .collect(Collectors.toMap(PairDetails::getSymbol, pairDetails -> pairDetails)))
            .orElse(new HashMap<>());

    log.info("Extracted price from {}: {}", "Binance", pairDetailsFromBinance);

    ResponseEntity<PairResult> responseEntity2 = restTemplate.exchange(huobiUri,
            HttpMethod.GET,
            httpEntity,
            PairResult.class);

    Map<String, PairDetails> pairDetailsFromHuobi = Optional
            .ofNullable(responseEntity2.getBody())
            .map(PairResult::getData)
            .map(list -> list.stream()
                    .filter(pair -> pair.getSymbol().equalsIgnoreCase("BTCUSDT")
                            || pair.getSymbol().equalsIgnoreCase("ETHUSDT"))
                    .collect(Collectors.toMap(pairDetails -> pairDetails.getSymbol().toUpperCase(),
                            pairDetails -> pairDetails)))
            .orElse(new HashMap<>());

    log.info("Extracted price from {}: {}", "Huobi", pairDetailsFromHuobi);



    double binanceAsk = Optional.ofNullable(pairDetailsFromBinance.get("BTCUSDT"))
            .map(pairDetails -> Double.parseDouble(pairDetails.getAsk()))
            .orElse(0.00);

    double huobiAsk = Optional.ofNullable(pairDetailsFromHuobi.get("BTCUSDT"))
            .map(pairDetails -> Double.parseDouble(pairDetails.getAsk()))
            .orElse(0.00);

    return new ArrayList<>();
  }
}
