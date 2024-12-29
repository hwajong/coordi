package com.leo.coordi.controller

import com.leo.coordi.repository.Category
import com.leo.coordi.service.*
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/product")
@Validated
class ProductController(
    private val productService: ProductService
) {
    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)

    // API #1: 최소 금액 모든 카테고리 상품 코디
    @GetMapping("/min-price-coordi")
    fun getMinPriceCoordi(): MinPriceCoordiResponse {
        return productService.getMinPriceCoordi()
    }

    // API #2: 단일 브랜드 최소 금액 모든 카테고리 상품 코디
    @GetMapping("/min-price-one-brand-coordi")
    fun getMinPriceOneBrandCoordi(): MinPriceOneBrandCoordiResponse {
        return productService.getMinPriceOneBrandCoordi()
    }

    // API #3: 카테고리별 최소, 최대 상품 정보 조회
    @GetMapping("/min-max-price-products/{category}")
    fun getCategoryMinMaxPriceProducts(@PathVariable category: String): CategoryMinMaxPriceProductResponse {
        require(Category.isValidCategoryString(category)) { "category: $category" }
        return productService.getCategoryMinMaxPriceProducts(Category.valueOf(category))
    }

    // API #4-0: 상품 검색
    @GetMapping("/search")
    fun searchProducts(
        @RequestParam(required = false) id: Long?,
        @RequestParam(required = false) @Size(max = 5, message = "브랜드는 5자 이하여야 합니다.") brand: String?,
        @RequestParam(required = false) category: String?,
    ): SearchProductsResponse {
        return productService.searchProducts(id, brand, category);
    }

    // API #4-1: 상품 다건 추가
    @PostMapping("/add")
    fun addProducts(@RequestBody @Valid request: AddProductsRequest): AddProductsResponse {
        log.debug("request: {}", request)
        return productService.addProducts(request)
    }

    // API #4-2: 상품 다건 업데이트 
    @PostMapping("/update")
    fun updateProducts(@RequestBody @Valid request: UpdateProductsRequest): UpdateProductsResponse {
        log.debug("request: {}", request)
        return productService.updateProducts(request)
    }

    // API #4-3: 상품 단건 삭제 
    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable @Positive(message = "id는 0보다 커야합니다.") id: Long): DeletedProductResponse {
        return productService.deleteProduct(id)
    }

}





