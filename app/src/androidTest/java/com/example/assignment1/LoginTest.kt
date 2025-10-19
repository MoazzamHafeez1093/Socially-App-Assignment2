package com.example.assignment1

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testLoginWithValidCredentials() {
        // Enter email
        onView(withId(R.id.emailTextBox))
            .perform(typeText("test@example.com"), closeSoftKeyboard())

        // Enter password
        onView(withId(R.id.passwordTextBox))
            .perform(typeText("password123"), closeSoftKeyboard())

        // Click login button
        onView(withId(R.id.btnLogin2))
            .perform(click())

        // Verify we navigate to home screen (this would need to be implemented)
        // onView(withId(R.id.homeScreen)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginWithEmptyFields() {
        // Click login button without entering credentials
        onView(withId(R.id.btnLogin2))
            .perform(click())

        // Verify error message is shown (this would need to be implemented)
        // onView(withText("Please fill in all fields")).check(matches(isDisplayed()))
    }
}
