package com.example.buggy.network

import android.content.Context
import android.content.SharedPreferences

class LoginManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    // Function to save login credentials
    fun saveLoginCredentials(email: String, password: String) {
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    // Function to check if credentials exist
    fun areCredentialsSaved(): Boolean {
        return sharedPreferences.contains("email") && sharedPreferences.contains("password")
    }

    // Function to retrieve saved email
    fun getEmail(): String? {
        return sharedPreferences.getString("email", null)
    }

    // Function to retrieve saved password
    fun getPassword(): String? {
        return sharedPreferences.getString("password", null)
    }

    // Function to clear saved credentials
    fun clearSavedCredentials() {
        editor.remove("email")
        editor.remove("password")
        editor.apply()
    }
}