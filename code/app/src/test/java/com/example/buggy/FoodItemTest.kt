package com.example.buggy

import com.example.buggy.data.model.FoodItem
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Test

class FoodItemTest {

    @Test
    fun createFoodItem() {
        val imageID = R.drawable.bread
        val name = "Bread"

        val foodItem = FoodItem(imageID, name)

        assertNotNull(foodItem)
        assertEquals(imageID, foodItem.imageResId)
        assertEquals(name, foodItem.name)
    }
}