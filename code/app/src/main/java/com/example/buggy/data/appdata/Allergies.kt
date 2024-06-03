package com.example.buggy.data.appdata

object Allergies {
    private val allergyList = BooleanArray(10)

    fun setAllergy(allergies: BooleanArray) {
        for (i in 0 until minOf(allergies.size, allergyList.size)) {
            allergyList[i] = allergies[i]
        }
    }

    val allergiesMap = mapOf(
        "Dairy" to 0,
        "Egg" to 1,
        "Gluten" to 2,
        "Grain" to 3,
        "Peanut" to 4,
        "Seafood" to 5,
        "Sesame" to 6,
        "Shellfish" to 7,
        "Soy" to 8,
        "Tree Nut" to 9
    )

    fun getAllergiesString(): String {
        val selectedAllergies = mutableListOf<String>()
        for ((allergyName, index) in allergiesMap) {
            if (allergyList.getOrElse(index) { false }) {
                selectedAllergies.add(allergyName)
            }
        }
        return selectedAllergies.joinToString(", ")
    }
}