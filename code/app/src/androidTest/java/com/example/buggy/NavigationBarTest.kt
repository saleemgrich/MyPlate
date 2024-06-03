package com.example.buggy

import android.app.Activity
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.buggy.activities.DailyNutritionActivity
import com.example.buggy.activities.FriendsActivity
import com.example.buggy.activities.PantryActivity
import com.example.buggy.activities.RecipesActivity
import com.example.buggy.activities.SettingsActivity
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationBarTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(PantryActivity::class.java)

    @Test
    fun testNavigationMenu() {
        // Click on each menu item and verify that the corresponding activity is launched
        clickMenuItem(R.id.menu_item_1, DailyNutritionActivity::class.java)
        clickMenuItem(R.id.menu_item_2, RecipesActivity::class.java)
        clickMenuItem(R.id.menu_item_3, PantryActivity::class.java)
        clickMenuItem(R.id.menu_item_4, FriendsActivity::class.java)
        clickMenuItem(R.id.menu_item_5, SettingsActivity::class.java)
    }
    private fun clickMenuItem(menuItemId: Int, expectedActivity: Class<out Activity>) {
        onView(withId(menuItemId)).perform(click())
        ActivityScenario.launch(expectedActivity).onActivity { activity ->
            assertEquals(expectedActivity, activity::class.java)
        }
    }
}