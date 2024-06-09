package com.aquariux.crypto.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class to load crypto price upon machine startup. */
@Component
@Slf4j
public class PriceInitializer {

  /**
   * Method to Initialize Price.
   *
   * @param priceAggregators PriceAggregators
   */
  @Autowired
  public PriceInitializer(PriceAggregators priceAggregators) {
    log.info("Initialize crypto Price upon machine startup.");
    priceAggregators.extractPrice();
  }
}
