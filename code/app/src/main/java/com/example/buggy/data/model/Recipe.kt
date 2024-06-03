package com.example.buggy.data.model

import android.os.Parcel
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/*
Data class for Recipes
 */
class Recipe {
    @SerializedName("id") var id: String? = null
    @SerializedName("title") var title: String? = null
    @SerializedName("image") var imageUrl: String? = null
    @SerializedName("analyzedInstructions") var analyzedInstructions: List<AnalyzedInstruction>? = null
    var instructions: String? = null
    // ... getters and setters ...

    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object{
        fun fromJson(json: String): Recipe {
            return Gson().fromJson(json, Recipe::class.java)
        }
    }
    fun toMap(): Map<String, String?> {
        return mapOf(
            "id" to id,
            "title" to title,
            "imageUrl" to imageUrl,
            "instructions" to instructions
        )
    }
}
//@Parcelize
//data class Recipe(
//    @SerializedName("recipeID")
//    val recipeID: Int,
//    // id in spoonacular
//    @SerializedName("originalID")
//    val originalID: Int?,
//    @SerializedName("title")
//    val title: String,
//    @SerializedName("image")
//    val image: String?,
//    @SerializedName("servings")
//    val servings: Int,
//    @SerializedName("readyInMinutes")
//    val readyInMinutes: Int,
//    @SerializedName("maxReadyTime")
//    val maxReadyTime: Int,
//    // refers to dietary restrictions e.g. vegetarian, gluten-free, etc.
//    @SerializedName("diet")
//    val diet: ArrayList<String>,
//    @SerializedName("cuisine")
//    val cuisine: ArrayList<String>,
//    // refers to allergies and personal preference
//    @SerializedName("intolerances")
//    val intolerances: ArrayList<String>,
////    @SerializedName("ingredients")
////    val ingredients: ArrayList<FoodItem>,
//    // refers to categorization e.g. main, dessert, etc.
//    @SerializedName("type")
//    val type: String,
//    @SerializedName("nutrition")
//    val nutrition: Nutrition?,
//    // related to attribution
//    val limitLicense: String?,
//    @SerializedName("author")
//    val author: String?,
//    @SerializedName("sourceName")
//    val sourceName: String?,
//    @SerializedName("sourceURL")
//    val sourceURL: String?,
//    @SerializedName("spoonacularSourceURL")
//    val spoonacularSourceRL: String?,
//    @SerializedName("instructions")
//    val instructions: List<Steps>?
//    ) : Parcelable
