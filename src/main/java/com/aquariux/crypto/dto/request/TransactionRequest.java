package com.aquariux.crypto.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Crypto Transaction Request object.
 */
@ToString
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionRequest {

  private String userId;
  private String action;
  private String pair;
  private Float unit;

}
