package com.leo.coordi.repository

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

// 실제 DB 쿼리 실행을 통한 Repository 테스트
// JPA 사용하기 때문에 DataJpaTest 를 사용함
@DataJpaTest
@Suppress("NonAsciiCharacters")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Transactional
class ProductRepositoryTest @Autowired constructor(
    private val productRepository: ProductRepository,
) {
    @Test
    @Order(1)
    fun `H2 DB 초기화 직후 테스트`() {
        // -------------------------------
        // findById 메소드 테스트
        // -------------------------------
        val optionalP1 = productRepository.findById(1)
        assertNotNull(optionalP1)
        
        val p1 = optionalP1.get()

        assertEquals(1, p1.id)
        assertEquals("A", p1.brand)
        assertEquals(Category.TOP, p1.category)
        assertEquals(11200, p1.price)

        val optionalP20 = productRepository.findById(20)
        assertNotNull(optionalP20)

        val p20 = optionalP20.get()

        assertEquals(20, p20.id)
        assertEquals("C", p20.brand)
        assertEquals(Category.SNEAKERS, p20.category)
        assertEquals(9200, p20.price)

        // -------------------------------
        // findAllMinPriceProductByCategory 메소드 테스트
        // -------------------------------
        val minProducts = productRepository.findAllMinPriceProductByCategory(Category.TOP)
        assertNotNull(minProducts)
        assertEquals(1, minProducts.size)

        assertEquals(Category.TOP, minProducts[0].category)
        assertEquals("C", minProducts[0].brand)
        assertEquals(10000, minProducts[0].price)

        // -------------------------------
        // findAllMaxPriceProductByCategory 메소드 테스트
        // -------------------------------
        val maxProducts = productRepository.findAllMaxPriceProductByCategory(Category.TOP)
        assertNotNull(maxProducts)
        assertEquals(1, maxProducts.size)

        assertEquals(Category.TOP, maxProducts[0].category)
        assertEquals("I", maxProducts[0].brand)
        assertEquals(11400, maxProducts[0].price)

        // -------------------------------
        // ProductSpecification 테스트
        // -------------------------------
        val specification = ProductSpecification.withFilters(null, "C", null)
        val searchedProducts = productRepository.findAll(specification).toList()
        assertNotNull(searchedProducts)
        assertEquals(8, searchedProducts.size)
    }

    @Test
    @Order(2)
    fun `추가 상품 등록 후 테스트`() {
        val newP1 = Product(null, "C", Category.TOP, 9000)
        val newP2 = Product(null, "I", Category.TOP, 25000)
        productRepository.saveAllAndFlush(listOf(newP1, newP2))
        
        // -------------------------------
        // findById 메소드 테스트
        // -------------------------------
        val optionalP1 = productRepository.findById(1)
        assertNotNull(optionalP1)

        val p1 = optionalP1.get()

        assertEquals(1, p1.id)
        assertEquals("A", p1.brand)
        assertEquals(Category.TOP, p1.category)
        assertEquals(11200, p1.price)

        val optionalP20 = productRepository.findById(20)
        assertNotNull(optionalP20)

        val p20 = optionalP20.get()

        assertEquals(20, p20.id)
        assertEquals("C", p20.brand)
        assertEquals(Category.SNEAKERS, p20.category)
        assertEquals(9200, p20.price)

        // -------------------------------
        // findAllMinPriceProductByCategory 메소드 테스트
        // -------------------------------
        val minProducts = productRepository.findAllMinPriceProductByCategory(Category.TOP)
        assertNotNull(minProducts)
        assertEquals(1, minProducts.size)

        // newP1 으로 나오는지 확인
        assertEquals(Category.TOP, minProducts[0].category)
        assertEquals(newP1.brand, minProducts[0].brand)
        assertEquals(newP1.price, minProducts[0].price)

        // -------------------------------
        // findAllMaxPriceProductByCategory 메소드 테스트
        // -------------------------------
        val maxProducts = productRepository.findAllMaxPriceProductByCategory(Category.TOP)
        assertNotNull(maxProducts)
        assertEquals(1, maxProducts.size)

        // newP2 으로 나오는지 확인
        assertEquals(Category.TOP, maxProducts[0].category)
        assertEquals(newP2.brand, maxProducts[0].brand)
        assertEquals(newP2.price, maxProducts[0].price)

        // -------------------------------
        // ProductSpecification 테스트
        // -------------------------------
        val specification = ProductSpecification.withFilters(null, "C", null)
        val searchedProducts = productRepository.findAll(specification).toList()
        assertNotNull(searchedProducts)
        assertEquals(9, searchedProducts.size) // 한개 추가됨
    }
}