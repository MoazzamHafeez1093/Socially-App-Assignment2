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
class SignupTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(signup::class.java)

    @Test
    fun testSignupWithValidData() {
        // Enter username
        onView(withId(R.id.userName1))
            .perform(typeText("testuser"), closeSoftKeyboard())

        // Enter email
        onView(withId(R.id.emailEditText))
            .perform(typeText("test@example.com"), closeSoftKeyboard())

        // Enter password
        onView(withId(R.id.passwordEditText))
            .perform(typeText("password123"), closeSoftKeyboard())

        // Click create account button
        onView(withId(R.id.createAccountBtn))
            .perform(click())

        // Verify we navigate to home screen (this would need to be implemented)
        // onView(withId(R.id.homeScreen)).check(matches(isDisplayed()))
    }

    @Test
    fun testSignupWithEmptyFields() {
        // Click create account button without entering data
        onView(withId(R.id.createAccountBtn))
            .perform(click())

        // Verify error message is shown (this would need to be implemented)
        // onView(withText("Please fill in all fields")).check(matches(isDisplayed()))
    }
}
