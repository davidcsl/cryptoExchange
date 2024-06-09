package com.aquariux.crypto.repository;

import com.aquariux.crypto.entity.TransactionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/** Transaction Repository. */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {


  @Query(nativeQuery = true,
          value = "SELECT TOP 1 * FROM TRANSACTION "
                  + "WHERE USER_ID = :userId ORDER BY TIMESTAMP DESC")
  TransactionEntity findFirstByOrderByTimestampDesc(String userId);

  List<TransactionEntity> findAllByUserId(String userId);
}
