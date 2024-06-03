package com.example.buggy.data.model

/*
Data class for FoodItem object
 */

data class FoodItem(
    val imageResId: Int,
    val name: String,
    ) {
    constructor() : this(0, "")
}