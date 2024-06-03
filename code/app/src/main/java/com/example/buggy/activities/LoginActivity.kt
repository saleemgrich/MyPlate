package com.example.buggy.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.buggy.R
import com.example.buggy.network.FirestoreManager
import com.example.buggy.network.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class LoginActivity : ComponentActivity() {
    private lateinit var login_button: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var plate: ImageView
    private lateinit var loginManager: LoginManager
    private lateinit var remember_checkbox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginManager = LoginManager(this)
        if (loginManager.areCredentialsSaved()) {
            // If credentials are saved, automatically login
            val savedEmail = loginManager.getEmail()
            val savedPassword = loginManager.getPassword()
            if (savedEmail == null || savedPassword == null) {
                Toast.makeText(this, "Please enter an email and password", Toast.LENGTH_SHORT).show()
            } else {
                FirestoreManager.readUser(savedEmail, savedPassword) { isSuccess, errorMessage ->
                    if (isSuccess) {
                        // Login successful, navigate to MainMenuActivity
                        startActivity(Intent(this, PantryActivity::class.java))
                        finish()
                    } else {
                        // Login failed, display error message
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        plate = findViewById(R.id.imageView2)
        plate.setImageResource(R.drawable.plate_icon)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_6 -> {
                    true
                }

                R.id.menu_item_7 -> {
                    startActivity(Intent(this, CreateAccountActivity::class.java))
                    finish()
                    true
                }

                else -> false
            }
        }

        login_button = findViewById(R.id.loginButton)
        remember_checkbox = findViewById(R.id.rememberCheckBox)

        email = findViewById(R.id.editTextEmail)
        password = findViewById(R.id.editTextPassword)

        login_button.setOnClickListener {
            val email = email.text.toString().lowercase(Locale.ROOT)
            val password = password.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter an email and password", Toast.LENGTH_SHORT).show()
            } else {
                FirestoreManager.readUser(email, password) { isSuccess, errorMessage ->
                    if (isSuccess) {
                        // Login successful, navigate to MainMenuActivity
                        if(!remember_checkbox.isChecked && loginManager.areCredentialsSaved()) {
                            loginManager.clearSavedCredentials()
                        } else {
                            loginManager.saveLoginCredentials(email, password)
                        }
                        startActivity(Intent(this, PantryActivity::class.java))
                        finish()
                    } else {
                        // Login failed, display error message
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}