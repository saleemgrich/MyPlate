package com.example.buggy.data.appdata

import com.example.buggy.data.model.Recipe

object SavedRecipes {
    var recipes = mutableListOf<Recipe>()
    var suggested_recipes = mutableListOf<Recipe>()
}