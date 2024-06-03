package com.example.buggy.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.example.buggy.R
import com.example.buggy.adapters.CustomAdapter
import com.example.buggy.data.appdata.FoodItemList
import com.example.buggy.data.model.FoodItem
import com.example.buggy.network.FirestoreManager
import com.google.android.material.bottomnavigation.BottomNavigationView


class PantryActivity : ComponentActivity() {

    private lateinit var add_item_button: Button
    private lateinit var remove_item_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantry)

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
        // Manually selects Pantry as the desired menu item
        val menu = bottomNavigationView.menu
        val menuItem = menu.findItem(R.id.menu_item_3) // Change to the desired menu item ID
        menuItem.isChecked = true

        add_item_button = findViewById(R.id.addItemButton)
        remove_item_button = findViewById(R.id.removeItemButton)

        val listView = findViewById<ListView>(R.id.listView)
        val selectedFoodItems = mutableListOf<FoodItem>() // Initialize a mutable list to hold selected food items
        val foodItems = FoodItemList.foodItems

        val listViewAdapter = CustomAdapter(this, selectedFoodItems)
        listView.adapter = listViewAdapter
        listViewAdapter.notifyDataSetChanged()

        FirestoreManager.fetchFoodItemsFromPantry { fetchedFoodItems ->
            selectedFoodItems.clear()
            selectedFoodItems.addAll(fetchedFoodItems)
            listViewAdapter.notifyDataSetChanged()
        }

        add_item_button.setOnClickListener {

            val adapter = object : ArrayAdapter<FoodItem>(this, android.R.layout.simple_list_item_1, foodItems) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = convertView ?: layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                    val foodItem = getItem(position)
                    view.findViewById<TextView>(android.R.id.text1).apply {
                        text = foodItem?.name
                        setCompoundDrawablesWithIntrinsicBounds(foodItem?.imageResId ?: 0, 0, 0, 0)
                        compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.drawable_padding) // Set padding if needed
                    }
                    return view
                }
            }

            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Add item")
            dialogBuilder.setMessage("Enter a food item to add")
            val input = EditText(this)
            dialogBuilder.setView(input)
            dialogBuilder.setPositiveButton("OK") { _, _ ->
                val item = input.text.toString().capitalize()
                // Update the pantry in Firestore
                if(!containsFoodItem(selectedFoodItems, item)) {
                    if(containsFoodItem(foodItems, item)) {
                        selectedFoodItems.add(foodItems[indexOfFoodItem(foodItems,item)])
                        FirestoreManager.addFoodItemToPantry(foodItems[indexOfFoodItem(foodItems,item)])
                    }else {
                        val temp = FoodItem(R.drawable.baseline_flatware_24, item)
                        selectedFoodItems.add(temp)
                        FirestoreManager.addFoodItemToPantry(temp)
                    }
                    listViewAdapter.notifyDataSetChanged()
                }
            }
            dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            dialogBuilder.show()
        }

        remove_item_button.setOnClickListener {
            val adapter = object : ArrayAdapter<FoodItem>(this, android.R.layout.simple_list_item_1, selectedFoodItems) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = convertView ?: layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                    val foodItem = getItem(position)
                    view.findViewById<TextView>(android.R.id.text1).apply {
                        text = foodItem?.name
                        setCompoundDrawablesWithIntrinsicBounds(foodItem?.imageResId ?: 0, 0, 0, 0)
                        compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.drawable_padding) // Set padding if needed
                    }
                    return view
                }
            }

            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Select a food item to remove")
            dialogBuilder.setAdapter(adapter) { dialog, which ->
                val temp = selectedFoodItems[which]
                // Handle item click
                if (selectedFoodItems.contains(temp)) {
                    selectedFoodItems.remove(temp)
                    FirestoreManager.removeFoodItemFromPantry(temp)
                    listViewAdapter.notifyDataSetChanged()
                }
            }
            dialogBuilder.create().show()
        }

    }
    fun containsFoodItem(foodItems: List<FoodItem>, targetName: String): Boolean {
        // Iterate through each FoodItem in the list
        for (foodItem in foodItems) {
            // Check if the name of the current FoodItem matches the target name
            if (foodItem.name.equals(targetName, ignoreCase = true)) {
                // If found, return true
                return true
            }
        }
        // If the loop completes without finding a match, return false
        return false
    }

    fun indexOfFoodItem(foodItems: List<FoodItem>, targetName: String): Int {
        // Iterate through each FoodItem in the list
        for (index in foodItems.indices) {
            // Check if the name of the current FoodItem matches the target name
            if (foodItems[index].name.equals(targetName, ignoreCase = true)) {
                // If found, return the index
                return index
            }
        }
        // If the loop completes without finding a match, return -1 to indicate not found
        return -1
    }
}