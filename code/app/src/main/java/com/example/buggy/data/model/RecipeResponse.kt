package com.example.buggy.data.model

data class RecipeResponse(
    val offset: Int,
    val number: Int,
    val results: List<Recipe>,
    val totalResults: Int,
)

data class AnalyzedInstruction(
    val steps: List<Step>
)

data class Step(
    val number: Int,
    val step: String
)