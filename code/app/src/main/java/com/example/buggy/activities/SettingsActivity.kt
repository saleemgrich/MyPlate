package com.example.buggy.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.example.buggy.R
import com.example.buggy.data.appdata.AppUserData
import com.example.buggy.network.FirestoreManager
import com.example.buggy.network.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : ComponentActivity(){

    //button declarations for settings page
    private lateinit var change_username_button: Button
    private lateinit var change_password_button: Button
    private lateinit var update_email_button: Button
    private lateinit var update_phone_number_button: Button
    private lateinit var log_out_button: Button
    private lateinit var delete_account_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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
                    true
                }
                else -> false
            }
        }
        // Manually selects Settings as the desired menu item
        val menu = bottomNavigationView.menu
        val menuItem = menu.findItem(R.id.menu_item_5) // Change to the desired menu item ID
        menuItem.isChecked = true

        //set buttons to respective IDs
        change_username_button = findViewById(R.id.changeUsernameButton)
        change_password_button = findViewById(R.id.changePasswordButton)
        update_email_button = findViewById(R.id.updateEmailButton)
        update_phone_number_button = findViewById(R.id.updatePhoneNumberButton)
        log_out_button = findViewById(R.id.logoutButton)
        delete_account_button = findViewById(R.id.deleteAccountButton)

        var tempText = getString(R.string.change_username_button_text, AppUserData.currentUser?.username)
        change_username_button.text = tempText
        tempText = getString(R.string.change_password_button_text, AppUserData.currentUser?.password)
        change_password_button.text = tempText
        tempText = getString(R.string.update_email_button_text, AppUserData.currentUser?.email)
        update_email_button.text = tempText
        tempText = getString(R.string.update_phone_button_text, AppUserData.currentUser?.phoneNumber)
        update_phone_number_button.text = tempText

        //onClickListeners for each button
        change_username_button.setOnClickListener{
            FirestoreManager.changeUsername(this) { isSuccess ->
                if (isSuccess) {
                    // Username change was successful
                    tempText = getString(R.string.change_username_button_text, AppUserData.currentUser?.username)
                    change_username_button.text = tempText
                } else {
                    // Username change failed or was canceled
                }
            }
        }

        change_password_button.setOnClickListener{
            FirestoreManager.changePassword(this) { isSuccess ->
                if (isSuccess) {
                    // Password change was successful
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    // Password change failed or was canceled
                }
            }
        }

        update_email_button.setOnClickListener{
            FirestoreManager.changeEmail(this) { isSuccess ->
                if (isSuccess) {
                    // Email change was successful
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    // Email change failed or was canceled
                }
            }
        }

        update_phone_number_button.setOnClickListener{
            FirestoreManager.changePhone(this) { isSuccess ->
                if (isSuccess) {
                    // Phone number change was successful
                    tempText = getString(R.string.update_phone_button_text, AppUserData.currentUser?.phoneNumber)
                    update_phone_number_button.text = tempText
                } else {
                    // Phone number change failed or was canceled
                }
            }
        }

        log_out_button.setOnClickListener {
            var loginManager = LoginManager(this)
            loginManager.clearSavedCredentials()
            FirestoreManager.logOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        delete_account_button.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Account")
            builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.")

            builder.setPositiveButton("Delete") { dialog, _ ->
                // User clicked delete button, perform deletion
                FirestoreManager.deleteAccount()
                dialog.dismiss()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                // User clicked cancel button, dismiss dialog
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}