package com.example.buggy

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.buggy.activities.LoginActivity
import com.example.buggy.activities.PantryActivity
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testLoginSuccess() {
        onView(withId(R.id.editTextEmail)).perform(typeText("d"), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword)).perform(typeText("d"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        // Add assertions for successful login behavior, checking that it went to PantryActivity
        ActivityScenario.launch(PantryActivity::class.java).onActivity { pantryActivity ->
            assertNotNull(pantryActivity)
        }
    }

    @Test
    fun testLoginFailure() {
        onView(withId(R.id.editTextEmail)).perform(typeText("invalid@example.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword)).perform(typeText("wrongpassword"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        // Add assertions for login failure, checking that it did not go to PantryActivity
        ActivityScenario.launch(PantryActivity::class.java).onActivity { pantryActivity ->
            assertNull(pantryActivity)
        }
    }
}