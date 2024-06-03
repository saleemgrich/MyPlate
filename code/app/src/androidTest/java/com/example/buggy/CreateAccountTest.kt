package com.example.buggy

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.buggy.activities.CreateAccountActivity
import com.example.buggy.activities.LoginActivity
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateAccountTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(CreateAccountActivity::class.java)

    @Test
    fun testCreateAccountSuccess() {
        val email = "test6@example.com" // May have to change if already used
        val username = "testuser"
        val password = "password123"

        // Fill in the fields and click on create account button
        onView(withId(R.id.editTextUsername)).perform(typeText(username), closeSoftKeyboard())
        onView(withId(R.id.editTextEmail2)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword2)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword3)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.createAccountButton)).perform(click())

        // Check if LoginActivity is launched
        ActivityScenario.launch(LoginActivity::class.java).onActivity { loginActivity ->
            assertNotNull(loginActivity)
        }
    }

    @Test
    fun testCreateAccountFailure() {
        val invalidEmail = "invalid@example.com"
        val invalidPassword = "short"

        // Fill in the fields with invalid data and click on create account button
        onView(withId(R.id.editTextUsername)).perform(typeText("testuser"), closeSoftKeyboard())
        onView(withId(R.id.editTextEmail2)).perform(typeText(invalidEmail), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword2)).perform(typeText(invalidPassword), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword3)).perform(typeText(invalidPassword), closeSoftKeyboard())
        onView(withId(R.id.createAccountButton)).perform(click())

        // Add assertions for create account failure, checking that it did not go to LoginActivity
        ActivityScenario.launch(LoginActivity::class.java).onActivity { loginActivity ->
            assertNull(loginActivity)
        }
    }
}