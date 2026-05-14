package com.example.invoicing.repository;

import com.example.invoicing.entity.Purchase;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("""
        SELECT DISTINCT p
        FROM Purchase p
        LEFT JOIN FETCH p.items i
        LEFT JOIN FETCH i.product
        WHERE p.id = :id
        """)
    Optional<Purchase> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT p
        FROM Purchase p
        LEFT JOIN FETCH p.items i
        LEFT JOIN FETCH i.product
        WHERE p.createdAt BETWEEN :start AND :end
        ORDER BY p.createdAt DESC
    """)
    List<Purchase> findByDateRangeWithItems(
            @Param("start") ZonedDateTime start,
            @Param("end") ZonedDateTime end
    );

}
