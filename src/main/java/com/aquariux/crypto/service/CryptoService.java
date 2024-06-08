package com.aquariux.crypto.service;

import com.aquariux.crypto.dto.request.TransactionRequest;
import com.aquariux.crypto.dto.response.PairDetails;
import com.aquariux.crypto.entity.PriceEntity;
import com.aquariux.crypto.repository.PriceRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Service class for Crypto Trading Platform. */
@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoService {

  private final PriceRepository priceRepository;

  /**
   * Method to retrieve latest best aggregated price.
   */
  public List<PairDetails> getLatestPrice() {

    List<PriceEntity> priceEntities = priceRepository.findAll();

    List<PairDetails> pricesResponse = priceEntities.stream()
            .map(priceEntity -> {
              PairDetails pairDetails = new PairDetails();
              pairDetails.setSymbol(priceEntity.getSymbol());
              Optional.ofNullable(priceEntity.getBid())
                      .map(Object::toString)
                      .ifPresent(pairDetails::setBid);
              Optional.ofNullable(priceEntity.getAsk())
                      .map(Object::toString)
                      .ifPresent(pairDetails::setAsk);
              return pairDetails;
            }).toList();

    return pricesResponse;
  }

  /**
   * Method to perform crypto transaction base on latest best aggregated price.
   */
  public String transact(TransactionRequest request) {
    return "";
  }
}
