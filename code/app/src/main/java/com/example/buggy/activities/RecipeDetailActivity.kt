package com.example.buggy.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.buggy.R
import com.example.buggy.data.appdata.DatedRecipes
import com.example.buggy.data.appdata.TodaysRecipes
import com.example.buggy.data.model.DatedRecipe
import com.example.buggy.data.model.Recipe
import com.example.buggy.network.FirestoreManager
import com.example.buggy.network.FirestoreManager.addDatedRecipe
import com.example.buggy.network.FirestoreManager.deleteDatedRecipe
import java.util.Calendar


class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var addToTodayButton: Button
    private lateinit var addToDifferentDay: Button
    private lateinit var removeMeal: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        val recipeJson = intent.getStringExtra("recipeJson")
        val recipe = recipeJson?.let { Recipe.fromJson(it) }

        val recipeImage: ImageView = findViewById(R.id.recipe_image)
        val recipeTitle: TextView = findViewById(R.id.recipe_title)
        val recipeInstructions: TextView = findViewById(R.id.recipe_instructions)
        backButton = findViewById(R.id.recipedetailbackbutton)

        backButton.setOnClickListener {
            startActivity(Intent(this,RecipesActivity::class.java))
            finish()
        }

        removeMeal = findViewById(R.id.removeMeal)

        removeMeal.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    var month = (monthOfYear+1).toString()
                    if (month.length < 2) {
                        month = "0$month"
                    }
                    var day = dayOfMonth.toString()
                    if (day.length < 2) {
                        day = "0$day"
                    }
                    val datepicked = (day + month + year)
                    if (recipe != null) {
                        deleteDatedRecipe(recipe.id!!, datepicked)

                        FirestoreManager.deleteDatedRecipe(recipe.id!!, datepicked)
                        DatedRecipes.recipes.removeAll {it.id == recipe.id && it.date == datepicked}
                    }
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        addToDifferentDay = findViewById(R.id.addToDifferentDay)

        addToDifferentDay.setOnClickListener {
            val cal = Calendar.getInstance()

            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)



            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    var month = (monthOfYear+1).toString()
                    if (month.length < 2) {
                        month = "0$month"
                    }
                    var day = dayOfMonth.toString()
                    if (day.length < 2) {
                        day = "0$day"
                    }

                    val datepicked = (day + month + year)

                    Log.d("date", datepicked)

                    if (recipe != null) {
                        val datedrecipe = DatedRecipe().apply {
                            this.id = recipe.id
                            this.title = recipe.title
                            this.imageUrl = recipe.imageUrl
                            this.instructions = recipe.instructions
                            this.date = datepicked
                        }
                        addDatedRecipe(datedrecipe)
                        DatedRecipes.recipes.add(datedrecipe)
                    }



                },
                year,
                month,
                day


            )
            datePickerDialog.show()


        }

        addToTodayButton = findViewById(R.id.addToToday)
        if(recipe != null) {
            if (TodaysRecipes.recipes.any { it.id == recipe.id }) {
                addToTodayButton.text = "Remove from Today's Meal Plan"
            }
        }

        addToTodayButton.setOnClickListener {
            if (recipe != null) {
                if (!TodaysRecipes.recipes.any { it.id == recipe.id }) {
                    FirestoreManager.addTodaysRecipe(recipe)
                    TodaysRecipes.recipes.add(recipe)
                    addToTodayButton.text = "Remove from Today's Meal Plan"
                } else {
                    recipe.id?.let {
                        it1 -> FirestoreManager.deleteTodaysRecipe(it1)
                        TodaysRecipes.recipes.removeAll {it.id == it1}
                    }
                    addToTodayButton.text = "Add to Today's Meal Plan"
                }
            }
        }


        if (recipe != null) {
            Glide.with(this)
                .load(recipe.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(recipeImage)
        }

        if (recipe != null) {
            recipeTitle.text = recipe.title
            recipeInstructions.text = recipe.instructions ?: "No instructions available"
        }




    }
}
