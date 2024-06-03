package com.example.buggy.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buggy.R
import com.example.buggy.adapters.RecipeAdapter
import com.example.buggy.api.SpoonacularClient
import com.example.buggy.data.appdata.Allergies
import com.example.buggy.data.appdata.DietaryRestrictions
import com.example.buggy.data.model.Recipe
import com.example.buggy.util.config
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class RecipeSearchActivity : ComponentActivity() {
    private lateinit var query_text: EditText
    private lateinit var cook_time_text: EditText
    private lateinit var cuisine_spinner: Spinner
    private lateinit var back_button: Button
    private lateinit var recipe_search_button: Button

    private var query : String = ""
    private var number : Int = 10 // Of recipes returned from query
    private var cuisine : String = ""
    private var diet : String = ""
    private var allergy : String = ""
    private var ingredients : String = ""
    private var cookTime : Int = 60
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_search)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_1 -> {
                    startActivity(Intent(this, DailyNutritionActivity::class.java))
                    finish()
                    true
                }
                R.id.menu_item_2 -> {
                    startActivity(Intent(this, RecipesActivity::class.java))
                    finish()
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
        // Manually selects Settings as the desired menu item
        val menu = bottomNavigationView.menu
        val menuItem = menu.findItem(R.id.menu_item_2) // Change to the desired menu item ID
        menuItem.isChecked = true

        val cuisines = listOf<String>("None", "African", "Asian", "American", "British", "Cajun",
            "Caribbean", "Chinese", "Eastern European", "European", "French", "German" , "Greek",
            "Indian", "Irish", "Italian", "Japanese", "Jewish", "Korean", "Latin American",
            "Mediterranean", "Mexican", "Middle Eastern", "Nordic", "Southern", "Spanish", "Thai", "Vietnamese")
        cuisine_spinner = findViewById(R.id.cuisine_spinner)
        var isInitialized = false  // Flag to track initialization
        cuisine_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isInitialized) {
                    val selectedCuisine = cuisines[position]
                    cuisine = selectedCuisine
                    Toast.makeText(applicationContext, "Selected Cuisine: $selectedCuisine", Toast.LENGTH_SHORT).show()
                } else {
                    // Skip the toast when Spinner is initialized
                    isInitialized = true
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cuisines)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cuisine_spinner.adapter = adapter
        //recipe_adapter
        query_text = findViewById(R.id.queryText)
        cook_time_text = findViewById(R.id.cookTimeText)

        back_button = findViewById(R.id.recipeSearchBackButton)
        recipe_search_button = findViewById(R.id.searchButton)

        back_button.setOnClickListener{
            startActivity(Intent(this, RecipesActivity::class.java))
            finish()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recipeSearchRecipeView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        var recipes = listOf<Recipe>()
        recipe_search_button.setOnClickListener {
            val apiKey = config.api_key
            query = query_text.text.toString()
            diet = DietaryRestrictions.getRestrictionsString()
            allergy = Allergies.getAllergiesString()
            try {
                cookTime = cook_time_text.text.toString().toInt()
            } catch (e: NumberFormatException) {
//                Toast.makeText(applicationContext, "Invalid input for cook time", Toast.LENGTH_SHORT).show()
            }
            val spoonacularClient = SpoonacularClient(apiKey)
            val context = this
            lifecycleScope.launch{
                recipes = spoonacularClient.searchRecipes(query, number, cuisine, diet, allergy, ingredients, cookTime)!!
                if (recipes.isNotEmpty()) {
                    val adapter = RecipeAdapter(context, recipes)
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(context, "No Recipes Found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}