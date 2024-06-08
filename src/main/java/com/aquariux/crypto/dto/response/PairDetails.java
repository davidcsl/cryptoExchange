package com.aquariux.crypto.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * PairDetails data transfer object.
 */
@ToString
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PairDetails {

  private String symbol;

  @JsonAlias({"bidPrice", "bid"})
  private String bid;

  @JsonAlias({"askPrice", "ask"})
  private String ask;

}
