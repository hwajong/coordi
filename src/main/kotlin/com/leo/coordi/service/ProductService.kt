package com.leo.coordi.service

import com.leo.coordi.repository.*
import jakarta.persistence.EntityNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val productCustomRepository: ProductCustomRepository,
) {
    private val log: Logger = LoggerFactory.getLogger(ProductService::class.java)

    companion object {
        const val MIN_PRICE_COORDI_CACHE = "minPriceCoordiCache"
        const val MIN_PRICE_ONE_BRAND_COORDI_CACHE = "minPriceOneBrandCoordiCache"
        const val CATEGORY_MIN_MAX_PRICE_PRODUCTS_CACHE = "categoryMinMaxPriceProductsCache"
        const val SEARCH_PRODUCTS_CACHE = "searchProductsCache"
    }

    // 모든 카테고리 최저가격 상품들로 구성한 코디 정보 리턴 
    @Transactional(readOnly = true)
    @Cacheable(MIN_PRICE_COORDI_CACHE)
    fun getMinPriceCoordi(): MinPriceCoordiResponse {
        val coordiProducts = productCustomRepository.findMinPriceCoordi()
        check(coordiProducts.isNotEmpty())

        val totalPrice = coordiProducts.sumOf { it.price }

        check(coordiProducts.size == Category.entries.size) { "모든 카테고리의 코디가 정확하게 조회되지 않았습니다." }
        check(totalPrice > 0) { "총액이 0 보다 크지 않습니다." }

        return MinPriceCoordiResponse(coordiProducts, coordiProducts.size, totalPrice)
    }

    // 단일 브랜드로 모든 카테고리 상품을 구매할 경우 최저가 정보 리턴 
    @Transactional(readOnly = true)
    @Cacheable(MIN_PRICE_ONE_BRAND_COORDI_CACHE)
    fun getMinPriceOneBrandCoordi(): MinPriceOneBrandCoordiResponse {

        // 모든 브랜드의 카테고리별 최저 상품 조회 
        val coordiProducts = productCustomRepository.findMinPriceAllBrandCoordi()
        check(coordiProducts.isNotEmpty())

        // 브랜드별 그룹핑
        val coordiProductsGroupByBrand = coordiProducts.groupBy { it.brand }

        // 카테고리별 모든 상품이 있는 브랜드만 필터링
        val validCoordiProductsGroupByBrand =
            coordiProductsGroupByBrand.filter { it.value.size == Category.entries.size }

        // 브랜드 별로 모든 카테고리 상품의 총합 계산
        val totalPriceByBrand =
            validCoordiProductsGroupByBrand.mapValues { products -> products.value.sumOf { it.price } }

        // 최저가 브랜드 정보 
        val minPriceBrand = totalPriceByBrand.minBy { it.value }

        // 최저가 브랜드로 구성한 코디 
        val minPriceBrandCoordi = coordiProducts.filter { it.brand == minPriceBrand.key }.toMutableList()

        // 카테고리 정의된 순서로 소팅
        minPriceBrandCoordi.sortBy { Category.valueOf(it.category).ordinal }

        log.debug("브랜드 별로 모든 카테고리 상품의 총합: {}", totalPriceByBrand)
        log.debug("최저가 브랜드 정보: {}", minPriceBrand)
        log.debug("최저가 브랜드로 구성한 코디: {}", minPriceBrandCoordi)

        check(minPriceBrandCoordi.size == Category.entries.size) { "모든 카테고리의 코디가 정확하게 조회되지 않았습니다." }
        check(minPriceBrand.value > 0) { "총액이 0 보다 크지 않습니다." }

        return MinPriceOneBrandCoordiResponse(
            minPriceBrand.key,
            minPriceBrandCoordi,
            minPriceBrandCoordi.size,
            minPriceBrand.value
        )
    }

    // 해당 카테고리의 최저, 최고 가격 브랜드와 상품 가격 리턴
    @Transactional(readOnly = true)
    @Cacheable(CATEGORY_MIN_MAX_PRICE_PRODUCTS_CACHE, key = "#category.name")
    fun getCategoryMinMaxPriceProducts(category: Category): CategoryMinMaxPriceProductResponse {
        // 카테고리의 최저가 상품들 조회
        val minPriceProduct = productRepository.findAllMinPriceProductByCategory(category)

        // 카테고리의 최고가 상품들 조회
        val maxPriceProduct = productRepository.findAllMaxPriceProductByCategory(category)

        log.debug("카테고리의 최저가 상품들 조회: {}", minPriceProduct)
        log.debug("카테고리의 최고가 상품들 조회: {}", maxPriceProduct)

        return CategoryMinMaxPriceProductResponse(
            category.name,
            category.displayName,
            minPriceProduct.map { it.toBrandPriceResponse() },
            maxPriceProduct.map { it.toBrandPriceResponse() }
        )
    }

    // 상품 검색
    @Transactional(readOnly = true)
    @Cacheable(SEARCH_PRODUCTS_CACHE, key = "#id + ':' + #brand + ':' + #category")
    fun searchProducts(id: Long?, brand: String?, category: String?): SearchProductsResponse {
        val specification = ProductSpecification.withFilters(id, brand, category)
        val products = productRepository.findAll(specification).toList()
        return SearchProductsResponse(
            products.map { it.toProductResponse() },
            products.size,
        )
    }

    // 상품 추가
    @Transactional
    @CacheEvict(
        value = [
            MIN_PRICE_COORDI_CACHE,
            MIN_PRICE_ONE_BRAND_COORDI_CACHE,
            CATEGORY_MIN_MAX_PRICE_PRODUCTS_CACHE,
            SEARCH_PRODUCTS_CACHE],
        allEntries = true
    )
    fun addProducts(request: AddProductsRequest): AddProductsResponse {
        val newProducts = request.products.map {
            require(Category.isValidCategoryString(it.category)) { "[${it.category}] 카테고리 값이 올바르지 않습니다." }
            Product(null, it.brand, Category.valueOf(it.category), it.price)
        }
        productRepository.saveAllAndFlush(newProducts)

        val addedProducts = newProducts.map { it.toProductResponse() }
        val ret = AddProductsResponse(addedProducts, addedProducts.size)
        log.info("상품 추가 완료: {}", ret)
        return ret
    }

    // 상품 수정
    @Transactional
    @CacheEvict(
        value = [
            MIN_PRICE_COORDI_CACHE,
            MIN_PRICE_ONE_BRAND_COORDI_CACHE,
            CATEGORY_MIN_MAX_PRICE_PRODUCTS_CACHE,
            SEARCH_PRODUCTS_CACHE],
        allEntries = true
    )    
    fun updateProducts(request: UpdateProductsRequest): UpdateProductsResponse {

        val updatedProducts = mutableListOf<ProductResponse>()

        for (p in request.products) {
            val entity = productRepository.findById(p.id)
                .orElseThrow { throw EntityNotFoundException("[id=${p.id}] 상품 데이터가 존재하지 않습니다.") }

            var updated = false

            // 브랜드가 다르면 업데이트 
            p.brand?.let {
                if (entity.brand != p.brand) {
                    entity.brand = p.brand
                    updated = true
                }
            }

            // 카테고리가 다르면 업데이트
            p.category?.let {
                require(Category.isValidCategoryString(it)) { "[${it}] 존재하지 않는 카테고리" }
                val newCategory = Category.valueOf(it)
                if (entity.category != newCategory) {
                    entity.category = newCategory
                    updated = true
                }
            }

            // 가격이 다르면 업데이트
            p.price?.let {
                if (entity.price != p.price) {
                    entity.price = p.price
                    updated = true
                }
            }

            if (updated) {
                updatedProducts.add(entity.toProductResponse())
            }
        }

        if (updatedProducts.isNotEmpty()) {
            productRepository.flush()
        }

        return UpdateProductsResponse(
            updatedProducts,
            updatedProducts.size
        )
    }

    // 상품 삭제
    @Transactional
    @CacheEvict(
        value = [
            MIN_PRICE_COORDI_CACHE,
            MIN_PRICE_ONE_BRAND_COORDI_CACHE,
            CATEGORY_MIN_MAX_PRICE_PRODUCTS_CACHE,
            SEARCH_PRODUCTS_CACHE],
        allEntries = true
    )
    fun deleteProduct(id: Long): DeletedProductResponse {
        val entity = productRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("[id=${id}] 상품 데이터가 존재하지 않습니다.") }

        productRepository.delete(entity)
        productRepository.flush()

        return DeletedProductResponse(entity.toProductResponse())
    }

}