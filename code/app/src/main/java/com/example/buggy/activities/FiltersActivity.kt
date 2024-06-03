package com.example.buggy.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.buggy.R

class FiltersActivity : ComponentActivity(){

    private lateinit var backButton: Button
   override fun onCreate(savedInstanceState: Bundle?){
       super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_filters)

       backButton = findViewById(R.id.filtersBackButton)

       backButton.setOnClickListener {
           startActivity(Intent(this,RecipesActivity::class.java))
           finish()
       }
   }
}