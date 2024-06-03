package com.example.buggy.api

import com.example.buggy.data.model.RecipeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularApi {
    @GET("recipes/complexSearch")
    fun getRecipes(
        @Query("query") query: String?,
        @Query("number") number: Int,
        @Query("cuisine") cuisine: String?,
        @Query("diet") diet: String?,
        @Query("intolerances") intolerances: String?,
        @Query("includeIngredients") includeIngredients: String?,
//        @Query("excludeIngredients") excludeIngredients: String?, not yet implemented
        @Query("addRecipeInformation") addRecipeInformation: Boolean?,
        @Query("addRecipeInstructions") addRecipeInstructions: Boolean?,
//        @Query("addRecipeNutrition") addRecipeNutrition: Boolean?,
//        @Query("author") author: String?,
        @Query("maxReadyTime") maxReadyTime: Int?,
//        @Query("ignorePantry") ignorePantry: Boolean?,
    ): Call<RecipeResponse?>?
}
