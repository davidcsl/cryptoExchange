package com.aquariux.crypto.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Crypto Wallet Response object.
 */
@ToString
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class WalletResponse {

  private String userId;
  private Float btcBalance;
  private Float ethBalance;
  private Float usdtBalance;
}
