package com.leo.coordi.service

import com.leo.coordi.repository.Category
import com.leo.coordi.repository.Product
import com.leo.coordi.repository.ProductCustomRepository
import com.leo.coordi.repository.ProductRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
@Suppress("NonAsciiCharacters")
class ProductServiceTest {

    @Mock
    lateinit var productRepository: ProductRepository

    @Mock
    lateinit var productCustomRepository: ProductCustomRepository

    @InjectMocks
    lateinit var productService: ProductService

    @Test
    fun `getMinPriceCoordi 메소드 테스트`() {
        // Mock 설정
        val cp1 = CoordiProduct(Category.TOP.name, Category.TOP.displayName, "A", 10)
        val cp2 = CoordiProduct(Category.OUTER.name, Category.OUTER.displayName, "B", 20)
        val cp3 = CoordiProduct(Category.PANTS.name, Category.PANTS.displayName, "C", 30)
        val cp4 = CoordiProduct(Category.SNEAKERS.name, Category.SNEAKERS.displayName, "D", 40)
        val cp5 = CoordiProduct(Category.BAG.name, Category.BAG.displayName, "E", 50)
        val cp6 = CoordiProduct(Category.HAT.name, Category.HAT.displayName, "F", 60)
        val cp7 = CoordiProduct(Category.SOCKS.name, Category.SOCKS.displayName, "G", 70)
        val cp8 = CoordiProduct(Category.ACCESSORY.name, Category.ACCESSORY.displayName, "H", 80)
        
        val coordiProducts = listOf(cp1, cp2, cp3, cp4, cp5, cp6, cp7, cp8)
        whenever(productCustomRepository.findMinPriceCoordi()).thenReturn(coordiProducts)

        // 테스트 메소드 호출
        val result = productService.getMinPriceCoordi()

        // 결과 검증
        assertNotNull(result)
        assertEquals(8, result.coordiProductsCount)
        assertEquals(360, result.totalPrice)

        assertEquals(cp1, result.coordiProducts[0])
        assertEquals(cp2, result.coordiProducts[1])
        assertEquals(cp3, result.coordiProducts[2])
        assertEquals(cp4, result.coordiProducts[3])
        assertEquals(cp5, result.coordiProducts[4])
        assertEquals(cp6, result.coordiProducts[5])
        assertEquals(cp7, result.coordiProducts[6])
        assertEquals(cp8, result.coordiProducts[7])
    }

    @Test
    fun `getMinPriceOneBrandCoordi 메소드 테스트`() {
        // Mock 설정
        val cp1 = CoordiProduct(Category.TOP.name, Category.TOP.displayName, "AA", 10)
        val cp2 = CoordiProduct(Category.OUTER.name, Category.OUTER.displayName, "AA", 10)
        val cp3 = CoordiProduct(Category.PANTS.name, Category.PANTS.displayName, "AA", 10)
        val cp4 = CoordiProduct(Category.SNEAKERS.name, Category.SNEAKERS.displayName, "AA", 10)
        val cp5 = CoordiProduct(Category.BAG.name, Category.BAG.displayName, "AA", 10)
        val cp6 = CoordiProduct(Category.HAT.name, Category.HAT.displayName, "AA", 10)
        val cp7 = CoordiProduct(Category.SOCKS.name, Category.SOCKS.displayName, "AA", 10)
        val cp8 = CoordiProduct(Category.ACCESSORY.name, Category.ACCESSORY.displayName, "AA", 10)

        val coordiProducts = listOf(cp1, cp2, cp3, cp4, cp5, cp6, cp7, cp8)
        whenever(productCustomRepository.findMinPriceAllBrandCoordi()).thenReturn(coordiProducts)

        // 테스트 메소드 호출
        val result = productService.getMinPriceOneBrandCoordi()

        // 결과 검증
        assertNotNull(result)
        assertEquals("AA", result.brand)
        assertNotNull(result.coordiProducts)
        assertEquals(8, result.coordiProductsCount)
        assertEquals(80, result.totalPrice)

        assertEquals(cp1, result.coordiProducts[0])
        assertEquals(cp2, result.coordiProducts[1])
        assertEquals(cp3, result.coordiProducts[2])
        assertEquals(cp4, result.coordiProducts[3])
        assertEquals(cp5, result.coordiProducts[4])
        assertEquals(cp6, result.coordiProducts[5])
        assertEquals(cp7, result.coordiProducts[6])
        assertEquals(cp8, result.coordiProducts[7])
    }

    @Test
    fun `getCategoryMinMaxPriceProducts 메소드 테스트`() {
        // Mock 설정
        val minPriceProduct = listOf(
            Product(1, "LG", Category.TOP, 5000),
            Product(7, "CU", Category.TOP, 5000),
        )
        whenever(productRepository.findAllMinPriceProductByCategory(any())).thenReturn(minPriceProduct)

        val maxPriceProduct = listOf(
            Product(10, "LG", Category.TOP, 50000),
            Product(70, "CU", Category.TOP, 50000),
        )
        whenever(productRepository.findAllMaxPriceProductByCategory(any())).thenReturn(maxPriceProduct)

        // 테스트 메소드 호출
        val result = productService.getCategoryMinMaxPriceProducts(Category.TOP)

        // 결과 검증
        assertNotNull(result)
        assertEquals(Category.TOP.name, result.category)
        assertEquals(Category.TOP.displayName, result.categoryDisplayName)
        assertEquals(minPriceProduct.map { it.toBrandPriceResponse() }, result.minPriceProducts)
        assertEquals(maxPriceProduct.map { it.toBrandPriceResponse() }, result.maxPriceProducts)
    }

}