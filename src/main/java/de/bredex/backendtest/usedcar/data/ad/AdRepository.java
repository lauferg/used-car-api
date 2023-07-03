package de.bredex.backendtest.usedcar.data.ad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface AdRepository extends JpaRepository<Ad, BigDecimal> {

    @Override
    Optional<Ad> findById(BigDecimal id);

    @Query("SELECT a FROM Ad a WHERE LOWER(a.make) LIKE '%' + LOWER(:make) + '%' OR LOWER(a.type) LIKE '%' + LOWER(:type) + '%'")
    Optional<Ad> findByKeywords(String make, String type);
}
