package com.example.buggy.data.model

/*
Data class for User object
 */
data class User(
    var email: String = "",
    var friends: ArrayList<Int>? = null, // by userID
    var name: String = "",
    var password: String = "",
    var phoneNumber: String = "",
    val userID: String = "",
    var username: String = "",
)
