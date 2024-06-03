package com.example.buggy.data.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
class DatedRecipe {

    @SerializedName("id") var id: String? = null
    @SerializedName("title") var title: String? = null
    @SerializedName("image") var imageUrl: String? = null
    @SerializedName("analyzedInstructions") var analyzedInstructions: List<AnalyzedInstruction>? = null
    @SerializedName("date") var date: String? = null
    var instructions: String? = null


    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object{
        fun fromJson(json: String): DatedRecipe {
            return Gson().fromJson(json, DatedRecipe::class.java)
        }
    }


    fun toMap(): Map<String, String?> {
        return mapOf(
            "id" to id,
            "title" to title,
            "imageUrl" to imageUrl,
            "instructions" to instructions,
            "date" to date
        )
    }
}