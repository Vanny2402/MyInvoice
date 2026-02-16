
package com.example.invoicing.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.invoicing.dto.SaleListDTO;
import com.example.invoicing.dto.TelegramSaleReportDTO;
import com.example.invoicing.dto.SaleTelegramDTO;
import com.example.invoicing.entity.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByCustomerId(Long customerId);
    @Query("""
        SELECT s
        FROM Sale s
        WHERE s.createdAt >= :start
          AND s.createdAt < :end
    """)
    Page<Sale> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    /* CURRENT MONTH DTO */
    @Query("""
        SELECT new com.example.invoicing.dto.SaleListDTO(
            s.id,
            c.id,
            c.name,
            s.totalPrice,
            s.createdAt
        )
        FROM Sale s
        JOIN s.customer c
        WHERE s.createdAt >= :start
          AND s.createdAt < :end
        ORDER BY s.createdAt DESC
    """)
    List<SaleListDTO> findCurrentMonthSales(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /* TELEGRAM REPORT */
    @Query("""
        SELECT new com.example.invoicing.dto.TelegramSaleReportDTO(
            s.id,
            c.name,
            s.createdAt,
            s.totalPrice,
            COALESCE(s.paidAmount, 0)
        )
        FROM Sale s
        JOIN s.customer c
        WHERE s.createdAt >= :start
          AND s.createdAt < :end
    """)
    List<TelegramSaleReportDTO> findInvoicesForMonth(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT new com.example.invoicing.dto.SaleTelegramDTO(
            s.id,
            p.name,
            i.qty,
            i.price,
            i.lineTotal
        )
        FROM Sale s
        JOIN s.items i
        JOIN i.product p
        WHERE s.createdAt >= :start
          AND s.createdAt < :end
        ORDER BY s.createdAt DESC, i.id ASC
    """)
    List<SaleTelegramDTO> findInvoiceItemsForMonth(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT COALESCE(SUM(s.totalPrice), 0),
               COALESCE(SUM(s.paidAmount), 0)
        FROM Sale s
        WHERE s.createdAt >= :start
          AND s.createdAt < :end
    """)
    Object sumTotalsForMonth(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
