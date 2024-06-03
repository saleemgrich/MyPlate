package com.example.buggy.data.appdata

import com.example.buggy.R
import com.example.buggy.data.model.FoodItem

object FoodItemList {
    val foodItems = listOf(
        FoodItem(R.drawable.milk, "Milk"),
        FoodItem(R.drawable.rice, "Rice"),
        FoodItem(R.drawable.pasta, "Pasta"),
        FoodItem(R.drawable.bread, "Bread"),
        // Add more food items
    )
}