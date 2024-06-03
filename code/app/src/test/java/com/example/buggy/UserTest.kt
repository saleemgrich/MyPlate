package com.example.buggy

import com.example.buggy.data.model.User
import junit.framework.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Test

class UserTest {

    @Test
    fun createUser() {
        // Create a User object with sample data
        val user = User(
            email = "john@example.com",
            friends = arrayListOf(456, 789),
            name = "John Doe",
            password = "password123",
            phoneNumber = "1234567890",
            userID = "123",
            username = "johndoe"
        )

        // Verify the correctness of the user
        assertNotNull(user)
        assertEquals("john@example.com", user.email)
        assertEquals(arrayListOf(456, 789), user.friends)
        assertEquals("John Doe", user.name)
        assertEquals("password123", user.password)
        assertEquals("1234567890", user.phoneNumber)
        assertEquals("123", user.userID)
        assertEquals("johndoe", user.username)
    }
}
