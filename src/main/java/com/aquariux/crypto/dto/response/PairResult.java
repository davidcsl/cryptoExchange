package com.aquariux.crypto.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * PairResult from Huobi data transfer object.
 */
@ToString
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PairResult {

  private List<PairDetails> data;
}
