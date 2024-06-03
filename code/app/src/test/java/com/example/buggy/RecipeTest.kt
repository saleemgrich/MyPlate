package com.example.buggy

import com.example.buggy.data.model.Recipe
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeTest {

    @Test
    fun testToMap() {
        // Make a Recipe object
        val recipe = Recipe().apply {
            id = "123"
            title = "Test Recipe"
            imageUrl = "https://example.com/image.jpg"
            instructions = "Test instructions"
        }

        // Call toMap() function
        val map = recipe.toMap()

        // Verify the correctness of the returned map
        assertEquals("123", map["id"])
        assertEquals("Test Recipe", map["title"])
        assertEquals("https://example.com/image.jpg", map["imageUrl"])
        assertEquals("Test instructions", map["instructions"])
    }
}