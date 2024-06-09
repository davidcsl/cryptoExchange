package com.aquariux.crypto.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Transaction History Response object.
 */
@ToString
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class HistoryResponse {

  private String userId;
  private String pair;
  private String action;
  private Float price;
  private Float unit;
  private String status;
  private Float btcBalance;
  private Float ethBalance;
  private Float usdtBalance;
  private String transactionId;
  private LocalDateTime timestamp;
}
