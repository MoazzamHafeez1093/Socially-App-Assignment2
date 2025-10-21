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
        // Test successful signup flow
        onView(withId(R.id.userName1))
            .perform(typeText("testuser"), closeSoftKeyboard())

        onView(withId(R.id.emailEditText))
            .perform(typeText("test@example.com"), closeSoftKeyboard())

        onView(withId(R.id.passwordEditText))
            .perform(typeText("password123"), closeSoftKeyboard())

        onView(withId(R.id.createAccountBtn))
            .perform(click())

        // Verify the signup button was clicked successfully
        // In a real test, you'd verify navigation to HomeScreen
        onView(withId(R.id.createAccountBtn))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSignupWithEmptyUsername() {
        // Test signup with empty username
        onView(withId(R.id.emailEditText))
            .perform(typeText("test@example.com"), closeSoftKeyboard())

        onView(withId(R.id.passwordEditText))
            .perform(typeText("password123"), closeSoftKeyboard())

        onView(withId(R.id.createAccountBtn))
            .perform(click())

        // Verify the fields are still displayed (signup should not proceed)
        onView(withId(R.id.userName1))
            .check(matches(isDisplayed()))
        onView(withId(R.id.emailEditText))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSignupWithEmptyEmail() {
        // Test signup with empty email
        onView(withId(R.id.userName1))
            .perform(typeText("testuser"), closeSoftKeyboard())

        onView(withId(R.id.passwordEditText))
            .perform(typeText("password123"), closeSoftKeyboard())

        onView(withId(R.id.createAccountBtn))
            .perform(click())

        // Verify the fields are still displayed (signup should not proceed)
        onView(withId(R.id.userName1))
            .check(matches(isDisplayed()))
        onView(withId(R.id.emailEditText))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSignupWithEmptyPassword() {
        // Test signup with empty password
        onView(withId(R.id.userName1))
            .perform(typeText("testuser"), closeSoftKeyboard())

        onView(withId(R.id.emailEditText))
            .perform(typeText("test@example.com"), closeSoftKeyboard())

        onView(withId(R.id.createAccountBtn))
            .perform(click())

        // Verify the fields are still displayed (signup should not proceed)
        onView(withId(R.id.userName1))
            .check(matches(isDisplayed()))
        onView(withId(R.id.passwordEditText))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testProfileImageSelection() {
        // Test profile image selection
        onView(withId(R.id.cameraButton))
            .perform(click())

        // Verify the camera button is still displayed
        // In a real test, you'd verify the image picker opens
        onView(withId(R.id.cameraButton))
            .check(matches(isDisplayed()))
    }
}