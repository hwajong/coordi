package com.leo.coordi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.leo.coordi.service.AddProductRequest
import com.leo.coordi.service.AddProductsRequest
import com.leo.coordi.service.UpdateProductRequest
import com.leo.coordi.service.UpdateProductsRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

// API 통합테스트 
@SpringBootTest
@AutoConfigureMockMvc
@Suppress("NonAsciiCharacters")
@Transactional
class ProductControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `API #1 최소 금액 모든 카테고리 상품 코디`() {
        mockMvc.perform(get("/api/product/min-price-coordi"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.coordiProductsCount").value(8))
            .andExpect(jsonPath("$.totalPrice").value(34100))
            // coordiProducts[0] 확인
            .andExpect(jsonPath("$.coordiProducts[0].category").value("TOP"))
            .andExpect(jsonPath("$.coordiProducts[0].categoryDisplayName").value("상의"))
            .andExpect(jsonPath("$.coordiProducts[0].brand").value("C"))
            .andExpect(jsonPath("$.coordiProducts[0].price").value(10000))
            // coordiProducts[1] 확인
            .andExpect(jsonPath("$.coordiProducts[1].category").value("OUTER"))
            .andExpect(jsonPath("$.coordiProducts[1].categoryDisplayName").value("아우터"))
            .andExpect(jsonPath("$.coordiProducts[1].brand").value("E"))
            .andExpect(jsonPath("$.coordiProducts[1].price").value(5000))
            // coordiProducts[7] 확인
            .andExpect(jsonPath("$.coordiProducts[7].category").value("ACCESSORY"))
            .andExpect(jsonPath("$.coordiProducts[7].categoryDisplayName").value("액세서리"))
            .andExpect(jsonPath("$.coordiProducts[7].brand").value("F"))
            .andExpect(jsonPath("$.coordiProducts[7].price").value(1900))
    }

    @Test
    fun `API #2 단일 브랜드 최소 금액 모든 카테고리 상품 코디`() {
        mockMvc.perform(get("/api/product/min-price-one-brand-coordi"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.brand").value("D"))
            .andExpect(jsonPath("$.coordiProductsCount").value(8))
            .andExpect(jsonPath("$.totalPrice").value(36100))
            // coordiProducts[0] 확인
            .andExpect(jsonPath("$.coordiProducts[0].category").value("TOP"))
            .andExpect(jsonPath("$.coordiProducts[0].categoryDisplayName").value("상의"))
            .andExpect(jsonPath("$.coordiProducts[0].brand").value("D"))
            .andExpect(jsonPath("$.coordiProducts[0].price").value(10100))
            // coordiProducts[1] 확인
            .andExpect(jsonPath("$.coordiProducts[1].category").value("OUTER"))
            .andExpect(jsonPath("$.coordiProducts[1].categoryDisplayName").value("아우터"))
            .andExpect(jsonPath("$.coordiProducts[1].brand").value("D"))
            .andExpect(jsonPath("$.coordiProducts[1].price").value(5100))
            // coordiProducts[7] 확인
            .andExpect(jsonPath("$.coordiProducts[7].category").value("ACCESSORY"))
            .andExpect(jsonPath("$.coordiProducts[7].categoryDisplayName").value("액세서리"))
            .andExpect(jsonPath("$.coordiProducts[7].brand").value("D"))
            .andExpect(jsonPath("$.coordiProducts[7].price").value(2000))
    }

    @Test
    fun `API #3 카테고리별 최소, 최대 상품 정보 조회`() {
        mockMvc.perform(get("/api/product/min-max-price-products/HAT"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.category").value("HAT"))
            .andExpect(jsonPath("$.categoryDisplayName").value("모자"))
            // minPriceProducts[0] 확인
            .andExpect(jsonPath("$.minPriceProducts[0].brand").value("D"))
            .andExpect(jsonPath("$.minPriceProducts[0].price").value("1500"))
            // maxPriceProducts[0] 확인
            .andExpect(jsonPath("$.maxPriceProducts[0].brand").value("B"))
            .andExpect(jsonPath("$.maxPriceProducts[0].price").value("2000"))
    }

    @Test
    fun `API #4-0 상품 검색`() {
        mockMvc.perform(get("/api/product/search?brand=A"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.searchedProductsCount").value(8))
            // searchedProducts[0] 확인
            .andExpect(jsonPath("$.searchedProducts[0].id").value(1))
            .andExpect(jsonPath("$.searchedProducts[0].category").value("TOP"))
            .andExpect(jsonPath("$.searchedProducts[0].categoryDisplayName").value("상의"))
            .andExpect(jsonPath("$.searchedProducts[0].brand").value("A"))
            .andExpect(jsonPath("$.searchedProducts[0].price").value("11200"))
            // searchedProducts[7] 확인
            .andExpect(jsonPath("$.searchedProducts[7].id").value(8))
            .andExpect(jsonPath("$.searchedProducts[7].category").value("ACCESSORY"))
            .andExpect(jsonPath("$.searchedProducts[7].categoryDisplayName").value("액세서리"))
            .andExpect(jsonPath("$.searchedProducts[7].brand").value("A"))
            .andExpect(jsonPath("$.searchedProducts[7].price").value("2300"))
    }

    @Test
    fun `API #4-1 상품 다건 추가`() {
        val addProductsRequest = AddProductsRequest(
            listOf(
                AddProductRequest("A", "TOP", 500),
                AddProductRequest("A", "TOP", 50),
            )
        )
        val requestJson = objectMapper.writeValueAsString(addProductsRequest)

        mockMvc.perform(
            post("/api/product/add")
                .contentType("application/json")
                .content(requestJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.addedProductsCount").value(2))
            // addedProducts[0] 확인
            .andExpect(jsonPath("$.addedProducts[0].category").value("TOP"))
            .andExpect(jsonPath("$.addedProducts[0].categoryDisplayName").value("상의"))
            .andExpect(jsonPath("$.addedProducts[0].brand").value("A"))
            .andExpect(jsonPath("$.addedProducts[0].price").value("500"))
            // addedProducts[1] 확인
            .andExpect(jsonPath("$.addedProducts[1].category").value("TOP"))
            .andExpect(jsonPath("$.addedProducts[1].categoryDisplayName").value("상의"))
            .andExpect(jsonPath("$.addedProducts[1].brand").value("A"))
            .andExpect(jsonPath("$.addedProducts[1].price").value("50"))
    }

    @Test
    fun `API #4-2 상품 다건 업데이트`() {
        val updateProductsRequest = UpdateProductsRequest(
            listOf(
                UpdateProductRequest(1, null, null, 10),
                UpdateProductRequest(2, null, null, 30),
            )
        )
        val requestJson = objectMapper.writeValueAsString(updateProductsRequest)

        mockMvc.perform(
            post("/api/product/update")
                .contentType("application/json")
                .content(requestJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.updatedProductsCount").value(2))
            // updatedProducts[0] 확인
            .andExpect(jsonPath("$.updatedProducts[0].id").value(1))
            .andExpect(jsonPath("$.updatedProducts[0].category").value("TOP"))
            .andExpect(jsonPath("$.updatedProducts[0].categoryDisplayName").value("상의"))
            .andExpect(jsonPath("$.updatedProducts[0].brand").value("A"))
            .andExpect(jsonPath("$.updatedProducts[0].price").value("10"))
            // updatedProducts[1] 확인
            .andExpect(jsonPath("$.updatedProducts[1].id").value(2))
            .andExpect(jsonPath("$.updatedProducts[1].category").value("OUTER"))
            .andExpect(jsonPath("$.updatedProducts[1].categoryDisplayName").value("아우터"))
            .andExpect(jsonPath("$.updatedProducts[1].brand").value("A"))
            .andExpect(jsonPath("$.updatedProducts[1].price").value("30"))
    }

    @Test
    fun `API #4-3 상품 상품 단건 삭제`() {
        mockMvc.perform(delete("/api/product/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.deletedProduct.id").value(1))
            .andExpect(jsonPath("$.deletedProduct.category").value("TOP"))
            .andExpect(jsonPath("$.deletedProduct.categoryDisplayName").value("상의"))
            .andExpect(jsonPath("$.deletedProduct.brand").value("A"))
            .andExpect(jsonPath("$.deletedProduct.price").value(11200))
    }

}