package com.leo.coordi.service

import com.leo.coordi.repository.Product
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class ErrorResponse(
    val errorCode: Int,
    val reason: String
)

// -------

data class BrandPriceResponse(
    val brand: String,
    val price: Int,
)

data class CoordiProduct(
    val category: String,
    val categoryDisplayName: String,
    val brand: String,
    val price: Int,
)

data class ProductResponse(
    val id: Long,
    val category: String,
    val categoryDisplayName: String,
    val brand: String,
    val price: Int,
)

data class SearchProductsResponse(
    val searchedProducts: List<ProductResponse>,
    val searchedProductsCount: Int, 
)

data class MinPriceCoordiResponse(
    val coordiProducts: List<CoordiProduct>,
    val coordiProductsCount: Int,
    val totalPrice: Int,
)

data class MinPriceOneBrandCoordiResponse(
    val brand: String,
    val coordiProducts: List<CoordiProduct>,
    val coordiProductsCount: Int,
    val totalPrice: Int,
)

data class CategoryMinMaxPriceProductResponse(
    val category: String,
    val categoryDisplayName: String,
    val minPriceProducts: List<BrandPriceResponse>,
    val maxPriceProducts: List<BrandPriceResponse>,
)

// -------

data class AddProductRequest(
    @field:Size(min = 1, max = 5, message = "브랜드는 5글자 이내여야 합니다.")
    val brand: String,

    @field:Size(min = 1, max = 30, message = "카테고리는 30글자 이내여야 합니다.")
    val category: String,

    @field:Positive(message = "가격은 0보다 커야합니다.")
    val price: Int,
)

data class AddProductsRequest(
    @field:Valid
    val products: List<AddProductRequest>
)

data class AddProductsResponse(
    val addedProducts: List<ProductResponse>,
    val addedProductsCount: Int
)

// -------

data class UpdateProductRequest(
    @field:Positive(message = "id는 0보다 커야합니다.")
    val id: Long,
    
    @field:Size(min = 1, max = 5, message = "브랜드는 5글자 이내여야 합니다.")
    val brand: String?,

    @field:Size(min = 1, max = 30, message = "카테고리는 30글자 이내여야 합니다.")
    val category: String?,

    @field:Positive(message = "가격은 0보다 커야합니다.")
    val price: Int?,
)

data class UpdateProductsRequest(
    @field:Valid
    val products: List<UpdateProductRequest>
)

data class UpdateProductsResponse(
    val updatedProducts: List<ProductResponse>,
    val updatedProductsCount: Int
)

data class DeletedProductResponse(
    val deletedProduct: ProductResponse,
)

// -----------------------------------------------------
// -- 이하 DTO 변환 함수 

fun Product.toBrandPriceResponse() = BrandPriceResponse(this.brand, this.price)

fun Product.toProductResponse(): ProductResponse {
    check(this.id != null) { "id 가 없는 Product 엔티티는 ProductResponse 로 변환할 수 없습니다." }
    return ProductResponse(this.id!!, this.category.name, this.category.displayName, this.brand, this.price)
}

