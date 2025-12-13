package com.example.invoicing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.invoicing.entity.Purchase;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    @Query("SELECT p FROM Purchase p WHERE MONTH(p.createdAt) = :month AND YEAR(p.createdAt) = :year")
    List<Purchase> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
	   
	   
}


