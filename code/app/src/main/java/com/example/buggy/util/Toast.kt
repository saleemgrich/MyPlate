package com.example.buggy.util

import android.content.Context
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText

fun showToast(context: Context, msg: String) {
    makeText(context, msg, LENGTH_LONG).show()
}