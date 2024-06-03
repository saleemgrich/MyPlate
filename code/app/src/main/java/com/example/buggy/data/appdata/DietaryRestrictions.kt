package com.example.buggy.data.appdata

object DietaryRestrictions {
    private val restrictionList = BooleanArray(9)

    fun setRestriction(restrictions: BooleanArray) {
        for (i in 0 until minOf(restrictions.size, restrictionList.size)) {
            restrictionList[i] = restrictions[i]
        }
    }

    val restrictionsMap = mapOf(
        "Halal" to 0,
        "Kosher" to 1,
        "Vegetarian" to 2,
        "Vegan" to 3,
        "Ketogenic" to 4,
        "Low Carb" to 5,
        "Pescetarian" to 6,
        "Paleo" to 7,
        "Low Sodium" to 8
    )

    fun getRestrictionsString(): String {
        val selectedRestrictions = mutableListOf<String>()
        for ((restrictionName, index) in restrictionsMap) {
            if (restrictionList.getOrElse(index) { false }) {
                selectedRestrictions.add(restrictionName)
            }
        }
        return selectedRestrictions.joinToString(", ")
    }
}