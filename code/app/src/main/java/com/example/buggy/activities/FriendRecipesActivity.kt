package com.example.buggy.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buggy.R
import com.example.buggy.adapters.RecipeAdapter
import com.example.buggy.data.model.Recipe
import com.example.buggy.network.FirestoreManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class FriendRecipesActivity : ComponentActivity() {
    private lateinit var back_button: Button
    private lateinit var title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_recipes)

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

        back_button = findViewById(R.id.friendRecipesBackButton)
        title = findViewById(R.id.friendRecipesTextView)

        back_button.setOnClickListener{
            startActivity(Intent(this, FriendsActivity::class.java))
            finish()
        }

        val recyclerView: RecyclerView = findViewById(R.id.friendRecipesRecipeView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        // Get Friend's Recipes
        val friendName = intent.getStringExtra("friendName")
        title.setText("$friendName's Saved Recipes")
        var recipes = mutableListOf<Recipe>()
        if (friendName != null) {
            FirestoreManager.getFriendRecipes(friendName) { recipeList ->
                recipes = recipeList
                val adapter = RecipeAdapter(this, recipes)
                recyclerView.adapter = adapter
                if(recipes.isNullOrEmpty()) {
                    Toast.makeText(this,"$friendName has no recipes saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}