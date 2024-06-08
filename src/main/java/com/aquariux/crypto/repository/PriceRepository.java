package com.aquariux.crypto.repository;

import com.aquariux.crypto.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Price Repository. */
@Repository
public interface PriceRepository extends JpaRepository<PriceEntity, String> {
}
