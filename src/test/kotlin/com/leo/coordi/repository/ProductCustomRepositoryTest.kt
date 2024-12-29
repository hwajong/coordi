package com.leo.coordi.repository

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

// 실제 DB 쿼리 실행을 통한 Repository 테스트
// JdbcTemplate 을 주입 받아야 하기 때문에 SpringBootTest 을 사용함
@SpringBootTest
@Suppress("NonAsciiCharacters")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Transactional
class ProductCustomRepositoryTest @Autowired constructor(
    private val productCustomRepository: ProductCustomRepository,
    private val productRepository: ProductRepository,
) {
    @Test
    @Order(1)
    fun `H2 DB 초기화 직후 테스트`() {
        // -------------------------------
        // findMinPriceCoordi 메소드 테스트
        // -------------------------------
        val coordi1 = productCustomRepository.findMinPriceCoordi()

        // 결과 검증
        assertNotNull(coordi1)
        assertEquals(Category.entries.size, coordi1.size)

        assertEquals(Category.TOP.name, coordi1[0].category)
        assertEquals(Category.TOP.displayName, coordi1[0].categoryDisplayName)
        assertEquals("C", coordi1[0].brand)
        assertEquals(10000, coordi1[0].price)

        assertEquals(Category.OUTER.name, coordi1[1].category)
        assertEquals(Category.OUTER.displayName, coordi1[1].categoryDisplayName)
        assertEquals("E", coordi1[1].brand)
        assertEquals(5000, coordi1[1].price)

        assertEquals(Category.ACCESSORY.name, coordi1[7].category)
        assertEquals(Category.ACCESSORY.displayName, coordi1[7].categoryDisplayName)
        assertEquals("F", coordi1[7].brand)
        assertEquals(1900, coordi1[7].price)

        // -------------------------------
        // findMinPriceAllBrandCoordi 메소드 테스트
        // -------------------------------
        val coordi2 = productCustomRepository.findMinPriceAllBrandCoordi()

        // 결과 검증
        assertNotNull(coordi2)
        assertEquals(72, coordi2.size)

        assertEquals(Category.ACCESSORY.name, coordi2[0].category)
        assertEquals(Category.ACCESSORY.displayName, coordi2[0].categoryDisplayName)
        assertEquals("A", coordi2[0].brand)
        assertEquals(2300, coordi2[0].price)

        assertEquals(Category.BAG.name, coordi2[1].category)
        assertEquals(Category.BAG.displayName, coordi2[1].categoryDisplayName)
        assertEquals("A", coordi2[1].brand)
        assertEquals(2000, coordi2[1].price)

        assertEquals(Category.TOP.name, coordi2[71].category)
        assertEquals(Category.TOP.displayName, coordi2[71].categoryDisplayName)
        assertEquals("I", coordi2[71].brand)
        assertEquals(11400, coordi2[71].price)
    }

    @Test
    @Order(2)
    fun `최저가 상품 추가 등록 후 테스트`() {
        val p1 = Product(null, "C", Category.TOP, 9000)
        val p2 = Product(null, "A", Category.ACCESSORY, 300)
        productRepository.saveAllAndFlush(listOf(p1, p2))

        // -------------------------------
        // findMinPriceCoordi 메소드 테스트
        // -------------------------------
        val coordi1 = productCustomRepository.findMinPriceCoordi()

        // 결과 검증
        assertNotNull(coordi1)
        assertEquals(Category.entries.size, coordi1.size)

        // p1 로 바뀌었는지 확인
        assertEquals(p1.category.name, coordi1[0].category)
        assertEquals(p1.category.displayName, coordi1[0].categoryDisplayName)
        assertEquals(p1.brand, coordi1[0].brand)
        assertEquals(p1.price, coordi1[0].price)

        // p2 로 바뀌었는지 확인
        assertEquals(p2.category.name, coordi1[7].category)
        assertEquals(p2.category.displayName, coordi1[7].categoryDisplayName)
        assertEquals(p2.brand, coordi1[7].brand)
        assertEquals(p2.price, coordi1[7].price)

        // -------------------------------
        // findMinPriceAllBrandCoordi 메소드 테스트
        // -------------------------------
        val coordi2 = productCustomRepository.findMinPriceAllBrandCoordi()

        // 결과 검증
        assertNotNull(coordi2)
        assertEquals(72, coordi2.size)

        // p2 로 바뀌었는지 확인
        assertEquals(Category.ACCESSORY.name, coordi2[0].category)
        assertEquals(Category.ACCESSORY.displayName, coordi2[0].categoryDisplayName)
        assertEquals(p2.brand, coordi2[0].brand)
        assertEquals(p2.price, coordi2[0].price) 

        // 기존과 같은지 확인
        assertEquals(Category.BAG.name, coordi2[1].category)
        assertEquals(Category.BAG.displayName, coordi2[1].categoryDisplayName)
        assertEquals("A", coordi2[1].brand)
        assertEquals(2000, coordi2[1].price)

        // 기존과 같은지 확인
        assertEquals(Category.TOP.name, coordi2[71].category)
        assertEquals(Category.TOP.displayName, coordi2[71].categoryDisplayName)
        assertEquals("I", coordi2[71].brand)
        assertEquals(11400, coordi2[71].price)
    }


}