package com.leo.coordi.repository

import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification

object ProductSpecification {
    // for 상품 검색
    fun withFilters(id: Long?, brand: String?, category: String?): Specification<Product> {
        category?.let {
            if (it.isNotBlank()) {
                require(Category.isValidCategoryString(category)) { "[${category}] 존재하지 않는 카테고리" }
            }
        }
        
        return Specification { root, _, criteriaBuilder ->
            val predicates: MutableList<Predicate> = mutableListOf()

            id?.let {
                predicates.add(criteriaBuilder.equal(root.get<Long>("id"), it))
            }
            brand?.let {
                if (it.isNotBlank()) {
                    predicates.add(criteriaBuilder.equal(root.get<String>("brand"), it))    
                }
            }
            category?.let {
                if (it.isNotBlank()) {
                    predicates.add(criteriaBuilder.equal(root.get<Category>("category"), it))    
                }
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }
}
