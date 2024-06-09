package com.aquariux.crypto.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Crypto Transaction Response object.
 */
@ToString
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResponse {

  private String userId;
  private String action;
  private String pair;
  private Float unit;
  private Float price;
  private String transactionId;
  private LocalDateTime timestamp;
  private String status;

}
