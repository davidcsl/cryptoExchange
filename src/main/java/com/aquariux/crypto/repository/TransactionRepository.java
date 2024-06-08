package com.aquariux.crypto.repository;

import com.aquariux.crypto.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Transaction Repository. */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

  TransactionEntity findFirstByOrderByTimestampDesc();
}
