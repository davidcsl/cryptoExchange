package com.aquariux.crypto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Entity class for crypto transaction. */
@Entity
@Table(name = "TRANSACTION")
@Setter
@Getter
@ToString
@NoArgsConstructor
public class TransactionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "TRANSACTION_ID")
  private String transactionId;

  @Column(name = "USER_ID")
  private String userId;

  @Column(name = "TIMESTAMP")
  private LocalDateTime timestamp;

  @Column(name = "PAIR")
  private String pair;

  @Column(name = "ACTION")
  private String action;

  @Column(name = "PRICE")
  private Float price;

  @Column(name = "UNIT")
  private Float unit;

  @Column(name = "STATUS")
  private String status;

  @Column(name = "BTC_BALANCE")
  private Float btcBalance;

  @Column(name = "ETH_BALANCE")
  private Float ethBalance;

  @Column(name = "USDT_BALANCE")
  private Float usdtBalance;
}
