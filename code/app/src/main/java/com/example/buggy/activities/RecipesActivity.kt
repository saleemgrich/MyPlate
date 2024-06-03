package com.example.buggy.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buggy.R
import com.example.buggy.adapters.RecipeAdapter
import com.example.buggy.api.SpoonacularClient
import com.example.buggy.data.appdata.Allergies
import com.example.buggy.data.appdata.DietaryRestrictions
import com.example.buggy.data.appdata.SavedRecipes
import com.example.buggy.data.model.FoodItem
import com.example.buggy.network.FirestoreManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class RecipesActivity : ComponentActivity() {
    private lateinit var filters_button: Button
    private lateinit var recipe_search_button: Button
    private lateinit var dietary_restrictions_button: Button

    private var query : String = ""
    private var number : Int = 7 // Of recipes returned from query
    private var cuisine : String = ""
    private var diet : String = ""
    private var allergy : String = ""
    private var cookTime : Int = 60
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_1 -> {
                    startActivity(Intent(this, DailyNutritionActivity::class.java))
                    finish()
                    true
                }

                R.id.menu_item_2 -> {
                    true
                }

                R.id.menu_item_3 -> {
                    startActivity(Intent(this, PantryActivity::class.java))
                    finish()
                    true
                }

                R.id.menu_item_4 -> {
                    startActivity(Intent(this, FriendsActivity::class.java))
                    finish()
                    true
                }

                R.id.menu_item_5 -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }

                else -> false
            }
        }
        // Manually selects Recipes as the desired menu item
        val menu = bottomNavigationView.menu
        val menuItem = menu.findItem(R.id.menu_item_2) // Change to the desired menu item ID
        menuItem.isChecked = true

        // Saved Recipes adapter setup
        var recyclerView: RecyclerView = findViewById(R.id.savedRecipesView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        var adapter = RecipeAdapter(this, SavedRecipes.recipes)
        recyclerView.adapter = adapter

        // Suggested Recipes adapter setup
        var recyclerView2: RecyclerView = findViewById(R.id.suggestedRecipesView)
        val layoutManager2 = LinearLayoutManager(this)
        recyclerView2.layoutManager = layoutManager2
        var adapter2 = RecipeAdapter(this, SavedRecipes.suggested_recipes)
        recyclerView2.adapter = adapter2

        adapter2.setDatasetUpdateListener(adapter)
        adapter.setDatasetUpdateListener(adapter2)

        if (SavedRecipes.suggested_recipes.isEmpty()) {
            val apiKey = "ea2db2a0cb4946fc98288e25bd98720d"
            val diet = DietaryRestrictions.getRestrictionsString()
            val allergy = Allergies.getAllergiesString()

            val context = this
            FirestoreManager.fetchFoodItemsFromPantry { foodItems ->
                val ingredients = foodToString(foodItems)
                val spoonacularClient = SpoonacularClient(apiKey)
                lifecycleScope.launch {
                    Log.d(ContentValues.TAG, ingredients)
                    val recipes = spoonacularClient.searchRecipes(
                        query, number, cuisine, diet, allergy, ingredients, cookTime
                    )
                    if (recipes != null) {
                        SavedRecipes.suggested_recipes = recipes.toMutableList()
                        SavedRecipes.suggested_recipes =
                            SavedRecipes.suggested_recipes.filter { suggestedRecipe ->
                                SavedRecipes.recipes.none { savedRecipe ->
                                    suggestedRecipe.id == savedRecipe.id
                                }
                            }.toMutableList()
                        val adapter2 = RecipeAdapter(context, SavedRecipes.suggested_recipes)
                        recyclerView2.adapter = adapter2
                        adapter2.setDatasetUpdateListener(adapter)
                    } else {
                        // Handle null response here
                        Log.e(ContentValues.TAG, "Null recipes received")
                    }

                }
            }
        } else {
            adapter2 = RecipeAdapter(this, SavedRecipes.suggested_recipes)
            recyclerView2.adapter = adapter2
            adapter2.setDatasetUpdateListener(adapter)
        }

        recipe_search_button = findViewById(R.id.recipeSearchButton)
        dietary_restrictions_button = findViewById(R.id.dietaryRestrictionsButton)

        dietary_restrictions_button.setOnClickListener {
            startActivity(Intent(this, DietaryRestrictionsActivity::class.java))
            finish()
        }

        recipe_search_button.setOnClickListener {
            startActivity(Intent(this, RecipeSearchActivity::class.java))
            finish()
        }
    }

    fun foodToString(foodItems: List<FoodItem>): String {
        val separator = ", " // Define the separator
        val shuffledItems = foodItems.shuffled()
        return shuffledItems.take(2).joinToString(separator) { it.name }
    }
}