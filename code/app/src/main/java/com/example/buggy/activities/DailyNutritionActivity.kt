package com.example.buggy.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buggy.R
import com.example.buggy.adapters.DatedRecipeAdapter
import com.example.buggy.adapters.RecipeAdapter
import com.example.buggy.data.appdata.DatedRecipes
import com.example.buggy.data.appdata.TodaysRecipes
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date

class DailyNutritionActivity : ComponentActivity() {

    private lateinit var calendar: CalendarView
    private val sdf = SimpleDateFormat("ddMMyyyy")
    var rn = (sdf.format(Date()))
    private lateinit var calendarTitle: TextView
    private lateinit var recommendedRecipes: TextView
    private lateinit var sharePlan: Button
    private lateinit var logNutrients: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_nutrition)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_1 -> {
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
        // Manually selects Daily Nutrition as the desired menu item
        val menu = bottomNavigationView.menu
        val menuItem = menu.findItem(R.id.menu_item_1) // Change to the desired menu item ID
        menuItem.isChecked = true

        calendar = findViewById(R.id.calendarView)
//        calendarTitle = findViewById(R.id.calendarTitle)
//        recommendedRecipes = findViewById(R.id.recommendedRecipes)
//        sharePlan = findViewById(R.id.sharePlan)
//        logNutrients = findViewById(R.id.LogNutri)

        // Today's Recipes adapter setup
        var recyclerView: RecyclerView = findViewById(R.id.todaysRecipesView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        var adapter = RecipeAdapter(this, TodaysRecipes.recipes)
        recyclerView.adapter = adapter

        calendar
            .setOnDateChangeListener(
                OnDateChangeListener { _, year, monthOfYear, dayOfMonth ->
                    var month = (monthOfYear+1).toString()
                    if (month.length < 2) {
                        month = "0$month"
                    }
                    var day = dayOfMonth.toString()
                    if (day.length < 2) {
                        day = "0$day"
                    }


                    val currentDay = (day + month + year)
                    Log.d("New Day", currentDay)

                    if (currentDay == rn) {
                        var adapter = RecipeAdapter(this, TodaysRecipes.recipes)
                        recyclerView.adapter = adapter
                    } else {


                        var filtered = DatedRecipes.recipes.filter { it.date == currentDay }
                        var adapter = DatedRecipeAdapter.DatedRecipeAdapter(this, filtered)
                        recyclerView.adapter = adapter
                    }
                    //recommendedRecipes.setText(currentDay)
                        /*for (recipe in TodaysRecipes) {
                            val datedrecipe = DatedRecipe().apply {
                                this.id = recipe.id
                                this.title = recipe.title
                                this.imageUrl = recipe.imageUrl
                                this.instructions = recipe.instructions
                                this.date = dayDisplayed
                            }
                            FirestoreManager.addDatedRecipe(datedrecipe)
                            FirestoreManager.deleteTodaysRecipe(recipe.id)
                        }

                    dayDisplayed = currentDay
                    for (recipe in DatedRecipesCopy) {
                        if (recipe.date == dayDisplayed) {

                            val newrecipe = Recipe().apply {
                                this.id = recipe.id
                                this.title = recipe.title
                                this.imageUrl = recipe.imageUrl
                                this.instructions = recipe.instructions
                            }
                            FirestoreManager.addTodaysRecipe(newrecipe)
                            FirestoreManager.deleteDatedRecipe(recipe.id)
                        }

                    }*/


                    adapter.setDatasetUpdateListener(adapter)

                })


        //recommendedRecipes.text = (sdf.format(Date()))




        adapter.setDatasetUpdateListener(adapter)


    }
    private fun deselectMenuItem(bottomNavigationView: BottomNavigationView, itemId: Int) {
        bottomNavigationView.menu.findItem(itemId)?.isChecked = false
    }
}