package com.example.buggy.activities

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.example.buggy.R
import com.example.buggy.data.appdata.AppUserData
import com.example.buggy.network.FirestoreManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class FriendsActivity : ComponentActivity() {

    private lateinit var add_friend_button: Button
    private lateinit var remove_friend_button: Button
    private val friendsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        //Bottom Navigation Bar code
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
        //End Bottom Navigation Bar Code

        // Manually selects Friends as the desired menu item
        val menu = bottomNavigationView.menu
        val menuItem = menu.findItem(R.id.menu_item_4) // Change to the desired menu item ID
        menuItem.isChecked = true

        add_friend_button = findViewById(R.id.addFriendButton)
        remove_friend_button = findViewById(R.id.removeFriendButton)

        val listView = findViewById<ListView>(R.id.friendsList)
        val friendsList = mutableListOf<String>() // Initialize an empty list for friends

        // Create an adapter for the ListView
        val listViewAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendsList)
        listView.adapter = listViewAdapter // Set the adapter to the ListView

        FirestoreManager.getFriendsList { fetchedFriendsList ->
            friendsList.clear() // Clear the current list
            friendsList.addAll(fetchedFriendsList) // Update the list with fetched friends
            listViewAdapter.notifyDataSetChanged() // Notify the adapter of data change
        }

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // Retrieve the clicked friend from the list
            val clickedFriend = friendsList[position]
            val intent = Intent(this, FriendRecipesActivity::class.java)
            intent.putExtra("friendName", clickedFriend)
            startActivity(intent)
        }

        add_friend_button.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Follow Friend")
            dialogBuilder.setMessage("Enter a username")
            val input = EditText(this)
            dialogBuilder.setView(input)
            dialogBuilder.setPositiveButton("OK") { _, _ ->
                val username = input.text.toString()
                // Update the friend in Firestore
                if(username == AppUserData.currentUser?.username) {
                    Toast.makeText(this,"Cannot add yourself", Toast.LENGTH_SHORT).show()
                } else if(!friendsList.contains(username)) {
                    FirestoreManager.addFriend(this, username,
                        onSuccess = {
                            friendsList.add(username)
                            listViewAdapter.notifyDataSetChanged()
                        })
                }
            }
            dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            dialogBuilder.show()
        }

        remove_friend_button.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Unfollow Friend")
            dialogBuilder.setMessage("Enter a username")
            val input = EditText(this)
            dialogBuilder.setView(input)
            dialogBuilder.setPositiveButton("OK") { _, _ ->
                val username = input.text.toString()
                // Update the friend in Firestore
                if(friendsList.contains(username)) {
                    friendsList.remove(username)
                    FirestoreManager.deleteFriend(username)
                    listViewAdapter.notifyDataSetChanged()
                }
            }
            dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            dialogBuilder.show()
        }
    }
}