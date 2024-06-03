package com.example.buggy

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.buggy.activities.DietaryRestrictionsActivity
import com.example.buggy.activities.RecipeSearchActivity
import com.example.buggy.activities.RecipesActivity
import junit.framework.TestCase.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipesActivityTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(RecipesActivity::class.java)

    @Test
    fun testRecipeSearchButtonNavigation() {
        // Click on the Recipe Search button
        onView(withId(R.id.recipeSearchButton)).perform(click())
        ActivityScenario.launch(RecipeSearchActivity::class.java).onActivity { activity ->
            assertNotNull(activity)
        }
    }

    @Test
    fun testDietaryRestrictionsButtonNavigation() {
        // Click on the Dietary Restrictions button
        onView(withId(R.id.dietaryRestrictionsButton)).perform(click())
        ActivityScenario.launch(DietaryRestrictionsActivity::class.java).onActivity { activity ->
            assertNotNull(activity)
        }
    }
}