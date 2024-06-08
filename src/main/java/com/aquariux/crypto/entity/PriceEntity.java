package com.aquariux.crypto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Entity class for crypto price. */
@Entity
@Table(name = "PRICE")
@Setter
@Getter
@ToString
@NoArgsConstructor
public class PriceEntity {

  @Id
  @Column(name = "SYMBOL")
  private String symbol;

  @Column(name = "ASK")
  private Float ask;

  @Column(name = "BID")
  private Float bid;
}
