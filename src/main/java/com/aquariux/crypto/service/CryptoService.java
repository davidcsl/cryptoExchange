package com.aquariux.crypto.service;

import com.aquariux.crypto.dto.request.TransactionRequest;
import com.aquariux.crypto.dto.response.HistoryResponse;
import com.aquariux.crypto.dto.response.PairDetails;
import com.aquariux.crypto.dto.response.WalletResponse;
import com.aquariux.crypto.entity.PriceEntity;
import com.aquariux.crypto.entity.TransactionEntity;
import com.aquariux.crypto.repository.PriceRepository;
import com.aquariux.crypto.repository.TransactionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Service class for Crypto Trading Platform. */
@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoService {

  private final PriceRepository priceRepository;
  private final TransactionRepository transactionRepository;

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
  public String transact(TransactionRequest request) throws Exception {

    log.info("Crypto transaction begin. Transaction received: " + request);

    String action = Optional.ofNullable(request).map(TransactionRequest::getAction).orElse(null);

    if (Objects.isNull(action)
            || Objects.isNull(request.getPair())
            || Objects.isNull(request.getUnit())) {
      throw new Exception("Invalid transaction request: Null request detected.");
    }

    if (!request.getPair().equals("BTCUSDT") && !request.getPair().equals("ETHUSDT")) {
      throw new Exception("Invalid transaction request: Invalid pair detected");
    }

    TransactionEntity latestTransaction =
            transactionRepository.findFirstByOrderByTimestampDesc(request.getUserId());
    log.info("Latest transaction is: " + latestTransaction);

    if (Objects.isNull(latestTransaction)) {
      throw new Exception(
              "No previous transaction available for this user: " + request.getUserId());
    }

    TransactionEntity newTransaction = new TransactionEntity();
    List<PriceEntity> priceEntities = priceRepository.findAll();
    AtomicBoolean isPairExist = new AtomicBoolean(false);
    if (!priceEntities.isEmpty()) {
      priceEntities.stream()
              .filter(priceEntity -> priceEntity.getSymbol().equals(request.getPair()))
              .findFirst()
              .ifPresent(priceEntity -> {
                isPairExist.set(true);
                if (request.getAction().equals("BUY")) {
                  Float currentUsdtBalance = latestTransaction.getUsdtBalance();
                  Float usdtCost = request.getUnit() * priceEntity.getAsk();
                  Float newUsdtBalance = currentUsdtBalance - usdtCost;
                  if (newUsdtBalance < 0) {
                    throw new RuntimeException("Transaction cancelled: Insufficient USDT Balance.");
                  }
                  newTransaction.setUsdtBalance(newUsdtBalance);

                  switch (request.getPair()) {
                    case "BTCUSDT" -> {
                      Float currentBtcBalance = latestTransaction.getBtcBalance();
                      Float btcCost = request.getUnit();
                      Float newBtcBalance = currentBtcBalance + btcCost;
                      newTransaction.setBtcBalance(newBtcBalance);
                      newTransaction.setEthBalance(latestTransaction.getEthBalance());
                    }
                    case "ETHUSDT" -> {
                      Float currentEthBalance = latestTransaction.getEthBalance();
                      Float ethCost = request.getUnit();
                      Float newEthBalance = currentEthBalance + ethCost;
                      newTransaction.setEthBalance(newEthBalance);
                      newTransaction.setBtcBalance(latestTransaction.getBtcBalance());
                    }
                    default -> throw new RuntimeException("Invalid crypto pair detected.");
                  }
                  newTransaction.setPair(request.getPair());
                  newTransaction.setUnit(request.getUnit());
                  newTransaction.setPrice(priceEntity.getAsk());
                  newTransaction.setTimestamp(LocalDateTime.now());
                  newTransaction.setAction(request.getAction());
                  newTransaction.setTransactionId(String.valueOf(UUID.randomUUID()));
                  newTransaction.setStatus("SUCCEED");
                  newTransaction.setUserId(latestTransaction.getUserId());
                  transactionRepository.saveAndFlush(newTransaction);

                } else if (request.getAction().equals("SELL")) {
                  switch (request.getPair()) {
                    case "BTCUSDT" -> {
                      Float currentBtcBalance = latestTransaction.getBtcBalance();
                      Float btcCost = request.getUnit();
                      Float newBtcBalance = currentBtcBalance - btcCost;
                      if (newBtcBalance < 0) {
                        throw new RuntimeException(
                                "Transaction cancelled: Insufficient BTC Balance.");
                      }
                      newTransaction.setBtcBalance(newBtcBalance);
                      newTransaction.setEthBalance(latestTransaction.getEthBalance());
                    }
                    case "ETHUSDT" -> {
                      Float currentEthBalance = latestTransaction.getEthBalance();
                      Float ethCost = request.getUnit();
                      Float newEthBalance = currentEthBalance - ethCost;
                      if (newEthBalance < 0) {
                        throw new RuntimeException(
                                "Transaction cancelled: Insufficient ETH Balance.");
                      }
                      newTransaction.setEthBalance(newEthBalance);
                      newTransaction.setBtcBalance(latestTransaction.getBtcBalance());
                    }
                    default -> throw new RuntimeException("Invalid crypto pair detected.");
                  }

                  Float currentUsdtBalance = latestTransaction.getUsdtBalance();
                  Float usdtCost = request.getUnit() * priceEntity.getBid();
                  Float newUsdtBalance = currentUsdtBalance + usdtCost;
                  newTransaction.setUsdtBalance(newUsdtBalance);
                  newTransaction.setPair(request.getPair());
                  newTransaction.setUnit(request.getUnit());
                  newTransaction.setPrice(priceEntity.getBid());
                  newTransaction.setTimestamp(LocalDateTime.now());
                  newTransaction.setAction(request.getAction());
                  newTransaction.setTransactionId(String.valueOf(UUID.randomUUID()));
                  newTransaction.setStatus("SUCCEED");
                  newTransaction.setUserId(latestTransaction.getUserId());
                  transactionRepository.saveAndFlush(newTransaction);
                } else {
                  throw new RuntimeException("Invalid transaction action detected.");
                }
              });
    }
    if (!isPairExist.get()) {
      throw new RuntimeException("Transaction Request Pair Price not exist.");
    }
    return "DONE";
  }

  /**
   * Method to retrieve user's cryptocurrency balance.
   */
  public WalletResponse getBalance(String userId) {

    TransactionEntity latestTransaction =
            transactionRepository.findFirstByOrderByTimestampDesc(userId);

    if (Objects.isNull(latestTransaction)) {
      throw new RuntimeException("The requested userId not found: " + userId);
    }

    WalletResponse walletResponse = new WalletResponse(
            latestTransaction.getUserId(),
            latestTransaction.getBtcBalance(),
            latestTransaction.getEthBalance(),
            latestTransaction.getUsdtBalance());

    return walletResponse;
  }

  /**
   * Method to retrieve user's all time cryptocurrency trading history.
   */
  public List<HistoryResponse> getHistory(String userId) {

    List<TransactionEntity> transactionEntities =
            transactionRepository.findAllByUserId(userId);

    if (transactionEntities.isEmpty()) {
      throw new RuntimeException("No history records for user: " + userId);
    }

    List<HistoryResponse> historyResponseList =  transactionEntities.stream()
            .map(transactionEntity ->
                    new HistoryResponse(
                            transactionEntity.getUserId(),
                            transactionEntity.getPair(),
                            transactionEntity.getAction(),
                            transactionEntity.getPrice(),
                            transactionEntity.getUnit(),
                            transactionEntity.getStatus(),
                            transactionEntity.getBtcBalance(),
                            transactionEntity.getEthBalance(),
                            transactionEntity.getUsdtBalance(),
                            transactionEntity.getTransactionId(),
                            transactionEntity.getTimestamp()))
            .toList();

    return historyResponseList;
  }


}

