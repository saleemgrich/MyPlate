package com.example.buggy

import com.example.buggy.data.appdata.DietaryRestrictions
import org.junit.Assert.assertEquals
import org.junit.Test

class DietaryRestrictionsTest {

    @Test
    fun testGetRestrictionsString() {
        val restrictions = BooleanArray(9)
        restrictions[2] = true // Set Vegetarian restriction
        restrictions[3] = true // Set Vegan restriction
        restrictions[7] = true // Set Paleo restriction

        DietaryRestrictions.setRestriction(restrictions)

        val expectedString = "Vegetarian, Vegan, Paleo"
        val actualString = DietaryRestrictions.getRestrictionsString()

        assertEquals(expectedString, actualString)
    }
}
