package com.aquariux.crypto.service;

import com.aquariux.crypto.dto.response.PairDetails;
import com.aquariux.crypto.dto.response.PairResult;
import com.aquariux.crypto.entity.PriceEntity;
import com.aquariux.crypto.repository.PriceRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Crypto Price Aggregator from externals.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PriceAggregators {

  @Value("${rest.baseuri.binance}")
  private String binanceUri;

  @Value("${rest.baseuri.huobi}")
  private String huobiUri;

  private final RestTemplate restTemplate = new RestTemplate();
  private final PriceRepository priceRepository;

  /** Method to extract crypto prices from externals. */
  @Scheduled(cron = "${app.schedule.call-price.cron}")
  public void extractPrice() {

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

    storingBestPrice(pairDetailsFromBinance, pairDetailsFromHuobi, priceRepository, "BTCUSDT");
    storingBestPrice(pairDetailsFromBinance, pairDetailsFromHuobi, priceRepository, "ETHUSDT");
  }

  /** Private method to process and store best prices from the external raw prices. */
  private void storingBestPrice(Map<String, PairDetails> pairDetailsFromBinance,
                                Map<String, PairDetails> pairDetailsFromHuobi,
                                PriceRepository priceRepository,
                                String symbol) {
    PriceEntity priceEntity = new PriceEntity();
    priceEntity.setSymbol(symbol);

    float binanceAsk = Optional.ofNullable(pairDetailsFromBinance.get(symbol))
            .map(pairDetails -> Float.parseFloat(pairDetails.getAsk()))
            .orElse(Float.MAX_VALUE);
    float huobiAsk = Optional.ofNullable(pairDetailsFromHuobi.get(symbol))
            .map(pairDetails -> Float.parseFloat(pairDetails.getAsk()))
            .orElse(Float.MAX_VALUE);
    if (binanceAsk != Float.MAX_VALUE && huobiAsk != Float.MAX_VALUE) {
      priceEntity.setAsk(Math.min(binanceAsk, huobiAsk));
    }

    float binanceBid = Optional.ofNullable(pairDetailsFromBinance.get(symbol))
            .map(pairDetails -> Float.parseFloat(pairDetails.getBid()))
            .orElse(0f);
    float huobiBid = Optional.ofNullable(pairDetailsFromHuobi.get(symbol))
            .map(pairDetails -> Float.parseFloat(pairDetails.getBid()))
            .orElse(0f);
    if (binanceBid != 0f && huobiBid != 0f) {
      priceEntity.setBid(Math.max(binanceBid, huobiBid));
    }

    if (Objects.nonNull(priceEntity.getBid())
            && Objects.nonNull(priceEntity.getAsk())
            && priceEntity.getBid() > priceEntity.getAsk()) {
      float binanceSpread = binanceAsk - binanceBid;
      float huobiSpread = huobiAsk - huobiBid;
      if (binanceSpread < huobiSpread) {
        priceEntity.setAsk(binanceAsk);
        priceEntity.setBid(binanceBid);
      } else {
        priceEntity.setAsk(huobiAsk);
        priceEntity.setBid(huobiBid);
      }
    }
    priceRepository.saveAndFlush(priceEntity);
    log.info("Stored best price: " + priceEntity);

  }
}
