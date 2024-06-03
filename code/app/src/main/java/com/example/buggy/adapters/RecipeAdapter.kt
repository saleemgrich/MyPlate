package com.example.buggy.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buggy.R
import com.example.buggy.activities.RecipeDetailActivity
import com.example.buggy.data.appdata.SavedRecipes
import com.example.buggy.data.model.Recipe
import com.example.buggy.network.FirestoreManager

interface DatasetUpdateListener {
    fun onDatasetUpdate()
}

class RecipeAdapter(private val context: Context, private val recipes: List<Recipe>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>(), DatasetUpdateListener {

    private var datasetUpdateListener: DatasetUpdateListener? = null
    fun setDatasetUpdateListener(listener: DatasetUpdateListener) {
        datasetUpdateListener = listener
    }
    override fun onDatasetUpdate() {
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_widget, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImage: ImageView = itemView.findViewById(R.id.recipe_image)
        private val recipeName: TextView = itemView.findViewById(R.id.recipe_name)
        private val viewRecipeButton: Button = itemView.findViewById(R.id.view_recipe_button)
        private val saveRecipeButton: Button = itemView.findViewById(R.id.save_recipe_button)

        fun bind(recipe: Recipe) {
            // Set recipe name
            recipeName.text = recipe.title
            // Load recipe image using Glide or any other image loading library
            Glide.with(itemView)
                .load(recipe.imageUrl)
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                .error(R.drawable.error_image) // Error image if loading fails
                .into(recipeImage)

            saveRecipeButton.text = if (SavedRecipes.recipes.any { it.id == recipe.id }) {
                "Unsave Recipe"
            } else {
                "Save Recipe"
            }
            // Set click listener for save recipe button
            saveRecipeButton.setOnClickListener {
                if (!SavedRecipes.recipes.any { it.id == recipe.id }) {
                    FirestoreManager.addRecipe(recipe)
                    SavedRecipes.recipes.add(recipe)
                    SavedRecipes.suggested_recipes.remove(recipe) // Remove from suggested recipes if it exists there
                    saveRecipeButton.text = "Unsave Recipe"
                } else {
                    recipe.id?.let { it1 -> FirestoreManager.deleteRecipe(it1) }
                    SavedRecipes.recipes.remove(recipe)
                    saveRecipeButton.text = "Save Recipe"
                }
                // Notify the listener and update the UI
                datasetUpdateListener?.onDatasetUpdate()
                notifyDataSetChanged()
            }
            // Set click listener for view recipe button
            viewRecipeButton.setOnClickListener {
                val intent = Intent(context, RecipeDetailActivity::class.java)
                intent.putExtra("recipeJson", recipe.toJson())
                context.startActivity(intent)
            }
        }
    }
}
