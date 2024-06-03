package com.example.buggy

import com.example.buggy.data.appdata.Allergies
import org.junit.Assert.assertEquals
import org.junit.Test

class AllergiesTest {

    @Test
    fun testGetAllergiesString() {
        val allergies = BooleanArray(10)
        allergies[0] = true // Set Dairy allergy
        allergies[4] = true // Set Peanut allergy

        Allergies.setAllergy(allergies)

        val expectedString = "Dairy, Peanut"
        val actualString = Allergies.getAllergiesString()

        assertEquals(expectedString, actualString)
    }
}
