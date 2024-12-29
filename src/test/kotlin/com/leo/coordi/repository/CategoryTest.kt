package com.leo.coordi.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class CategoryTest {
    
    @Test
    fun `isValidCategoryString 메소드 테스트`() {
        assertTrue(Category.isValidCategoryString("TOP"))
        assertTrue(Category.isValidCategoryString("OUTER"))
        assertTrue(Category.isValidCategoryString("PANTS"))
        assertTrue(Category.isValidCategoryString("SNEAKERS"))
        assertTrue(Category.isValidCategoryString("BAG"))
        assertTrue(Category.isValidCategoryString("HAT"))
        assertTrue(Category.isValidCategoryString("SOCKS"))
        assertTrue(Category.isValidCategoryString("ACCESSORY"))

        assertFalse(Category.isValidCategoryString("top"))
        assertFalse(Category.isValidCategoryString("outer"))
        assertFalse(Category.isValidCategoryString("pants"))
        assertFalse(Category.isValidCategoryString("sneakers"))
        assertFalse(Category.isValidCategoryString("bag"))
        assertFalse(Category.isValidCategoryString("hat"))
        assertFalse(Category.isValidCategoryString("socks"))
        assertFalse(Category.isValidCategoryString("accessory"))
        
        assertFalse(Category.isValidCategoryString("AAAAAAAAA"))        
    }
}