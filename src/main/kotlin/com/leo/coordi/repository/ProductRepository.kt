package com.leo.coordi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query

interface ProductRepository : JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    // 카테고리의 가장 낮은 가격의 상품들 조회
    @Query("""
        SELECT p 
        FROM Product p 
        WHERE p.category = :category 
          AND p.price = (
            SELECT MIN(p2.price) 
            FROM Product p2 
            WHERE p2.category = :category)
    """)
    fun findAllMinPriceProductByCategory(category: Category): List<Product>
    
    // 카테고리의 가장 높은 가격의 상품들 조회
    @Query("""
        SELECT p 
        FROM Product p 
        WHERE p.category = :category 
          AND p.price = (
            SELECT MAX(p2.price) 
            FROM Product p2 
            WHERE p2.category = :category)
    """)
    fun findAllMaxPriceProductByCategory(category: Category): List<Product>

}

