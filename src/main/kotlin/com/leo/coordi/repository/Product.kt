package com.leo.coordi.repository

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
data class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var brand: String,

    @Enumerated(EnumType.STRING)
    var category: Category,

    var price: Int,

    @CreationTimestamp
    @Column(name = "insert_dt", updatable = false)
    val insertDt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "update_dt")
    val updateDt: LocalDateTime? = null
)

enum class Category(val displayName: String) {
    TOP("상의"),
    OUTER("아우터"),
    PANTS("바지"),
    SNEAKERS("스니커즈"),
    BAG("가방"),
    HAT("모자"),
    SOCKS("양말"),
    ACCESSORY("액세서리");
    
    companion object {
        fun isValidCategoryString(category: String): Boolean {
            return entries.any { it.name == category }
        }
    }
}