package com.example.buggy.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.buggy.R
import com.example.buggy.data.model.User
import com.example.buggy.network.FirestoreManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class CreateAccountActivity : ComponentActivity() {
    private lateinit var create_account: Button

    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var password2: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_6 -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }

                R.id.menu_item_7 -> {
                    true
                }
                else -> false
            }
        }

        create_account = findViewById(R.id.createAccountButton)

        username = findViewById(R.id.editTextUsername)
        email = findViewById(R.id.editTextEmail2)
        password = findViewById(R.id.editTextPassword2)
        password2 = findViewById(R.id.editTextPassword3)

        create_account.setOnClickListener {
            val username = username.text.toString()
            val email = email.text.toString().lowercase(Locale.ROOT)
            val password = password.text.toString()
            val password2 = password2.text.toString()
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (password != password2) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else if (isPasswordValid(password)) {
                FirestoreManager.checkEmailExists(email) { exists ->
                    if(exists) {
                        Toast.makeText(this, "Email is already in use", Toast.LENGTH_SHORT).show()
                    } else {
                        FirestoreManager.checkUsernameExists(username) { userExists ->
                            if(userExists) {
                                Toast.makeText(this, "Username is already in use", Toast.LENGTH_SHORT).show()
                            } else {
                                val newUser = User(email, null, "testname", password,
                                    "0000000000", "", username)
                                FirestoreManager.writeUser(newUser) { isSuccess, errorMessage ->
                                    if (isSuccess) {
                                        startActivity(Intent(this, LoginActivity::class.java))
                                    } else {
                                        Toast.makeText(this, errorMessage ?: "Create Account failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }else {
                // Password is not valid
            }
        }
    }
    private fun isPasswordValid(password: String): Boolean {
        // Check if password length is at least 8 characters
        if (password.length < 8) {
            Toast.makeText(this, "Password must be 8 characters long", Toast.LENGTH_SHORT).show()
            return false
        }

        var hasLetter = false
        var hasNumber = false

        // Check each character in the password
        for (char in password) {
            // Check if character is a letter
            if (char.isLetter()) {
                hasLetter = true
            }
            // Check if character is a digit (number)
            if (char.isDigit()) {
                hasNumber = true
            }
        }
        if(!hasLetter){
            Toast.makeText(this, "Password must have at least one letter", Toast.LENGTH_SHORT).show()
        }
        if(!hasNumber) {
            Toast.makeText(this, "Password must have at least one number", Toast.LENGTH_SHORT).show()
        }
        // Check if the password has at least one letter and one number
        return hasLetter && hasNumber
    }
}