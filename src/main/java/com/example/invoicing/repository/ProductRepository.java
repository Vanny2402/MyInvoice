package com.example.invoicing.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.invoicing.dto.ProductListDTO;
import com.example.invoicing.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT new com.example.invoicing.dto.ProductListDTO(
                p.id, p.name, p.productColor, p.productType, p.remark,
                p.price, p.purchasePrice, p.stock
            )
            FROM Product p
            ORDER BY p.name ASC
            """)
    List<ProductListDTO> findAllListProjection();

    @Query("""
            SELECT new com.example.invoicing.dto.ProductListDTO(
                p.id, p.name, p.productColor, p.productType, p.remark,
                p.price, p.purchasePrice, p.stock
            )
            FROM Product p
            WHERE p.id = :id
            """)
    Optional<ProductListDTO> findListProjectionById(@Param("id") Long id);

    @Query("""
            SELECT new com.example.invoicing.dto.ProductListDTO(
                p.id, p.name, p.productColor, p.productType, p.remark,
                p.price, p.purchasePrice, p.stock
            )
            FROM Product p
            """)
    Page<ProductListDTO> pageAllListProjection(Pageable pageable);
}
