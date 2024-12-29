package com.leo.coordi.repository

import com.leo.coordi.service.CoordiProduct
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class ProductCustomRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    // 카테고리 별 최저가격 브랜드, 상품가격과 총액 조회
    // 한번의 쿼리로 처리하기 위해 jdbcTemplate 로 처리함
    // jpa 로 처리할 경우 한번의 쿼리로 처리하기가 복잡하고 어렵다.
    fun findMinPriceCoordi(): List<CoordiProduct> {
        val sql = """
            select p1.category, min(brand) brand, min(price) price
            from product p1 join (
              select _p.category, min(_p.price) as min_price
              from product _p
              group by category
            ) p2 on p1.category = p2.category and p1.price = p2.min_price
            group by p1.category
        """.trimIndent()

        val coordiProducts = jdbcTemplate.query(sql) { rs, _ ->
            val category = rs.getString("category")
            val categoryDisplayName = Category.valueOf(category).displayName
            CoordiProduct(category, categoryDisplayName, rs.getString("brand"), rs.getInt("price"))
        }

        // 카테고리 정의된 순서로 소팅
        coordiProducts.sortBy { Category.valueOf(it.category).ordinal }

        return coordiProducts
    }

    // 브랜드, 카테고리 별 최저가 상품의 가격 조회
    // 한번의 쿼리로 처리하기 위해 jdbcTemplate 로 처리함
    // jpa 로 처리할 경우 한번의 쿼리로 처리하기가 복잡하고 어렵다.
    fun findMinPriceAllBrandCoordi(): List<CoordiProduct> {
        val sql = """
            select p1.brand, p1.category, min(price) price
            from product p1 join (
              select _p.brand, _p.category, min(_p.price) min_price
              from product _p
              group by brand, category
            ) p2 on p1.brand = p2.brand and p1.category = p2.category and p1.price = p2.min_price
            group by p1.brand, p1.category
            order by p1.brand, p1.category 
        """.trimIndent()

        val coordiProducts = jdbcTemplate.query(sql) { rs, _ ->
            val category = rs.getString("category")
            val categoryDisplayName = Category.valueOf(category).displayName
            CoordiProduct(category, categoryDisplayName, rs.getString("brand"), rs.getInt("price"))
        }.toList()

        return coordiProducts
    }
}